package com.mantelgen.mantelcats;

import com.mantelgen.mantelcats.foundation.copycat.model.kinetic.KineticCopycatRenderer;
import com.mantelgen.mantelcats.utility.LogicalSidedProvider;
import com.mantelgen.mantelcats.network.CCPackets;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;

public class CopycatsClient {

    public static final SuperByteBufferCache BUFFER_CACHE = new SuperByteBufferCache();

    public static void init() {
        LogicalSidedProvider.setClient(Minecraft::getInstance);
        CCKeys.register();
        BUFFER_CACHE.registerCompartment(KineticCopycatRenderer.KINETIC_COPYCAT, 60);

    }

    public static void invalidateCaches() {
        BUFFER_CACHE.invalidate();
    }
}
