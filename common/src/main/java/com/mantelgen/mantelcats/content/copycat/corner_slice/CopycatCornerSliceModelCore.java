package com.mantelgen.mantelcats.content.copycat.corner_slice;

import com.mantelgen.mantelcats.CCBlocks;
import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.AssemblyTransform;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.List;

import static com.mantelgen.mantelcats.content.copycat.corner_slice.CopycatCornerSliceBlock.*;
import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.MutableCullFace.*;

public class CopycatCornerSliceModelCore extends CopycatModelCore {

    @Override
    public void registerModels(List<ModelEntry> entries) {
        registerForMultiState(entries, CCBlocks.COPYCAT_CORNER_SLICE.get(), false);
    }

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        boolean flipY = state.getValue(HALF) == Half.TOP;
        int rot = (int) state.getValue(FACING).toYRot();
        int layers = state.getValue(LAYERS);
        AssemblyTransform transform = t -> t.rotateY(rot).flipY(flipY);
        switch (key) {
            case BOTTOM_SOUTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, 0, 16 - layers),
                    aabb(layers, layers, layers).move(16 - layers, 0, 16 - layers),
                    cull(UP | NORTH | WEST)
            );
            case BOTTOM_SOUTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, 0, 16 - layers),
                    aabb(layers, layers, layers).move(0, 0, 16 - layers),
                    cull(UP | NORTH | EAST)
            );
            case BOTTOM_NORTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, 0, 16 - layers * 2),
                    aabb(layers, layers, layers).move(16 - layers, 0, 0),
                    cull(UP | SOUTH | WEST)
            );
            case BOTTOM_NORTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, 0, 16 - layers * 2),
                    aabb(layers, layers, layers).move(0, 0, 0),
                    cull(UP | SOUTH | EAST)
            );
            case TOP_SOUTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, layers, 16 - layers),
                    aabb(layers, layers, layers).move(16 - layers, 16 - layers, 16 - layers),
                    cull(DOWN | NORTH | WEST)
            );
            case TOP_SOUTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, layers, 16 - layers),
                    aabb(layers, layers, layers).move(0, 16 - layers, 16 - layers),
                    cull(DOWN | NORTH | EAST)
            );
            case TOP_NORTH_EAST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers, layers, 16 - layers * 2),
                    aabb(layers, layers, layers).move(16 - layers, 16 - layers, 0),
                    cull(DOWN | SOUTH | WEST)
            );
            case TOP_NORTH_WEST -> context.assemblePiece(
                    transform,
                    vec3(16 - layers * 2, layers, 16 - layers * 2),
                    aabb(layers, layers, layers).move(0, 16 - layers, 0),
                    cull(DOWN | SOUTH | EAST)
            );
        }
    }
}
