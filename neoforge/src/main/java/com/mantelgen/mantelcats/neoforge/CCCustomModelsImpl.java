package com.mantelgen.mantelcats.neoforge;

import com.mantelgen.mantelcats.content.copycat.fluid_pipe.neoforge.CopycatFluidPipeModelNeoForge;
import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.resources.model.BakedModel;

public class CCCustomModelsImpl {

    public static BakedModel getFluidPipeModel(BakedModel original, CopycatModelCore copycat, boolean disableAO) {
        return new CopycatFluidPipeModelNeoForge(original, copycat, disableAO);
    }
}
