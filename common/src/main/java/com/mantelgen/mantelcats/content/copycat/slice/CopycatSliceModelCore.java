package com.mantelgen.mantelcats.content.copycat.slice;

import com.mantelgen.mantelcats.CCBlocks;
import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.AssemblyTransform;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.List;

import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.MutableCullFace.*;
import static com.mantelgen.mantelcats.content.copycat.slice.CopycatSliceBlock.*;

public class CopycatSliceModelCore extends CopycatModelCore {

    @Override
    public void registerModels(List<ModelEntry> entries) {
        registerForMultiState(entries, CCBlocks.COPYCAT_SLICE.get(), false);
    }

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        boolean flipY = state.getValue(HALF) == Half.TOP;
        int rot = (int) state.getValue(FACING).toYRot();
        int layers = state.getValue(LAYERS);
        AssemblyTransform transform = t -> t.rotateY(rot).flipY(flipY);
        switch (key) {
            case BOTTOM_SOUTH -> context.assemblePiece(
                    transform,
                    vec3(0, 0, 16 - layers),
                    aabb(16, layers, layers).move(0, 0, 16 - layers),
                    cull(UP | NORTH)
            );
            case TOP_SOUTH -> context.assemblePiece(
                    transform,
                    vec3(0, layers, 16 - layers),
                    aabb(16, layers, layers).move(0, 16 - layers, 16 - layers),
                    cull(DOWN | NORTH)
            );
            case BOTTOM_NORTH -> context.assemblePiece(
                    transform,
                    vec3(0, 0, 16 - layers * 2),
                    aabb(16, layers, layers).move(0, 0, 0),
                    cull(UP | SOUTH)
            );
            case TOP_NORTH -> context.assemblePiece(
                    transform,
                    vec3(0, layers, 16 - layers * 2),
                    aabb(16, layers, layers).move(0, 16 - layers, 0),
                    cull(DOWN | SOUTH)
            );
        }
    }
}
