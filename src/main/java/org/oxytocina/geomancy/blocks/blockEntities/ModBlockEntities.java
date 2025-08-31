package org.oxytocina.geomancy.blocks.blockEntities;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.ModBlocks;

public class ModBlockEntities {

    public static final BlockEntityType<SmitheryBlockEntity> SMITHERY_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,Geomancy.locate("smithery_be"),
            FabricBlockEntityTypeBuilder.create(SmitheryBlockEntity::new,ModBlocks.SMITHERY).build());
    public static final BlockEntityType<SpellmakerBlockEntity> SPELLMAKER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,Geomancy.locate("spellmaker_be"),
            FabricBlockEntityTypeBuilder.create(SpellmakerBlockEntity::new,ModBlocks.SPELLMAKER).build());
    public static final BlockEntityType<AutocasterBlockEntity> AUTOCASTER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,Geomancy.locate("autocaster_be"),
            FabricBlockEntityTypeBuilder.create(AutocasterBlockEntity::new,ModBlocks.AUTOCASTER).build());
    public static final BlockEntityType<SoulForgeBlockEntity> RITUALISTIC_FORGE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,Geomancy.locate("ritualforge_be"),
            FabricBlockEntityTypeBuilder.create(SoulForgeBlockEntity::new,ModBlocks.SOUL_FORGE).build());
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,Geomancy.locate("pedestal_be"),
            FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new,ModBlocks.PEDESTAL).build());

    public static void register() {

    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Geomancy.MOD_ID,id), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}