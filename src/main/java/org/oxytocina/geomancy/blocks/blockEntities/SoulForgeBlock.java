package org.oxytocina.geomancy.blocks.blockEntities;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.AdvancementHelper;

public class SoulForgeBlock extends BlockWithEntity implements BlockEntityProvider {

    private static VoxelShape SHAPE = SoulForgeBlock.createCuboidShape(0,0,0,16,16,16);

    public SoulForgeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulForgeBlockEntity(pos,state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof SoulForgeBlockEntity forge){
                ItemScatterer.spawn(world, pos, forge.getDroppedItems());
                if(!world.isClient)
                    forge.destroy();
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state,world,pos,newState,moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var forge = (SoulForgeBlockEntity) world.getBlockEntity(pos);
        if(forge==null) return ActionResult.PASS;
        if(hand == Hand.OFF_HAND) return ActionResult.PASS;

        // prevent opening if aiming at it with an activate spell
        ItemStack handStack = player.getMainHandStack();
        if(!player.isSneaking() && handStack.getItem() instanceof SoulCastingItem caster){
            var spell = caster.getSelectedSpell(handStack);
            if(spell!=null && spell.containsComponent(SpellBlocks.ACTIVATE) && (world.isClient||!forge.isActive()))
                return ActionResult.PASS;
        }

        if(!world.isClient){
            player.openHandledScreen(forge);
            AdvancementHelper.grantAdvancementCriterion((ServerPlayerEntity)player,"interaction/simple_soulforge","simple_soulforge");
        }

        return ActionResult.CONSUME;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.SOULFORGE_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    public void activate(World world, BlockPos blockPos, SpellContext ctx) {
        var forge = (SoulForgeBlockEntity) world.getBlockEntity(blockPos);
        if(forge==null) return;
        forge.activate(ctx);
    }
}