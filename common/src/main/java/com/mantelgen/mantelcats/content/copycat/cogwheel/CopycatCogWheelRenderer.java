package com.mantelgen.mantelcats.content.copycat.cogwheel;

import com.mantelgen.mantelcats.CCCopycatPartialModels;
import com.mantelgen.mantelcats.foundation.copycat.model.kinetic.IKineticCopycatBlockRenderer;
import com.mantelgen.mantelcats.foundation.copycat.model.kinetic.KineticCopycatRenderer;
import com.mantelgen.mantelcats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.multistate.MaterialItemStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatCogWheelRenderer extends BracketedKineticBlockEntityRenderer implements IKineticCopycatBlockRenderer {
    public CopycatCogWheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(BracketedKineticBlockEntity be, BlockState state) {
        return IKineticCopycatBlockRenderer.super.getRotatedModel(CCCopycatPartialModels.SHAFT, (IMultiStateCopycatBlockEntity) be, "shaft");
    }

    @Override
    protected void renderSafe(BracketedKineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        MaterialItemStorage.MaterialItem cogwheelItem = ((IMultiStateCopycatBlockEntity) be).getMaterialItemOrNull(CopycatCogWheelBlock.Part.COGWHEEL.getSerializedName());
        RenderType type = getRenderType(be, cogwheelItem == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : cogwheelItem.material());

        if (!ICogWheel.isLargeCog(be.getBlockState())) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
            if (type != null)
                renderRotatingBuffer(be, KineticCopycatRenderer.getRenderedBuffer(CCCopycatPartialModels.COGWHEEL, (IMultiStateCopycatBlockEntity) be, "cogwheel"), ms, buffer.getBuffer(type), light);
            return;
        }

        // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to
        // mesh properly

        MaterialItemStorage.MaterialItem shaftItem = ((IMultiStateCopycatBlockEntity) be).getMaterialItemOrNull(CopycatCogWheelBlock.Part.SHAFT.getSerializedName());
        RenderType shaftType = getRenderType(be, shaftItem == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : shaftItem.material());

        Direction.Axis axis = getRotationAxisOf(be);
        if (type != null)
            renderRotatingBuffer(be,
                    KineticCopycatRenderer.getRenderedBuffer(CCCopycatPartialModels.LARGE_COGWHEEL, (IMultiStateCopycatBlockEntity) be, "cogwheel"),
                    ms, buffer.getBuffer(type), light);

        float angle = getAngleForLargeCogShaft(be, axis);
        SuperByteBuffer shaft =
                KineticCopycatRenderer.getRenderedBuffer(CCCopycatPartialModels.SHAFT, (IMultiStateCopycatBlockEntity) be, "shaft");
        kineticRotationTransform(shaft, be, axis, angle, light);
        if (shaftType != null)
            shaft.renderInto(ms, buffer.getBuffer(shaftType));
    }
}
