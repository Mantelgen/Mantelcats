package com.copycatsplus.copycats.content.copycat.corner_panel;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.WaterloggedMultiStateCopycatBlock;
import com.google.common.collect.ImmutableMap;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An L-shaped copycat panel that fills the inside corner between two perpendicular faces.
 * Two parts: the arm lying on the z-facing wall ({@code plate_z}) and the arm lying on the
 * x-facing wall ({@code plate_x}) in the canonical {@link Corner#NORTH_EAST} orientation, so
 * each arm can carry a different material. Part names travel with the physical arm under
 * rotation, so no storage remapping is needed.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CopycatCornerPanelBlock extends WaterloggedMultiStateCopycatBlock {
    public static final String PLATE_Z = "plate_z";
    public static final String PLATE_X = "plate_x";
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);

    private static final int THICKNESS = 3;

    private final ImmutableMap<BlockState, VoxelShape> shapesCache;
    private final ImmutableMap<FaceData, VoxelShape> partialFaceCache;

    private record FaceData(Corner corner, String property, Direction face) {
    }

    public enum Corner implements StringRepresentable {
        NORTH_EAST(Direction.EAST, Direction.NORTH),
        SOUTH_EAST(Direction.EAST, Direction.SOUTH),
        SOUTH_WEST(Direction.WEST, Direction.SOUTH),
        NORTH_WEST(Direction.WEST, Direction.NORTH);

        // Clockwise rotation order around Y: NORTH_EAST -> SOUTH_EAST -> SOUTH_WEST -> NORTH_WEST
        private final Direction xDir;
        private final Direction zDir;

        Corner(Direction xDir, Direction zDir) {
            this.xDir = xDir;
            this.zDir = zDir;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public Direction xDir() {
            return xDir;
        }

        public Direction zDir() {
            return zDir;
        }

        public static Corner of(Direction xDir, Direction zDir) {
            for (Corner corner : values()) {
                if (corner.xDir == xDir && corner.zDir == zDir) return corner;
            }
            return NORTH_EAST;
        }
    }

    public CopycatCornerPanelBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CORNER, Corner.NORTH_EAST));
        this.shapesCache = this.getShapeForEachState(CopycatCornerPanelBlock::calculateShape);
        ImmutableMap.Builder<FaceData, VoxelShape> builder = ImmutableMap.builder();
        for (Corner corner : Corner.values()) {
            for (String property : storageProperties()) {
                for (Direction face : Direction.values()) {
                    builder.put(new FaceData(corner, property, face), partialFaceShape(corner, property, face));
                }
            }
        }
        this.partialFaceCache = builder.build();
    }

    @Override
    public String defaultProperty() {
        return PLATE_Z;
    }

    @Override
    public Vec3i vectorScale(BlockState state) {
        return new Vec3i(1, 1, 1);
    }

    @Override
    public Set<String> storageProperties() {
        return Set.of(PLATE_Z, PLATE_X);
    }

    @Override
    public int getColorIndex(String property) {
        return property.equals(PLATE_X) ? 1 : 0;
    }

    @Override
    public boolean partExists(BlockState state, String property) {
        return property.equals(PLATE_Z) || property.equals(PLATE_X);
    }

    @Override
    public Vec3i getVectorFromProperty(BlockState state, String property) {
        return Vec3i.ZERO;
    }

    @Override
    public String getPropertyFromRender(String renderingProperty, BlockState state, BlockGetter level, Vec3i vector, BlockPos blockPos) {
        return renderingProperty;
    }

    /**
     * The canonical-orientation arm box (block-space, in sixteenths) of the given part.
     */
    private static net.minecraft.world.phys.AABB canonicalArmBox(String property) {
        if (property.equals(PLATE_Z)) return new net.minecraft.world.phys.AABB(8, 0, 0, 16, 16, THICKNESS);
        return new net.minecraft.world.phys.AABB(16 - THICKNESS, 0, THICKNESS, 16, 16, 8);
    }

    /**
     * The arm box of the given part in the given corner orientation, in sixteenths.
     */
    private static net.minecraft.world.phys.AABB armBox16(Corner corner, String property) {
        net.minecraft.world.phys.AABB box = canonicalArmBox(property);
        // Rotate the canonical (NORTH_EAST) box clockwise around Y by the corner ordinal.
        for (int i = 0; i < corner.ordinal(); i++) {
            box = new net.minecraft.world.phys.AABB(16 - box.maxZ, box.minY, box.minX, 16 - box.minZ, box.maxY, box.maxX);
        }
        return box;
    }

    /**
     * The arm box of the given part in the given corner orientation, in block units [0,1].
     */
    private static net.minecraft.world.phys.AABB armBox(Corner corner, String property) {
        net.minecraft.world.phys.AABB box = armBox16(corner, property);
        return new net.minecraft.world.phys.AABB(
                box.minX / 16, box.minY / 16, box.minZ / 16,
                box.maxX / 16, box.maxY / 16, box.maxZ / 16);
    }

    /**
     * The wall face that a part's outer surface lies on for the given corner. Both arms keep
     * their canonical assignments under rotation, so the wall is derived from the arm's thin
     * axis: {@code plate_z} rotates north -> east -> south -> west, {@code plate_x} east ->
     * south -> west -> north as the corner goes NORTH_EAST -> SOUTH_EAST -> SOUTH_WEST ->
     * NORTH_WEST.
     */
    private static Direction plateWall(Corner corner, String property) {
        net.minecraft.world.phys.AABB box = armBox16(corner, property);
        if (box.maxX - box.minX <= THICKNESS)
            return box.minX == 0 ? Direction.WEST : Direction.EAST;
        return box.minZ == 0 ? Direction.NORTH : Direction.SOUTH;
    }

    @Override
    public String getPropertyFromInteraction(BlockState state, BlockGetter level, Vec3i hitLocation, BlockPos blockPos, Direction facing, Vec3 unscaledHit) {
        Corner corner = state.getValue(CORNER);
        Direction wallZ = plateWall(corner, PLATE_Z);
        Direction wallX = plateWall(corner, PLATE_X);
        if (facing == wallZ) return PLATE_Z;
        if (facing == wallX) return PLATE_X;
        // Clicked a face the panel does not cover: pick the nearest arm.
        double distZ = armBox(corner, PLATE_Z).distanceToSqr(unscaledHit);
        double distX = armBox(corner, PLATE_X).distanceToSqr(unscaledHit);
        return distZ <= distX ? PLATE_Z : PLATE_X;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(CORNER));
    }

    private static VoxelShape calculateShape(BlockState pState) {
        Corner corner = pState.getValue(CORNER);
        VoxelShape shape = Shapes.empty();
        for (String property : Set.of(PLATE_Z, PLATE_X)) {
            net.minecraft.world.phys.AABB box = armBox(corner, property);
            shape = Shapes.joinUnoptimized(shape, Shapes.create(box), BooleanOp.OR);
        }
        return shape.optimize();
    }

    private static VoxelShape partialFaceShape(Corner corner, String property, Direction face) {
        net.minecraft.world.phys.AABB box = armBox(corner, property);
        // Intersect the arm with the block face plane to get the part's coverage on that face.
        Direction.Axis axis = face.getAxis();
        double min = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 15d / 16 : 0;
        double max = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 1d / 16;
        double x1 = axis == Direction.Axis.X ? min : box.minX;
        double x2 = axis == Direction.Axis.X ? max : box.maxX;
        double y1 = axis == Direction.Axis.Y ? min : box.minY;
        double y2 = axis == Direction.Axis.Y ? max : box.maxY;
        double z1 = axis == Direction.Axis.Z ? min : box.minZ;
        double z2 = axis == Direction.Axis.Z ? max : box.maxZ;
        if (x1 >= x2 || y1 >= y2 || z1 >= z2) return Shapes.empty();
        return Shapes.create(new net.minecraft.world.phys.AABB(x1, y1, z1, x2, y2, z2));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return Objects.requireNonNull(this.shapesCache.get(pState));
    }

    @Override
    public VoxelShape getPartialFaceShape(BlockGetter level, BlockState state, String property, Direction face) {
        if (!partExists(state, property)) return Shapes.empty();
        return Objects.requireNonNull(partialFaceCache.getOrDefault(
                new FaceData(state.getValue(CORNER), property, face),
                Shapes.empty()
        ));
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull PathComputationType pType) {
        return pType == PathComputationType.LAND;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        if (stateForPlacement == null) return null;
        // The corner snaps to the vertical block edge nearest the click.
        Vec3 hit = context.getClickLocation();
        BlockPos pos = context.getClickedPos();
        Direction xDir = hit.x - pos.getX() >= 0.5 ? Direction.EAST : Direction.WEST;
        Direction zDir = hit.z - pos.getZ() >= 0.5 ? Direction.SOUTH : Direction.NORTH;
        return stateForPlacement.setValue(CORNER, Corner.of(xDir, zDir));
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        Corner corner = state.getValue(CORNER);
        Direction xDir = corner.xDir();
        Direction zDir = corner.zDir();
        if (transform.mirror != null && transform.mirror != Mirror.NONE) {
            xDir = transform.mirror.mirror(xDir);
            zDir = transform.mirror.mirror(zDir);
        }
        if (transform.rotation != null && transform.rotation != Rotation.NONE
                && (transform.rotationAxis == null || transform.rotationAxis.isVertical())) {
            xDir = transform.rotation.rotate(xDir);
            zDir = transform.rotation.rotate(zDir);
        }
        return state.setValue(CORNER, Corner.of(xDir, zDir));
    }

    @Override
    public void transformStorage(BlockState state, IMultiStateCopycatBlockEntity be, StructureTransform transform) {
        // Part names are bound to the arm, not the wall, so materials travel with the arms
        // when the whole corner rotates. No remapping required.
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
}
