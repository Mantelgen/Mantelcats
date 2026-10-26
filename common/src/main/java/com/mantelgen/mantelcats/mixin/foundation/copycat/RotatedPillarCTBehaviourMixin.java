package com.mantelgen.mantelcats.mixin.foundation.copycat;

import com.mantelgen.mantelcats.CCBlocks;
import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.block.connected.RotatedPillarCTBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RotatedPillarCTBehaviour.class)
public class RotatedPillarCTBehaviourMixin {
    @WrapOperation(
            method = "connectsTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
    )
    private Block getCopycat(BlockState instance, Operation<Block> original) {
        if (instance.getBlock() instanceof ICopycatBlock copycatBlock) {
            CCBlocks.WRAPPED_COPYCAT.get().setWrapped(copycatBlock);
            return CCBlocks.WRAPPED_COPYCAT.get();
        }
        return original.call(instance);
    }
}
