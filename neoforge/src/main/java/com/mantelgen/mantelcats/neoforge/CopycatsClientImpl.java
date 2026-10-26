package com.mantelgen.mantelcats.neoforge;

import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.CopycatsClient;
import com.mantelgen.mantelcats.foundation.copycat.model.kinetic.RendererReloadCache;
import dev.engine_room.flywheel.api.event.ReloadLevelRendererEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Copycats.MODID, dist = Dist.CLIENT)
public class CopycatsClientImpl {

    public CopycatsClientImpl(FMLModContainer container, IEventBus modBus) {
        CopycatsClient.init();
        NeoForge.EVENT_BUS.<ReloadLevelRendererEvent>addListener(RendererReloadCache::onReloadLevelRenderer);
    }
}
