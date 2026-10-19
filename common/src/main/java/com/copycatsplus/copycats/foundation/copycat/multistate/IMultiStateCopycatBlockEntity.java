package com.copycatsplus.copycats.foundation.copycat.multistate;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.utility.BlockEntityUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.redstone.RoseQuartzLampBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * An interface with implementation for all multi-state copycat block entities.
 * <p>
 * Implementors should create a field to store the materials and redirect calls of
 * {@link IMultiStateCopycatBlockEntity#onLoad},
 * {@link IMultiStateCopycatBlockEntity#read},
 * {@link IMultiStateCopycatBlockEntity#writeSafe} and
 * {@link IMultiStateCopycatBlockEntity#write} to this interface.
 * <p>
 * If the concrete class is not a subclass of {@link MultiStateCopycatBlockEntity},
 * it should also be registered in platform-specific MultiStateCopycatBlockEntityMixins as a mixin target.
 * <p>
 * It is not recommended to override undocumented methods in this interface, since they are considered internal to
 * the implementation of copycats.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IMultiStateCopycatBlockEntity extends ICopycatBlockEntity {
    MaterialItemStorage getMaterialItemStorage();

    /**
     * Null-safe material item lookup that tolerates a missing storage or property,
     * e.g. when querying a stale, partially initialized, or cleared block entity.
     */
    default @Nullable MaterialItemStorage.MaterialItem getMaterialItemOrNull(String property) {
        MaterialItemStorage storage = getMaterialItemStorage();
        if (storage == null || property == null)
            return null;
        return storage.getMaterialItem(property);
    }

    @ApiStatus.OverrideOnly
    void setMaterialItemStorageInternal(MaterialItemStorage storage);

    @Override
    default void init() {
        if (getBlockState().getBlock() instanceof IMultiStateCopycatBlock copycatBlock) {
            Set<String> properties = copycatBlock.storageProperties();
            setMaterialItemStorageInternal(MaterialItemStorage.create(properties));
        } else {
            setMaterialItemStorageInternal(MaterialItemStorage.create(Set.of("block")));
        }
    }

    @Override
    default BlockState getMaterial() {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        return item == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : item.material();
    }

    @Override
    default ItemStack getConsumedItem() {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        return item == null ? ItemStack.EMPTY : item.consumedItem();
    }

    @Override
    default boolean isCTEnabled() {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        return item == null || item.enableCT();
    }

    @Override
    default void setMaterialInternal(BlockState material) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        if (item != null)
            item.setMaterial(material);
    }

    @Override
    default void setConsumedItemInternal(ItemStack consumedItem) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        if (item != null)
            item.setConsumedItem(consumedItem);
    }

    @Override
    default void setCTEnabledInternal(boolean value) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(getBlock().defaultProperty());
        if (item != null)
            item.setEnableCT(value);
    }

    @Override
    default IMultiStateCopycatBlock getBlock() {
        Block block = getBlockState().getBlock();
        if (block instanceof IMultiStateCopycatBlock copycatBlock)
            return copycatBlock;
        // the block state might not be a copycat block in some virtual worlds
        // return sensible defaults in those cases
        return new IMultiStateCopycatBlock() {
            @Override
            public String defaultProperty() {
                MaterialItemStorage storage = getMaterialItemStorage();
                if (storage == null)
                    return "material";
                return storage.getAllProperties().stream().findFirst().orElse("material");
            }

            @Override
            public Vec3i vectorScale(BlockState state) {
                return new Vec3i(1, 1, 1);
            }

            @Override
            public Set<String> storageProperties() {
                return Set.of(defaultProperty());
            }

            @Override
            public int getColorIndex(String property) {
                return 0;
            }

            @Override
            public boolean partExists(BlockState state, String property) {
                return false;
            }

            @Override
            public Vec3i getVectorFromProperty(BlockState state, String property) {
                return new Vec3i(0, 0, 0);
            }

            @Override
            public String getPropertyFromInteraction(BlockState state, BlockGetter level, Vec3i hitLocation, BlockPos blockPos, Direction facing, Vec3 unscaledHit) {
                return defaultProperty();
            }

            @Override
            public VoxelShape getPartialFaceShape(BlockGetter level, BlockState state, String property, Direction face) {
                return state.getFaceOcclusionShape(level, BlockPos.ZERO, face);
            }

            @Override
            public void transformStorage(BlockState state, IMultiStateCopycatBlockEntity be, StructureTransform transform) {

            }

            @Override
            public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, BlockPos fromPos, @Nullable BlockPos toPos, @Nullable BlockState toState) {
                return true;
            }

            @Override
            public boolean canConnectTexturesToward(String property, BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
                return false;
            }
        };
    }

    @Override
    default boolean hasCustomMaterial() {
        MaterialItemStorage storage = getMaterialItemStorage();
        return storage != null
                && !storage.getAllMaterials().stream().allMatch(state -> state.is(AllBlocks.COPYCAT_BASE.get()));
    }

    default void setMaterial(String property, BlockState blockState) {
        MaterialItemStorage storage = getMaterialItemStorage();
        if (storage == null)
            return;
        BlockState wrapperState = getBlockState();

        MaterialItemStorage.MaterialItem materialItem = storage.getMaterialItem(property);
        BlockState finalMaterial = blockState;
        if (materialItem == null || !materialItem.material().is(finalMaterial.getBlock()))
            for (Direction side : Iterate.directions) {
                BlockPos neighbour = getBlockPos().relative(side);
                BlockState neighbourState = getLevel().getBlockState(neighbour);
                if (neighbourState != wrapperState)
                    continue;
                if (!(getLevel().getBlockEntity(neighbour) instanceof IMultiStateCopycatBlockEntity cbe))
                    continue;
                MaterialItemStorage.MaterialItem otherItem = cbe.getMaterialItemOrNull(property);
                if (otherItem == null)
                    continue;
                BlockState otherMaterial = otherItem.material();
                if (!otherMaterial.is(blockState.getBlock()))
                    continue;
                blockState = otherMaterial;
                break;
            }
        if (materialItem == null)
            materialItem = new MaterialItemStorage.MaterialItem(blockState, ItemStack.EMPTY);
        else
            materialItem.setMaterial(blockState);
        storage.storeMaterialItem(property, materialItem);

        BlockEntityUtils.redraw((BlockEntity) this);
    }

    default boolean cycleMaterial(String property) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(property);
        if (item == null)
            return false;
        BlockState material = item.material();
        if (material.hasProperty(TrapDoorBlock.HALF) && material.getOptionalValue(TrapDoorBlock.OPEN)
                .orElse(false))
            setMaterial(property, material.cycle(TrapDoorBlock.HALF));
        else if (material.hasProperty(BlockStateProperties.FACING))
            setMaterial(property, material.cycle(BlockStateProperties.FACING));
        else if (material.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            setMaterial(property, material.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    material.getValue(BlockStateProperties.HORIZONTAL_FACING)
                            .getClockWise()));
        else if (material.hasProperty(BlockStateProperties.AXIS))
            setMaterial(property, material.cycle(BlockStateProperties.AXIS));
        else if (material.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
            setMaterial(property, material.cycle(BlockStateProperties.HORIZONTAL_AXIS));
        else if (material.hasProperty(BlockStateProperties.LIT))
            setMaterial(property, material.cycle(BlockStateProperties.LIT));
        else if (material.hasProperty(RoseQuartzLampBlock.POWERING))
            setMaterial(property, material.cycle(RoseQuartzLampBlock.POWERING));
        else
            return false;

        return true;
    }

    default void setConsumedItem(String property, ItemStack itemStack) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(property);
        if (item == null)
            return;
        item.setConsumedItem(itemStack);
        notifyUpdate();
    }

    default void setEnableCT(String property, boolean value) {
        MaterialItemStorage.MaterialItem item = getMaterialItemOrNull(property);
        if (item == null)
            return;
        item.setEnableCT(value);
        notifyUpdate();
    }

    @Override
    default ItemRequirement getRequiredItems(BlockState state) {
        MaterialItemStorage storage = getMaterialItemStorage();
        if (storage == null)
            return new ItemRequirement(java.util.List.of());
        return new ItemRequirement(
                storage.getAllConsumedItems().stream()
                        .map(stack -> new ItemRequirement.StackRequirement(stack, ItemRequirement.ItemUseType.CONSUME))
                        .toList()
        );
    }

    @Override
    default void transform(BlockEntity blockEntity, StructureTransform transform) {
        MaterialItemStorage storage = getMaterialItemStorage();
        if (storage == null)
            return;
        getBlock().transformStorage(this.getBlockState(), this, transform);
        for (String key : storage.getAllProperties()) {
            MaterialItemStorage.MaterialItem item = storage.getMaterialItem(key);
            if (item != null)
                item.setMaterial(transform.apply(item.material()));
        }
        notifyUpdate();
    }

    static void read(IMultiStateCopycatBlockEntity self, CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (self.getBlockState().getBlock() instanceof IMultiStateCopycatBlock && self.getMaterialItemStorage() != null) {
            boolean anyUpdated = self.getMaterialItemStorage().deserialize(tag.getCompound("material_data"), registries);

            if (anyUpdated)
                BlockEntityUtils.redraw((BlockEntity) self); // not calling self.redraw() because Extended Cogwheels overwrites it to be protected
        }
    }

    static void writeSafe(IMultiStateCopycatBlockEntity self, CompoundTag tag, HolderLookup.Provider registries) {
        BlockEntityUtils.saveMetadata((BlockEntity) self, tag);
        if (self.getMaterialItemStorage() != null)
            tag.put("material_data", self.getMaterialItemStorage().serializeSafe(registries));
    }

    static void write(IMultiStateCopycatBlockEntity self, CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (self.getMaterialItemStorage() != null)
            tag.put("material_data", self.getMaterialItemStorage().serialize(registries));
    }
}
