package com.mantelgen.mantelcats.datagen.recipes.neoforge;


import com.mantelgen.mantelcats.Copycats;
import com.mantelgen.mantelcats.neoforge.CopycatsImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CCCraftingConditions {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterEvent.class, registerEvent -> {
            registerEvent.register(NeoForgeRegistries.CONDITION_SERIALIZERS.key(), helper -> {
               helper.register(Copycats.asResource("feature_enabled"), FeatureEnabledCondition.CODEC);
            });
        });
    }
}
