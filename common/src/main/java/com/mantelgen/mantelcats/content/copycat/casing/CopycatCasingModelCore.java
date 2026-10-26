package com.mantelgen.mantelcats.content.copycat.casing;

import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import com.mantelgen.mantelcats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.world.level.block.state.BlockState;

public class CopycatCasingModelCore extends CopycatModelCore {

    @Override
    public void emitCopycatQuads(String key, BlockState state, CopycatRenderContext context, BlockState material) {
        context.assembleAll(); // assemble without any modifications
    }
}
