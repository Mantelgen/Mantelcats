package com.mantelgen.mantelcats.content.copycat.door;

import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.AssemblyTransform;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.quad.QuadTransform;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.mantelgen.mantelcats.foundation.copycat.model.assembly.MutableCullFace.*;

public class CopycatDoorModelCore extends CopycatModelCore {

    @Override
    public void registerModels(List<ModelEntry> entries) {
        entries.add(new ModelEntry(MATERIAL_KEY, ModelGetter.MATERIAL, this, updatePropertiesIfMatch(DoorBlock.class), EntryType.COPYCAT));
    }

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        if (material.getBlock() instanceof DoorBlock) {
            context.assembleAll();
            return;
        }

        if (state.getValue(CopycatDoorBlock.CT)) {
            assembleWithCT(state, context);
        } else {
            assembleWithoutCT(state, context);
        }
    }

    /**
     * When the door is open, the panel swings 90 degrees onto the hinge-side edge. The open model
     * must reuse the same material face as the closed model so the texture rotates rigidly with
     * the panel like vanilla doors, rather than sampling a different face of the material. The
     * swing is applied as a quad transform so it only moves the copied vertices: putting it in
     * the assembly transform would rotate the source selection too and leave the output
     * identical to the closed state.
     */
    private static final QuadTransform[] NO_SWING = new QuadTransform[0];
    private static final QuadTransform[] SWING = {rotate(pivot(8, 8, 8), angle(0, 90, 0))};

    private static void assembleWithCT(BlockState state, CopycatRenderContext context) {
        int rot = (int) state.getValue(DoorBlock.FACING).toYRot();
        boolean rightHinge = state.getValue(DoorBlock.HINGE).equals(DoorHingeSide.RIGHT);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = state.getValue(DoorBlock.OPEN);
        AssemblyTransform transform = t -> t.rotateY(rot);
        if (open && !rightHinge)
            transform = t -> t.flipX(true).rotateY(rot);
        QuadTransform[] swing = open ? SWING : NO_SWING;
        if (half == DoubleBlockHalf.LOWER) {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 12, 2),
                    cull(SOUTH | UP),
                    swing);
            context.assemblePiece(transform,
                    vec3(0, 12, 0),
                    aabb(16, 4, 2).move(0, 4, 0),
                    cull(SOUTH | DOWN),
                    swing);
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 12, 1).move(0, 0, 15),
                    cull(NORTH | UP),
                    swing);
            context.assemblePiece(transform,
                    vec3(0, 12, 2),
                    aabb(16, 4, 1).move(0, 4, 15),
                    cull(NORTH | DOWN),
                    swing);
        } else {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 4, 0),
                    aabb(16, 12, 2).move(0, 4, 0),
                    cull(SOUTH | DOWN),
                    swing);
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 4, 2).move(0, 8, 0),
                    cull(SOUTH | UP),
                    swing);
            //Back
            context.assemblePiece(transform,
                    vec3(0, 4, 2),
                    aabb(16, 12, 1).move(0, 4, 15),
                    cull(NORTH | DOWN),
                    swing);
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 4, 1).move(0, 8, 15),
                    cull(NORTH | UP),
                    swing);
        }
    }

    private static void assembleWithoutCT(BlockState state, CopycatRenderContext context) {
        int rot = (int) state.getValue(DoorBlock.FACING).toYRot();
        boolean rightHinge = state.getValue(DoorBlock.HINGE).equals(DoorHingeSide.RIGHT);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = state.getValue(DoorBlock.OPEN);
        AssemblyTransform transform = t -> t.rotateY(rot);
        if (open && !rightHinge)
            transform = t -> t.flipX(true).rotateY(rot);
        QuadTransform[] swing = open ? SWING : NO_SWING;
        if (half == DoubleBlockHalf.LOWER) {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 16, 2),
                    cull(SOUTH),
                    swing);
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 16, 1).move(0, 0, 15),
                    cull(NORTH),
                    swing);
        } else {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 16, 2).move(0, 0, 0),
                    cull(SOUTH),
                    swing);
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 16, 1).move(0, 0, 15),
                    cull(NORTH),
                    swing);
        }
    }
}
