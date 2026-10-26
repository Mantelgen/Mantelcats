package com.mantelgen.mantelcats.foundation.copycat.model.neoforge;

import com.mantelgen.mantelcats.foundation.copycat.model.CopycatModelCore;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.NotNull;

public class CopycatModelCoreImpl {

    @NotNull
    public static BakedModel createModel(BakedModel original, CopycatModelCore core) {
        return new CopycatModelNeoForge(original, core, false);
    }

    @NotNull
    public static BakedModel createKineticModel(BakedModel original, CopycatModelCore core) {
        return new CopycatModelNeoForge(original, core, true);
    }
}
