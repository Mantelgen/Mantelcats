package com.mantelgen.mantelcats.datagen;

import com.mantelgen.mantelcats.foundation.copycat.CopycatBaseBlock;
import com.mantelgen.mantelcats.content.copycat.fluid_pipe.CopycatGlassFluidPipeBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;

public class CCBlockStateGen {

    @ExpectPlatform
    public static void glassPipe(DataGenContext<Block, CopycatGlassFluidPipeBlock> c, RegistrateBlockstateProvider p) {
    }

    @ExpectPlatform
    public static void base(DataGenContext<Block, CopycatBaseBlock> c, RegistrateBlockstateProvider p) {
    }
}
