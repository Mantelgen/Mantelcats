package com.mantelgen.mantelcats.mixin.compat.rubidium;

import com.mantelgen.mantelcats.compat.Mods;
import com.mantelgen.mantelcats.foundation.annotation.ModMixin;
import com.mantelgen.mantelcats.foundation.copycat.CopycatExternalContext;
import com.mantelgen.mantelcats.foundation.copycat.multistate.MultiStateTextureAtlasSprite;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteFinderCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Record the currently rendering property for multi-state blocks so that block colors can be displayed properly.
 * <p>
 * Rubidium compatible version of {@link com.mantelgen.mantelcats.mixin.foundation.copycat.multistate.ModelBlockRendererMixin}.
 */
@ModMixin(requiredMods = {Mods.RUBIDIUM, Mods.SODIUM})
@Mixin(value = BlockRenderer.class, remap = false)
@Pseudo
public class BlockRendererMixin {
    @Inject(
            method = "colorizeQuad",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;getColors(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos$MutableBlockPos;Ljava/lang/Object;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;[I)V"
            ),
            require = 0
    )
    private void beforeColor(MutableQuadViewImpl quad, int colorIndex, CallbackInfo ci) {
        if (quad.sprite(SpriteFinderCache.forBlockAtlas()) instanceof MultiStateTextureAtlasSprite sprite)
            CopycatExternalContext.setPropertyForBlockColor(sprite.getProperty());
    }

    @Inject(
            method = "colorizeQuad",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;getColors(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos$MutableBlockPos;Ljava/lang/Object;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;[I)V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void afterColor(MutableQuadViewImpl quad, int colorIndex, CallbackInfo ci) {
        CopycatExternalContext.setPropertyForBlockColor(null);
    }
}
