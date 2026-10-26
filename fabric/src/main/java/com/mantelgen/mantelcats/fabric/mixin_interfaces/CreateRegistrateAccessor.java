package com.mantelgen.mantelcats.fabric.mixin_interfaces;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public interface CreateRegistrateAccessor {

    void copycats$setCreativeTab(ResourceKey<CreativeModeTab> tab);
}
