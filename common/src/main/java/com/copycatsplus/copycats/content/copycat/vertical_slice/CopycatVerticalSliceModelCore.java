package com.copycatsplus.copycats.content.copycat.vertical_slice;

import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;
import static com.copycatsplus.copycats.content.copycat.vertical_slice.CopycatVerticalSliceBlock.*;

public class CopycatVerticalSliceModelCore extends CopycatModelCore {

    @Override
    public void registerModels(List<ModelEntry> entries) {
        registerForMultiState(entries, CCBlocks.COPYCAT_VERTICAL_SLICE.get(), false);
    }

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        int rot = (int) state.getValue(FACING).toYRot();
        int layers = state.getValue(LAYERS);
        AssemblyTransform transform = t -> t.rotateY(rot);
        switch (key) {
            case SOUTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, 0, 16 - layers),
                    aabb(layers, 16, layers).move(16 - layers, 0, 16 - layers),
                    cull(WEST | NORTH)
            );
            case NORTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, 0, 16 - layers * 2),
                    aabb(layers, 16, layers).move(16 - layers, 0, 0),
                    cull(WEST | SOUTH)
            );
            case SOUTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, 0, 16 - layers),
                    aabb(layers, 16, layers).move(0, 0, 16 - layers),
                    cull(EAST | NORTH)
            );
            case NORTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, 0, 16 - layers * 2),
                    aabb(layers, 16, layers).move(0, 0, 0),
                    cull(EAST | SOUTH)
            );
        }
    }
}
