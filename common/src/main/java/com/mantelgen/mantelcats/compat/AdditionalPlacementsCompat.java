package com.mantelgen.mantelcats.compat;

import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlock;
import com.firemerald.additionalplacements.generation.IBlockBlacklister;
import com.firemerald.additionalplacements.generation.RegistrationInitializer;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AdditionalPlacementsCompat implements RegistrationInitializer {

    @Override
    public void addGlobalBlacklisters(Consumer<IBlockBlacklister<Block>> register) {
        register.accept((t, rl) -> t instanceof ICopycatBlock);
    }
}
