package com.mantelgen.mantelcats.content.copycat.vertical_slice;

import com.mantelgen.mantelcats.CCBlocks;
import com.mantelgen.mantelcats.CCShapes;
import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.content.copycat.slice.CopycatSliceBlock;
import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlock;
import com.mantelgen.mantelcats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.multistate.WaterloggedMultiStateCopycatBlock;
import com.mantelgen.mantelcats.utility.BlockUtils;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraft.core.Direction.Axis;

/**
 * A vertical column copycat that occupies a 2L x 2L square column at a horizontal block
 * edge, where L is the {@link #LAYERS} value. The column is subdivided into four L-sized
 * cells arranged in a 2x2 grid across the x and z axes. Each cell carries an independent
 * material, named by its position in the canonical SOUTH-facing orientation (column in
 * the south-east corner), so the two wall-adjacent columns of cells can carry different
 * materials for corner trims.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CopycatVerticalSliceBlock extends WaterloggedMultiStateCopycatBlock implements SpecialBlockItemRequirement {

    public static final String SOUTH_EAST = "southeast";
    public static final String SOUTH_WEST = "southwest";
    public static final String NORTH_EAST = "northeast";
    public static final String NORTH_WEST = "northwest";
    public static final List<String> PROPERTIES = List.of(SOUTH_EAST, SOUTH_WEST, NORTH_EAST, NORTH_WEST);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;


    public CopycatVerticalSliceBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(LAYERS, 1)
        );
    }

    @Override
    public String defaultProperty() {
        return SOUTH_EAST;
    }

    @Override
    public Vec3i vectorScale(BlockState state) {
        return new Vec3i(2, 1, 2);
    }

    @Override
    public Set<String> storageProperties() {
        return Set.copyOf(PROPERTIES);
    }

    @Override
    public int getColorIndex(String property) {
        return PROPERTIES.indexOf(property);
    }

    @Override
    public boolean partExists(BlockState state, String property) {
        return PROPERTIES.contains(property);
    }

    @Override
    public Vec3i getVectorFromProperty(BlockState state, String property) {
        return switch (property) {
            case SOUTH_EAST -> new Vec3i(1, 0, 1);
            case NORTH_EAST -> new Vec3i(1, 0, 0);
            case SOUTH_WEST -> new Vec3i(0, 0, 1);
            default -> Vec3i.ZERO;
        };
    }

    @Override
    public String getPropertyFromRender(String renderingProperty, BlockState state, BlockGetter level, Vec3i vector, BlockPos blockPos) {
        return renderingProperty;
    }

    /**
     * The canonical-orientation cell box (block-space, in sixteenths) of the given part.
     */
    public static AABB canonicalCellBox(String property, int layers) {
        int l = layers;
        return switch (property) {
            case NORTH_EAST -> new AABB(16 - l, 0, 16 - 2 * l, 16, 16, 16 - l);
            case SOUTH_WEST -> new AABB(16 - 2 * l, 0, 16 - l, 16 - l, 16, 16);
            case NORTH_WEST -> new AABB(16 - 2 * l, 0, 16 - 2 * l, 16 - l, 16, 16 - l);
            default -> new AABB(16 - l, 0, 16 - l, 16, 16, 16);
        };
    }

    @Override
    public String getPropertyFromInteraction(BlockState state, BlockGetter level, Vec3i hitLocation, BlockPos blockPos, Direction facing, Vec3 unscaledHit) {
        int layers = state.getValue(LAYERS);
        Vec3 hit = CopycatSliceBlock.unorientPoint(state.getValue(FACING), null, unscaledHit.scale(16));
        boolean east = hit.x >= 16 - layers;
        boolean south = hit.z >= 16 - layers;
        if (east) return south ? SOUTH_EAST : NORTH_EAST;
        return south ? SOUTH_WEST : NORTH_WEST;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pType) {
        return false;
    }

    private static final Map<Pair<Integer, Integer>, Direction> VERTICAL_POSITION_MAP = new HashMap<>();
    private static final Map<Pair<Direction, Integer>, Direction> HORIZONTAL_POSITION_MAP = new HashMap<>();

    static {
        for (Direction main : Iterate.horizontalDirections) {
            Direction cross = main.getCounterClockWise();

            int mainOffset = main.getAxisDirection().getStep();
            int crossOffset = cross.getAxisDirection().getStep();

            if (main.getAxis() == Axis.X)
                VERTICAL_POSITION_MAP.put(Pair.of(mainOffset, crossOffset), main);
            else
                VERTICAL_POSITION_MAP.put(Pair.of(crossOffset, mainOffset), main);

            HORIZONTAL_POSITION_MAP.put(Pair.of(main.getOpposite(), crossOffset), main);
            HORIZONTAL_POSITION_MAP.put(Pair.of(cross.getOpposite(), mainOffset), main);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement == null) return null;
        BlockPos blockPos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(blockPos);
        if (state.is(this)) {
            if (state.getValue(LAYERS) < 8)
                return state.cycle(LAYERS);
            else {
                Copycats.LOGGER.warn("Can't figure out where to place a slice! Please file an issue if you see this.");
                return state;
            }
        } else {
            int xOffset = context.getClickLocation().x - context.getClickedPos().getX() > 0.5 ? 1 : -1;
            int zOffset = context.getClickLocation().z - context.getClickedPos().getZ() > 0.5 ? 1 : -1;

            if (context.getClickedFace().getAxis() == Axis.Y) {
                return stateForPlacement.setValue(FACING, VERTICAL_POSITION_MAP.get(Pair.of(xOffset, zOffset)));
            } else {
                return stateForPlacement.setValue(FACING, HORIZONTAL_POSITION_MAP.get(
                        Pair.of(context.getClickedFace(), context.getClickedFace().getAxis() == Axis.X ? zOffset : xOffset)
                ));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(@NotNull BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        if (!itemstack.is(this.asItem())) return false;
        if (pState.getValue(LAYERS) == 8) return false;
        Direction facing = pState.getValue(FACING);
        return pUseContext.getClickedFace() == facing.getOpposite() || pUseContext.getClickedFace() == facing.getClockWise();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (state.getValue(LAYERS) <= 1)
            return super.onSneakWrenched(state, context);

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world instanceof ServerLevel serverLevel) {
            if (player != null && !player.isCreative()) {
                // Respect loot tables
                List<ItemStack> drops = Block.getDrops(state.setValue(LAYERS, 1), serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand());
                for (ItemStack drop : drops) {
                    player.getInventory().placeItemBackInInventory(drop);
                }
            }
            BlockPos up = pos.relative(Direction.UP);
            // need to call updateShape before setBlock to schedule a tick for water
            world.setBlockAndUpdate(pos, state.setValue(LAYERS, state.getValue(LAYERS) - 1).updateShape(Direction.UP, world.getBlockState(up), world, pos, up));
            IWrenchable.playRemoveSound(world, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        return ICopycatBlock.getRequiredItemsForLayer(state, LAYERS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING, LAYERS));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return CCShapes.VERTICAL_SLICE.get(pState.getValue(FACING)).get(pState.getValue(LAYERS)).toShape();
    }

    @Override
    public VoxelShape getPartialFaceShape(BlockGetter level, BlockState state, String property, Direction face) {
        if (!partExists(state, property)) return net.minecraft.world.phys.shapes.Shapes.empty();
        return CopycatSliceBlock.partialFaceShapePublic(CopycatSliceBlock.orientedCellBox(state, property), face);
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    public boolean hidesNeighborFace(BlockGetter level,
                                     BlockPos pos,
                                     BlockState state,
                                     BlockState neighborState,
                                     Direction dir) {
        return ICopycatBlock.hidesNeighborFace(level, pos, state, neighborState, dir);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return BlockUtils.transformStepLikeVertical(state, transform, CCBlocks.COPYCAT_SLICE.getDefaultState());
    }

    @Override
    public void transformStorage(BlockState state, IMultiStateCopycatBlockEntity be, StructureTransform transform) {
        CopycatSliceBlock.remapSliceStorage(state, be, transform);
    }
}
