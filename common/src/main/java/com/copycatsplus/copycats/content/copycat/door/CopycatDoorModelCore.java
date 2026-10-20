package com.copycatsplus.copycats.content.copycat.door;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

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

    private static void assembleWithCT(BlockState state, CopycatRenderContext context) {
        int rot = (int) state.getValue(DoorBlock.FACING).toYRot();
        boolean rightHinge = state.getValue(DoorBlock.HINGE).equals(DoorHingeSide.RIGHT);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = state.getValue(DoorBlock.OPEN);
        AssemblyTransform transform = open
                ? rightHinge ? t -> t.rotateY(rot - 90) : t -> t.flipX(true).rotateY(rot + 90)
                : t -> t.rotateY(rot);
        if (half == DoubleBlockHalf.LOWER) {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 12, 2),
                    cull(SOUTH | UP));
            context.assemblePiece(transform,
                    vec3(0, 12, 0),
                    aabb(16, 4, 2).move(0, 4, 0),
                    cull(SOUTH | DOWN));
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 12, 1).move(0, 0, 15),
                    cull(NORTH | UP));
            context.assemblePiece(transform,
                    vec3(0, 12, 2),
                    aabb(16, 4, 1).move(0, 4, 15),
                    cull(NORTH | DOWN));
        } else {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 4, 0),
                    aabb(16, 12, 2).move(0, 4, 0),
                    cull(SOUTH | DOWN));
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 4, 2).move(0, 8, 0),
                    cull(SOUTH | UP));
            //Back
            context.assemblePiece(transform,
                    vec3(0, 4, 2),
                    aabb(16, 12, 1).move(0, 4, 15),
                    cull(NORTH | DOWN));
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 4, 1).move(0, 8, 15),
                    cull(NORTH | UP));
        }
    }

    private static void assembleWithoutCT(BlockState state, CopycatRenderContext context) {
        int rot = (int) state.getValue(DoorBlock.FACING).toYRot();
        boolean rightHinge = state.getValue(DoorBlock.HINGE).equals(DoorHingeSide.RIGHT);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = state.getValue(DoorBlock.OPEN);
        AssemblyTransform transform = open
                ? rightHinge ? t -> t.rotateY(rot - 90) : t -> t.flipX(true).rotateY(rot + 90)
                : t -> t.rotateY(rot);
        if (half == DoubleBlockHalf.LOWER) {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 16, 2),
                    cull(SOUTH));
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 16, 1).move(0, 0, 15),
                    cull(NORTH));
        } else {
            //Front
            context.assemblePiece(transform,
                    vec3(0, 0, 0),
                    aabb(16, 16, 2).move(0, 0, 0),
                    cull(SOUTH));
            //Back
            context.assemblePiece(transform,
                    vec3(0, 0, 2),
                    aabb(16, 16, 1).move(0, 0, 15),
                    cull(NORTH));
        }
    }
}
