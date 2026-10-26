package com.mantelgen.mantelcats.config.neoforge;

import com.mantelgen.mantelcats.config.CCConfigs;
import com.mantelgen.mantelcats.config.SyncConfigBase;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public abstract class SyncConfigBaseImpl extends SyncConfigBase {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        for (ConfigBase config : CCConfigs.CONFIGS.values())
            if (config instanceof SyncConfigBase syncConfig)
                syncConfig.syncToPlayer((ServerPlayer) event.getEntity());
    }
}
