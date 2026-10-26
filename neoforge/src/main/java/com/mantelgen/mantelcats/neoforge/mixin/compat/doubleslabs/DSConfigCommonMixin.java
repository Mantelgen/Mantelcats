package com.mantelgen.mantelcats.neoforge.mixin.compat.doubleslabs;

import cjminecraft.doubleslabs.common.config.DSConfig;
import com.mantelgen.mantelcats.compat.Mods;
import com.mantelgen.mantelcats.foundation.annotation.ModMixin;
import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Always blacklist copycat blocks from being considered by Double Slabs.
 */
@ModMixin(requiredMods = Mods.DOUBLE_SLABS)
@Mixin(DSConfig.Common.class)
@Pseudo
public class DSConfigCommonMixin {

    @Inject(method = "isBlacklistedHorizontalSlab", at = @At("HEAD"), cancellable = true, remap = false)
    private void copycats$alwaysBlacklistHorizontal(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof ICopycatBlock) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isBlacklistedVerticalSlab", at = @At("HEAD"), cancellable = true, remap = false)
    private void copycats$alwaysBlacklistVertical(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof ICopycatBlock) {
            cir.setReturnValue(false);
        }
    }
}
