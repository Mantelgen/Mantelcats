package com.mantelgen.mantelcats.fabric.mixin.featuretoggle;

import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.config.CCConfigs;
import com.mantelgen.mantelcats.utility.LogicalSidedProvider;
import com.mantelgen.mantelcats.utility.Platform;
import com.mantelgen.mantelcats.network.CCPackets;
import com.mantelgen.mantelcats.network.ConfigSyncPacket;
import com.simibubi.create.foundation.config.ui.ConfigScreen;
import com.simibubi.create.foundation.config.ui.SubMenuConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sync the config when the user saves changes in Create's GUI config screen.
 */
@Mixin(value = SubMenuConfigScreen.class, remap = false)
public class SubMenuConfigScreenMixin {
    @Inject(
            method = "saveChanges()V",
            at = @At("TAIL")
    )
    private void saveChangesAndRefresh(CallbackInfo ci) {
        if (ConfigScreen.modID.equals(Copycats.MODID)) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && Minecraft.getInstance().hasSingleplayerServer())
                LogicalSidedProvider.WORKQUEUE.get(Platform.Environment.SERVER).submit(() -> CCPackets.PACKETS.sendTo(PlayerSelection.all(), new ConfigSyncPacket(CCConfigs.common().getSyncConfig(), ModConfig.Type.COMMON)));
        }
    }
}
