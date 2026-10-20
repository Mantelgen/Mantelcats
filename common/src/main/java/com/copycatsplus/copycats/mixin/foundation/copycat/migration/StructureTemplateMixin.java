package com.copycatsplus.copycats.mixin.foundation.copycat.migration;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.MigrationManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @WrapOperation(
            method = "loadPalette",
            at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo")
    )
    private StructureTemplate.StructureBlockInfo migrateStructure(BlockPos pos, BlockState state, @Nullable CompoundTag nbt, Operation<StructureTemplate.StructureBlockInfo> original) {
        return MigrationManager.migrateStructure(original.call(pos, state, nbt));
    }

    /**
     * Structures rotate/mirror the block state but load block entity NBT verbatim. Copycat
     * material storage is keyed by part name (e.g. "north", "south"), so the stored materials
     * must be transformed along with the block state or they end up on the wrong parts.
     */
    @WrapOperation(
            method = "placeInWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V")
    )
    private void transformCopycatMaterials(BlockEntity instance, CompoundTag tag, HolderLookup.Provider registries, Operation<Void> original,
                                           @Local(argsOnly = true) ServerLevelAccessor level,
                                           @Local(argsOnly = true) StructurePlaceSettings settings) {
        original.call(instance, tag, registries);
        if (settings.getRotation() == Rotation.NONE && settings.getMirror() == Mirror.NONE)
            return;
        // Create's schematic pipeline (previews, schematicannon printing) places into a
        // SchematicLevel and re-applies the StructureTransform to block entities itself;
        // transforming here as well would rotate/mirror materials twice.
        if (level instanceof SchematicLevel)
            return;
        if (instance instanceof ICopycatBlockEntity copycatBE) {
            StructureTransform transform = new StructureTransform(BlockPos.ZERO, Direction.Axis.Y, settings.getRotation(), settings.getMirror());
            copycatBE.transform(instance, transform);
        }
    }
}
