package com.copycatsplus.copycats.content.copycat.corner_panel;

import com.copycatsplus.copycats.CCBlocks;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadAutoCull;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.copycatsplus.copycats.content.copycat.corner_panel.CopycatCornerPanelBlock.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

/**
 * Assembles the two arms of the L-shaped corner panel from the copied material's own quads.
 * Each arm needs three pieces: the outer carve (material surface quads clipped to the arm
 * box) plus one face-plate for each interior wall of the recess, which has no corresponding
 * material surface.
 */
public class CopycatMultiCornerPanelModelCore extends CopycatModelCore {

    @Override
    public void registerModels(List<ModelEntry> entries) {
        registerForMultiState(entries, CCBlocks.COPYCAT_CORNER_PANEL.get(), false);
    }

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        if (!key.equals(PLATE_Z) && !key.equals(PLATE_X)) return;

        Corner corner = state.getValue(CORNER);
        // The geometry below is the canonical NORTH_EAST L; rotate it into the placed corner.
        int rot = corner.ordinal() * 90;
        AssemblyTransform transform = t -> t.rotateY(rot);

        if (key.equals(PLATE_Z)) {
            // Arm on the north wall: [8,16] x [0,16] x [0,3], owns the corner junction.
            QuadAutoCull autoCull = autoCull(aabb(8, 16, 3).move(8, 0, 0));
            // Outer faces: north, east (junction sliver), up, down.
            context.assemblePiece(transform,
                    vec3(8, 0, 0),
                    aabb(8, 16, 3).move(8, 0, 0),
                    cull(SOUTH | WEST),
                    autoCull
            );
            // Inner recess wall facing west at x=8.
            context.assemblePiece(transform,
                    vec3(8, 0, 0),
                    aabb(1, 16, 3).move(0, 0, 0),
                    cull(NORTH | SOUTH | EAST | UP | DOWN),
                    autoCull
            );
            // Inner recess wall facing south at z=3.
            context.assemblePiece(transform,
                    vec3(8, 0, 3),
                    aabb(8, 16, 1).move(8, 0, 15),
                    cull(NORTH | EAST | WEST | UP | DOWN),
                    autoCull
            );
        } else {
            // Arm on the east wall: [13,16] x [0,16] x [3,8], stops short of the junction.
            QuadAutoCull autoCull = autoCull(aabb(3, 16, 5).move(13, 0, 3));
            // Outer faces: east, up, down.
            context.assemblePiece(transform,
                    vec3(13, 0, 3),
                    aabb(3, 16, 5).move(13, 0, 3),
                    cull(NORTH | SOUTH | WEST),
                    autoCull
            );
            // Inner recess wall facing west at x=13.
            context.assemblePiece(transform,
                    vec3(13, 0, 3),
                    aabb(1, 16, 5).move(0, 0, 3),
                    cull(NORTH | SOUTH | EAST | UP | DOWN),
                    autoCull
            );
            // Inner recess wall facing south at z=8.
            context.assemblePiece(transform,
                    vec3(13, 0, 8),
                    aabb(3, 16, 1).move(13, 0, 15),
                    cull(NORTH | EAST | WEST | UP | DOWN),
                    autoCull
            );
        }
    }
}
