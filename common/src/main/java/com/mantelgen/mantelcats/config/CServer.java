package com.mantelgen.mantelcats.config;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class CServer extends ConfigBase {
    public final CStress stress;

    public final ConfigBool disableBlockConsumption = b(false, "disableBlockConsumption", Comments.disableBlockConsumption);

    public CServer() {
        this.stress = this.nested(0, CStress::new, new String[]{Comments.stress});
    }

    public @NotNull String getName() {
        return "server";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";
        static String disableBlockConsumption = "Do not consume material blocks when applying them to copycats in survival mode. Materials applied for free are not returned when the copycat is broken or the material is removed.";
    }
}
