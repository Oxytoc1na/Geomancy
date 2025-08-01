package org.oxytocina.geomancy.items.tools;

import com.klikli_dev.modonomicon.client.gui.*;
import net.fabricmc.api.*;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.minecraft.stat.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.progression.advancement.ModAdvancementCriterion;
import org.oxytocina.geomancy.util.AdvancementHelper;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.*;

public class GuidebookItem extends Item {

    public static final Identifier GUIDEBOOK_ID = Geomancy.locate("guidebook");

    public GuidebookItem(Settings settings) {
        super(settings);
    }


    private static final Set<UUID> alreadyReprocessedPlayers = new HashSet<>();

    public static void reprocessAdvancementUnlocks(ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity.getServer() == null) {
            return;
        }
        MinecraftServer server = serverPlayerEntity.getServer();

        UUID uuid = serverPlayerEntity.getUuid();
        if (alreadyReprocessedPlayers.contains(uuid)) {
            return;
        }
        alreadyReprocessedPlayers.add(uuid);

        PlayerAdvancementTracker tracker = serverPlayerEntity.getAdvancementTracker();

        for (Advancement advancement : serverPlayerEntity.getServer().getAdvancementLoader().getAdvancements()) {
            AdvancementProgress hasAdvancement = tracker.getProgress(advancement);
            if (!hasAdvancement.isDone()) {
                for (Map.Entry<String, AdvancementCriterion> criterionEntry : advancement.getCriteria().entrySet()) {
                    CriterionConditions conditions = criterionEntry.getValue().getConditions();
                    if (conditions != null && conditions.getId().equals(ModAdvancementCriterion.getID()) && conditions instanceof ModAdvancementCriterion.Conditions hasAdvancementConditions) {
                        Advancement advancementCriterionAdvancement = server.getAdvancementLoader().get(hasAdvancementConditions.getAdvancement());
                        if (advancementCriterionAdvancement != null) {
                            AdvancementProgress hasAdvancementCriterionAdvancement = tracker.getProgress(advancementCriterionAdvancement);
                            if (hasAdvancementCriterionAdvancement.isDone()) {
                                tracker.grantCriterion(advancement, criterionEntry.getKey());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            // if the player has never opened the book before
            // automatically open the introduction page
            openGuidebook();

            /** TODO: this can be removed by putting
             * "entry_to_open": "spectrum:general/intro",
             * "open_entry_to_open_only_once": true,
             *
             * in the general category entry in 1.21
             * https://klikli-dev.github.io/modonomicon/docs/basics/structure/categories
             */
            if (!hasOpenedGuidebookBefore()) {
                openGuidebook(Geomancy.locate("general/intro"), 0);
            }
        } else if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            reprocessAdvancementUnlocks(serverPlayerEntity);

            AdvancementHelper.grantAdvancementCriterion(serverPlayerEntity, "hidden/opened_guidebook", "opened_guidebook");
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }

    /**
     * If clientside and the client does not have stats synced yet (not opened the stats screen)
     * this is always false 💀
     */
    @Environment(EnvType.CLIENT)
    private boolean hasOpenedGuidebookBefore() {
        return true; //ClientAdvancements.hasDone(Geomancy.locate("hidden/opened_guidebook"));
    }

    public void openGuidebook() {
        BookGuiManager.get().openBook(GUIDEBOOK_ID);
    }

    public void openGuidebook(Identifier entry, int page) {
        BookGuiManager.get().openEntry(GUIDEBOOK_ID, entry, page);
    }

}
