package com.mantelgen.mantelcats.mixin.foundation.copycat.kinetic;

import com.mantelgen.mantelcats.CopycatsClient;
import com.simibubi.create.CreateClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Invalidate the kinetic copycats buffer cache when the renderers are invalidated.
 */
@Mixin(value = CreateClient.class, remap = false)
public class CreateClientMixin {
    @Inject(method = "invalidateRenderers", at = @At("HEAD"))
    private static void invalidateRenderers(CallbackInfo ci) {
        CopycatsClient.invalidateCaches();
    }
}
