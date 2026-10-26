package com.mantelgen.mantelcats.content.copycat.vertical_slope;

import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.AssemblyTransform;
import com.mantelgen.mantelcats.content.copycat.slope.CopycatSlopeModelCore;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatVerticalSlopeModelCore extends CopycatModelCore {

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        Direction facing = state.getValue(CopycatVerticalSlopeBlock.FACING);
        int rot = (int) facing.toYRot();
        AssemblyTransform transform = t -> t.rotateZ(-90).rotateY(rot);
        CopycatSlopeModelCore.assembleSlope(context, transform, 0, 16, enhanced);
    }
}
