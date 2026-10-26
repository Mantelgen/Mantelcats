package com.mantelgen.mantelcats.content.copycat.slice;

import com.mantelgen.mantelcats.CCBlocks;
import com.mantelgen.mantelcats.CCShapes;
import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlock;
import com.mantelgen.mantelcats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.multistate.MaterialItemStorage;
import com.mantelgen.mantelcats.foundation.copycat.multistate.WaterloggedMultiStateCopycatBlock;
import com.mantelgen.mantelcats.utility.BlockUtils;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A stair-step wedge copycat that occupies a 2L x 2L square on a wall, where L is the
 * {@link #LAYERS} value. The square is subdivided into four L-sized cells arranged in a
 * 2x2 grid across the y (bottom/top) and depth (wall/outer) axes. Each cell carries an
 * independent material, named by its position in the canonical SOUTH-facing, BOTTOM-half
 * orientation.
 * <p>
 * This class also hosts the shared transform machinery for the slice family
 * (slice, vertical slice and corner slice), mirroring how
 * {@link com.mantelgen.mantelcats.content.copycat.half_layer.CopycatHalfLayerBlock}
 * hosts the machinery for the layer family.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CopycatSliceBlock extends WaterloggedMultiStateCopycatBlock implements SpecialBlockItemRequirement {

    public static final String BOTTOM_SOUTH = "bottom_south";
    public static final String BOTTOM_NORTH = "bottom_north";
    public static final String TOP_SOUTH = "top_south";
    public static final String TOP_NORTH = "top_north";
    private static final List<String> PROPERTIES = List.of(BOTTOM_SOUTH, TOP_SOUTH, BOTTOM_NORTH, TOP_NORTH);

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    public CopycatSliceBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState()
                .setValue(HALF, Half.BOTTOM)
                .setValue(FACING, Direction.SOUTH)
                .setValue(LAYERS, 1)
        );
    }

    @Override
    public String defaultProperty() {
        return BOTTOM_SOUTH;
    }

    @Override
    public Vec3i vectorScale(BlockState state) {
        return new Vec3i(1, 2, 2);
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
            case TOP_SOUTH -> new Vec3i(0, 1, 1);
            case TOP_NORTH -> new Vec3i(0, 1, 0);
            case BOTTOM_NORTH -> Vec3i.ZERO;
            default -> new Vec3i(0, 0, 1);
        };
    }

    @Override
    public String getPropertyFromRender(String renderingProperty, BlockState state, BlockGetter level, Vec3i vector, BlockPos blockPos) {
        return renderingProperty;
    }

    /**
     * The canonical-orientation cell box (block-space, in sixteenths) of the given part.
     */
    private static AABB canonicalCellBox(String property, int layers) {
        int l = layers;
        return switch (property) {
            case TOP_SOUTH -> new AABB(0, l, 16 - l, 16, 2 * l, 16);
            case TOP_NORTH -> new AABB(0, l, 16 - 2 * l, 16, 2 * l, 16 - l);
            case BOTTOM_NORTH -> new AABB(0, 0, 16 - 2 * l, 16, l, 16 - l);
            default -> new AABB(0, 0, 16 - l, 16, l, 16);
        };
    }

    @Override
    public String getPropertyFromInteraction(BlockState state, BlockGetter level, Vec3i hitLocation, BlockPos blockPos, Direction facing, Vec3 unscaledHit) {
        int layers = state.getValue(LAYERS);
        Vec3 hit = unorientPoint(state.getValue(FACING), state.getValue(HALF), unscaledHit.scale(16));
        return hit.y < layers ? (hit.z >= 16 - layers ? BOTTOM_SOUTH : BOTTOM_NORTH)
                : (hit.z >= 16 - layers ? TOP_SOUTH : TOP_NORTH);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(HALF, FACING, LAYERS));
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pType) {
        return switch (pType) {
            case LAND -> pState.getValue(LAYERS) < 5;
            default -> false;
        };
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
            Direction facing = context.getHorizontalDirection();
            if (facing.getAxis() == Axis.X) {
                if ((facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) !=
                        (context.getClickLocation().x - context.getClickedPos().getX() > 0.5))
                    facing = facing.getOpposite();
            } else {
                if ((facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) !=
                        (context.getClickLocation().z - context.getClickedPos().getZ() > 0.5))
                    facing = facing.getOpposite();
            }
            stateForPlacement = stateForPlacement.setValue(FACING, facing);
            Direction direction = context.getClickedFace();
            if (direction == Direction.UP)
                return stateForPlacement;
            if (direction == Direction.DOWN || (context.getClickLocation().y - context.getClickedPos()
                    .getY() > 0.5D))
                return stateForPlacement.setValue(HALF, Half.TOP);
            return stateForPlacement;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(@NotNull BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        if (!itemstack.is(this.asItem())) return false;
        if (pState.getValue(LAYERS) == 8) return false;
        Half half = pState.getValue(HALF);
        if (half == Half.TOP && pUseContext.getClickedFace() == Direction.DOWN || half == Half.BOTTOM && pUseContext.getClickedFace() == Direction.UP)
            return true;
        if (pUseContext.getClickedFace() == pState.getValue(FACING).getOpposite())
            return true;
        return false;
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return CCShapes.SLICE.get(pState.getValue(FACING)).get(pState.getValue(HALF)).get(pState.getValue(LAYERS)).toShape();
    }

    @Override
    public VoxelShape getPartialFaceShape(BlockGetter level, BlockState state, String property, Direction face) {
        if (!partExists(state, property)) return net.minecraft.world.phys.shapes.Shapes.empty();
        AABB box = orientedCellBox(state, property);
        return partialFaceShapePublic(box, face);
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
        return BlockUtils.transformStepLikeHorizontal(state, transform, CCBlocks.COPYCAT_VERTICAL_SLICE.getDefaultState());
    }

    @Override
    public void transformStorage(BlockState state, IMultiStateCopycatBlockEntity be, StructureTransform transform) {
        remapSliceStorage(state, be, transform);
    }

    // ============ Shared slice-family transform machinery ============

    // Populated lazily: CCBlocks entries do not exist yet while this class is being
    // constructed during block registration.
    private static final Map<Block, List<String>> FAMILY_PROPERTIES = new HashMap<>();

    private static Map<Block, List<String>> familyProperties() {
        if (FAMILY_PROPERTIES.isEmpty()) {
            FAMILY_PROPERTIES.put(CCBlocks.COPYCAT_SLICE.get(), PROPERTIES);
            FAMILY_PROPERTIES.put(CCBlocks.COPYCAT_VERTICAL_SLICE.get(),
                    com.mantelgen.mantelcats.content.copycat.vertical_slice.CopycatVerticalSliceBlock.PROPERTIES);
            FAMILY_PROPERTIES.put(CCBlocks.COPYCAT_CORNER_SLICE.get(),
                    com.mantelgen.mantelcats.content.copycat.corner_slice.CopycatCornerSliceBlock.PROPERTIES);
        }
        return FAMILY_PROPERTIES;
    }

    /**
     * The canonical-orientation cell box (16-space) of the given part for any slice-family block.
     */
    public static AABB canonicalCellBoxOf(Block block, String property, int layers) {
        if (block instanceof CopycatSliceBlock) return canonicalCellBox(property, layers);
        if (block instanceof com.mantelgen.mantelcats.content.copycat.vertical_slice.CopycatVerticalSliceBlock)
            return com.mantelgen.mantelcats.content.copycat.vertical_slice.CopycatVerticalSliceBlock.canonicalCellBox(property, layers);
        return com.mantelgen.mantelcats.content.copycat.corner_slice.CopycatCornerSliceBlock.canonicalCellBox(property, layers);
    }

    /**
     * Rotate a 16-space point clockwise around the block center (x, z) by {@code steps} quarter turns.
     */
    private static Vec3 rotatePoint(Vec3 v, int steps) {
        for (int i = 0; i < ((steps % 4) + 4) % 4; i++) {
            v = new Vec3(16 - v.z, v.y, v.x);
        }
        return v;
    }

    /**
     * Apply the block's orientation (facing yaw and optional half flip) to a canonical-space point.
     */
    public static Vec3 orientPoint(Direction facing, @Nullable Half half, Vec3 canonical) {
        Vec3 v = canonical;
        if (half == Half.TOP) v = new Vec3(v.x, 16 - v.y, v.z);
        int steps = Math.round(facing.toYRot() / 90f);
        return rotatePoint(v, steps);
    }

    /**
     * Remove the block's orientation from a physical-space point, yielding canonical coordinates.
     */
    public static Vec3 unorientPoint(Direction facing, @Nullable Half half, Vec3 physical) {
        int steps = Math.round(facing.toYRot() / 90f);
        Vec3 v = rotatePoint(physical, 4 - (steps % 4));
        if (half == Half.TOP) v = new Vec3(v.x, 16 - v.y, v.z);
        return v;
    }

    /**
     * Equivalent to {@link StructureTransform#unapplyWithoutOffset(Vec3)} but reimplemented to
     * avoid the crash the codebase works around on older Create versions, matching
     * {@link com.mantelgen.mantelcats.foundation.copycat.CopycatTransformableState.Part}.
     */
    private static Vec3 unapplyWithoutOffset(StructureTransform transform, Vec3 globalVec) {
        Vec3 vec = globalVec;
        if (transform.rotationAxis != null)
            vec = VecHelper.rotateCentered(vec, -transform.angle, transform.rotationAxis);
        if (transform.mirror != null)
            vec = VecHelper.mirrorCentered(vec, transform.mirror);
        return vec;
    }

    private static AABB rotateBox(AABB box, int steps) {
        Vec3 min = rotatePoint(new Vec3(box.minX, box.minY, box.minZ), steps);
        Vec3 max = rotatePoint(new Vec3(box.maxX, box.maxY, box.maxZ), steps);
        return new AABB(min, max);
    }

    private static Direction facingOf(BlockState state) {
        return state.getValue(FACING);
    }

    private static @Nullable Half halfOf(BlockState state) {
        return state.hasProperty(HALF) ? state.getValue(HALF) : null;
    }

    /**
     * The cell box of the given part in the block's current orientation, in 16-space.
     */
    public static AABB orientedCellBox(BlockState state, String property) {
        int layers = state.getValue(LAYERS);
        AABB box = canonicalCellBoxOf(state.getBlock(), property, layers);
        Half half = halfOf(state);
        if (half == Half.TOP) box = new AABB(box.minX, 16 - box.maxY, box.minZ, box.maxX, 16 - box.minY, box.maxZ);
        return rotateBox(box, Math.round(facingOf(state).toYRot() / 90f));
    }

    /**
     * The cell's representative anchor point in canonical space: its center.
     */
    private static Vec3 canonicalAnchor(Block block, String property, int layers) {
        AABB box = canonicalCellBoxOf(block, property, layers);
        return box.getCenter();
    }

    /**
     * Intersect an oriented cell box (16-space) with a block face plane, producing a normalized shape.
     */
    public static VoxelShape partialFaceShapePublic(AABB box, Direction face) {
        Direction.Axis axis = face.getAxis();
        double min = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 15d / 16 : 0;
        double max = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 1d / 16;
        double x1 = axis == Direction.Axis.X ? min : box.minX / 16;
        double x2 = axis == Direction.Axis.X ? max : box.maxX / 16;
        double y1 = axis == Direction.Axis.Y ? min : box.minY / 16;
        double y2 = axis == Direction.Axis.Y ? max : box.maxY / 16;
        double z1 = axis == Direction.Axis.Z ? min : box.minZ / 16;
        double z2 = axis == Direction.Axis.Z ? max : box.maxZ / 16;
        if (x1 >= x2 || y1 >= y2 || z1 >= z2) return net.minecraft.world.phys.shapes.Shapes.empty();
        return net.minecraft.world.phys.shapes.Shapes.create(new AABB(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Which canonical part of the given state owns the given canonical-space point (nearest cell).
     */
    private static String nearestProperty(BlockState state, Vec3 canonical) {
        int layers = state.getValue(LAYERS);
        String best = null;
        double bestDist = Double.MAX_VALUE;
        for (String property : familyProperties().get(state.getBlock())) {
            double dist = canonicalCellBoxOf(state.getBlock(), property, layers).distanceToSqr(canonical);
            if (dist < bestDist) {
                bestDist = dist;
                best = property;
            }
        }
        return best;
    }

    /**
     * The family block's storage property list, or null for non-family blocks.
     */
    public static @Nullable List<String> storagePropsOf(Block block) {
        return familyProperties().get(block);
    }

    /**
     * Classify a set of family part anchors (16-space points with associated LAYERS data) into the
     * matching block state: which slice type, which facing, which half. The anchors are the oriented
     * cell centers of a family state, so their layout identifies the family member:
     * all at y=8 means a vertical slice, all sharing x=8 or z=8 means a slice, otherwise a corner slice.
     */
    public static BlockState fromTransformableAnchors(BlockState template, List<Vec3> anchors, int layers) {
        boolean allY8 = anchors.stream().allMatch(v -> Math.abs(v.y - 8) < 0.51);
        boolean allX8 = anchors.stream().allMatch(v -> Math.abs(v.x - 8) < 0.51);
        boolean allZ8 = anchors.stream().allMatch(v -> Math.abs(v.z - 8) < 0.51);
        double meanY = anchors.stream().mapToDouble(v -> v.y).average().orElse(8);
        double meanX = anchors.stream().mapToDouble(v -> v.x).average().orElse(8);
        double meanZ = anchors.stream().mapToDouble(v -> v.z).average().orElse(8);

        if (allY8) {
            // vertical slice: column corner from the anchors' horizontal extreme
            Direction xSide = meanX > 8 ? Direction.EAST : Direction.WEST;
            Direction zSide = meanZ > 8 ? Direction.SOUTH : Direction.NORTH;
            Direction facing = cornerFacing(xSide, zSide);
            return BlockUtils.tryCopyProperties(template, CCBlocks.COPYCAT_VERTICAL_SLICE.getDefaultState())
                    .setValue(FACING, facing)
                    .setValue(LAYERS, layers);
        } else if (allX8 || allZ8) {
            // slice: wall side from the dominant horizontal offset of the anchor cloud
            Direction facing;
            if (allX8) {
                facing = meanZ > 8 ? Direction.SOUTH : Direction.NORTH;
            } else {
                facing = meanX > 8 ? Direction.EAST : Direction.WEST;
            }
            Half half = meanY > 8 ? Half.TOP : Half.BOTTOM;
            return BlockUtils.tryCopyProperties(template, CCBlocks.COPYCAT_SLICE.getDefaultState())
                    .setValue(FACING, facing)
                    .setValue(HALF, half)
                    .setValue(LAYERS, layers);
        } else {
            // corner slice: junction corner from horizontal extremes, half from mean height
            Direction xSide = meanX > 8 ? Direction.EAST : Direction.WEST;
            Direction zSide = meanZ > 8 ? Direction.SOUTH : Direction.NORTH;
            Half half = meanY > 8 ? Half.TOP : Half.BOTTOM;
            return BlockUtils.tryCopyProperties(template, CCBlocks.COPYCAT_CORNER_SLICE.getDefaultState())
                    .setValue(FACING, cornerFacing(xSide, zSide))
                    .setValue(HALF, half)
                    .setValue(LAYERS, layers);
        }
    }

    private static Direction cornerFacing(Direction xSide, Direction zSide) {
        if (xSide == Direction.EAST) {
            return zSide == Direction.SOUTH ? Direction.SOUTH : Direction.EAST;
        }
        return zSide == Direction.SOUTH ? Direction.WEST : Direction.NORTH;
    }

    /**
     * The oriented anchor points of the given state's parts, in storage-property order.
     */
    public static Map<String, Vec3> orientedAnchors(BlockState state) {
        int layers = state.getValue(LAYERS);
        Direction facing = facingOf(state);
        Half half = halfOf(state);
        Map<String, Vec3> anchors = new HashMap<>();
        for (String property : familyProperties().get(state.getBlock())) {
            anchors.put(property, orientPoint(facing, half, canonicalAnchor(state.getBlock(), property, layers)));
        }
        return anchors;
    }

    /**
     * Recover the pre-transform state from a post-transform state by un-transforming its part anchors.
     */
    public static BlockState untransformState(BlockState newState, StructureTransform transform) {
        Map<String, Vec3> anchors = orientedAnchors(newState);
        int layers = newState.getValue(LAYERS);
        List<Vec3> oldAnchors = anchors.values().stream()
                .map(v -> unapplyWithoutOffset(transform, v.scale(1 / 16d)).scale(16))
                .toList();
        return fromTransformableAnchors(newState, oldAnchors, layers);
    }

    /**
     * Remap storage keys for a slice-family transform: each part's anchor is carried through the
     * transform and assigned to the new state's nearest canonical cell.
     */
    public static void remapSliceStorage(BlockState newState, IMultiStateCopycatBlockEntity be, StructureTransform transform) {
        MaterialItemStorage storage = be.getMaterialItemStorage();
        if (storage == null) return;
        List<String> newProps = familyProperties().get(newState.getBlock());
        if (newProps == null) return;
        BlockState oldState = untransformState(newState, transform);
        List<String> oldProps = familyProperties().get(oldState.getBlock());
        if (oldProps == null) return;
        Map<String, String> mapping = new HashMap<>();
        Map<String, Vec3> anchors = orientedAnchors(oldState);
        for (String oldProp : oldProps) {
            Vec3 anchor = anchors.get(oldProp);
            Vec3 moved = transform.applyWithoutOffset(anchor.scale(1 / 16d)).scale(16);
            Vec3 canonical = unorientPoint(facingOf(newState), halfOf(newState), moved);
            mapping.put(oldProp, nearestProperty(newState, canonical));
        }
        storage.remapStorage(key -> mapping.getOrDefault(key, key));
    }
}
