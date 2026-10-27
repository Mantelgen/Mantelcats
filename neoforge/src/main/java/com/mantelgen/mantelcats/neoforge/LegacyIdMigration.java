package com.mantelgen.mantelcats.neoforge;

import com.mantelgen.mantelcats.Copycats;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.BaseMappedRegistry;

/**
 * Migrates worlds saved before the {@code copycats -> mantelcats} namespace
 * rename in 4.0.0. NeoForge's {@link BaseMappedRegistry} alias mechanism makes
 * every registry lookup for a legacy {@code copycats:*} id resolve to the
 * {@code mantelcats:*} entry with the same path, covering blocks, items, block
 * entity types, and every other registry the mod populates.
 *
 * <p>Fabric has no equivalent registry alias mechanism; worlds on Fabric need a
 * datafixer schema instead (see {@link Copycats#DATA_FIXER_VERSION}).
 */
final class LegacyIdMigration {

    private static final String LEGACY_NAMESPACE = "copycats";

    private LegacyIdMigration() {
    }

    static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(LegacyIdMigration::registerAliases);
    }

    private static void registerAliases() {
        int aliases = 0;
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            if (!(registry instanceof BaseMappedRegistry<?> mapped)) {
                continue;
            }
            for (ResourceLocation key : registry.keySet()) {
                if (!key.getNamespace().equals(Copycats.MODID)) {
                    continue;
                }
                ResourceLocation legacy = ResourceLocation.fromNamespaceAndPath(LEGACY_NAMESPACE, key.getPath());
                mapped.addAlias(legacy, key);
                aliases++;
            }
        }
        Copycats.LOGGER.info("Registered {} legacy {}:* -> {}:* registry aliases", aliases, LEGACY_NAMESPACE, Copycats.MODID);
    }
}
