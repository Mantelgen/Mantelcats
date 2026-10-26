package com.mantelgen.mantelcats.compat.neoforge;

import com.mantelgen.mantelcats.compat.AdditionalPlacementsCompat;
import com.firemerald.additionalplacements.generation.Registration;

public class AdditionalPlacementsCompatNeoForge {
    public static void register() {
        Registration.addRegistration(new AdditionalPlacementsCompat());
    }
}
