package com.mantelgen.mantelcats.neoforge;

import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.compat.Mods;
import com.mantelgen.mantelcats.compat.neoforge.AdditionalPlacementsCompatNeoForge;
import com.mantelgen.mantelcats.datagen.neoforge.CCDatagenImpl;
import com.mantelgen.mantelcats.datagen.recipes.neoforge.CCCraftingConditions;
import com.mantelgen.mantelcats.utility.LogicalSidedProvider;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Copycats.MODID)
public class CopycatsImpl {

    static IEventBus modBus;

    public CopycatsImpl(IEventBus modBus, ModContainer container) {
        CopycatsImpl.modBus = modBus;
        Copycats.getRegistrate().registerEventListeners(modBus);
        CCCreativeTabsImpl.register(CopycatsImpl.modBus);
        Copycats.init();

        CCCraftingConditions.register(CopycatsImpl.modBus);
        NeoForge.EVENT_BUS.addListener(this::serverStarting);

        modBus.addListener(EventPriority.HIGHEST, CCDatagenImpl::gatherDataHighPriority);
        modBus.addListener(EventPriority.LOWEST, CCDatagenImpl::gatherData);
        modBus.addListener(LegacyIdMigration::onCommonSetup);
        Mods.ADDITIONAL_PLACEMENTS.executeIfInstalled(() -> AdditionalPlacementsCompatNeoForge::register);
    }

    private void serverStarting(ServerStartingEvent event) {
        LogicalSidedProvider.setServer(event::getServer);
    }
}