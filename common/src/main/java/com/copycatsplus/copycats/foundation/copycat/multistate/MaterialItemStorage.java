package com.copycatsplus.copycats.foundation.copycat.multistate;

import com.copycatsplus.copycats.Copycats;
import com.copycatsplus.copycats.utility.ItemUtils;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores copycat data, including material, consumed item and CT toggle, for each part of a multi-state copycat.
 * <p>
 * Storage is keyed by the copycat's declared part properties and enforces two invariants:
 * entries are never null, and keys outside the declared property set are rejected on write and
 * dropped on deserialize. Declared properties are seeded with the base material on creation, so
 * a declared key always resolves to a non-null item unless the storage has been cleared.
 */
public class MaterialItemStorage {

    private static final String SCHEMA_KEY = "schema";
    private static final int SCHEMA_VERSION = 1;

    private Map<String, MaterialItem> storage;

    private MaterialItemStorage(Set<String> properties) {
        storage = new HashMap<>(properties.size());
        for (String property : properties) {
            storage.put(property, new MaterialItem(AllBlocks.COPYCAT_BASE.getDefaultState(), ItemStack.EMPTY));
        }
    }

    public static MaterialItemStorage create(Set<String> properties) {
        return new MaterialItemStorage(properties);
    }

    /**
     * Replace the material item for a specific property.
     * <p>
     * Writes are rejected when the property is not part of this storage's declared property set
     * or the item is null, keeping foreign keys and null entries out of the storage.
     */
    public void storeMaterialItem(String property, MaterialItem materialItem) {
        if (materialItem == null) {
            Copycats.LOGGER.warn("Refusing to store null material item for property '{}'", property);
            return;
        }
        if (!storage.containsKey(property)) {
            Copycats.LOGGER.warn("Refusing to store material item for undeclared property '{}'", property);
            return;
        }
        storage.put(property, materialItem);
    }

    /**
     * Get the material item for a specific property.
     */
    public @Nullable MaterialItem getMaterialItem(String property) {
        return storage.get(property);
    }

    /**
     * Get all properties stored in this storage, regardless of whether they have a custom material.
     */
    public Set<String> getAllProperties() {
        return storage.keySet();
    }

    /**
     * Get all materials stored in this storage, regardless of whether they have a custom material or whether they exist according to the copycat's block state.
     */
    public Set<BlockState> getAllMaterials() {
        return storage.values().stream().filter(Objects::nonNull).map(MaterialItem::material).collect(Collectors.toSet());
    }

    /**
     * Get all material items stored in this storage, regardless of whether they have a custom material or whether they exist according to the copycat's block state.
     */
    public Set<MaterialItem> getAllMaterialItems() {
        return storage.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Get all consumed items stored in this storage. Empty stacks are not included.
     */
    public List<ItemStack> getAllConsumedItems() {
        return storage.values().stream().filter(Objects::nonNull).map(MaterialItem::consumedItem).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
    }

    /**
     * Get a map of all properties and their corresponding materials stored in this storage.
     */
    public Map<String, BlockState> getMaterialMap() {
        return storage.entrySet().stream().filter(s -> s.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, s -> s.getValue().material));
    }

    /**
     * Check if a specific property has a custom material.
     */
    public boolean hasCustomMaterial(String property) {
        MaterialItem item = storage.get(property);
        return item != null && item.hasCustomMaterial();
    }

    /**
     * Modify the keys of all stored materials using the provided key mapper.
     *
     * @param keyMapper A function which accepts a key and returns a new key where the corresponding material should be stored.
     */
    public void remapStorage(Function<String, String> keyMapper) {
        Map<String, MaterialItem> newStorage = new HashMap<>();
        storage.forEach((key, materialItem) -> {
            if (materialItem != null)
                newStorage.put(keyMapper.apply(key), materialItem);
        });
        storage = newStorage;
    }

    public CompoundTag serialize(HolderLookup.Provider registries) {
        CompoundTag root = new CompoundTag();
        root.putInt(SCHEMA_KEY, SCHEMA_VERSION);
        storage.forEach((key, materialItem) -> {
            if (materialItem != null)
                root.put(key, materialItem.serialize(registries));
        });
        return root;
    }

    public CompoundTag serializeSafe(HolderLookup.Provider registries) {
        CompoundTag root = new CompoundTag();
        root.putInt(SCHEMA_KEY, SCHEMA_VERSION);
        storage.forEach((key, materialItem) -> {
            if (materialItem != null)
                root.put(key, materialItem.serializeSafe(registries));
        });
        return root;
    }

    /**
     * Load material data from NBT. Both the current schema and the legacy flat format written by
     * older versions are accepted, but only keys declared by this storage are loaded; foreign or
     * unknown entries are dropped rather than stored, which migrates poisoned pre-rework data on
     * first read.
     */
    public boolean deserialize(CompoundTag tag, HolderLookup.Provider registries) {
        AtomicBoolean anyUpdated = new AtomicBoolean(false);
        tag.getAllKeys().forEach(key -> {
            if (SCHEMA_KEY.equals(key) || !storage.containsKey(key))
                return;
            MaterialItem newVersion = MaterialItem.deserialize(tag.getCompound(key), registries);
            MaterialItem oldVersion = storage.put(key, newVersion);
            if (oldVersion != null &&
                    (newVersion.material() != oldVersion.material() || newVersion.enableCT() != oldVersion.enableCT()) &&
                    !anyUpdated.get()) {
                anyUpdated.set(true);
            }
        });
        return anyUpdated.get();
    }

    public void clear() {
        storage.clear();
    }

    /**
     * Stores copycat data for a single part of a multi-state copycat.
     */
    public static class MaterialItem {

        private BlockState material;
        private ItemStack consumedItem;
        private boolean enableCT;

        public MaterialItem(BlockState material, ItemStack consumedItem) {
            this(material, consumedItem, true);
        }

        public MaterialItem(BlockState material, ItemStack consumedItem, boolean enableCT) {
            this.material = material;
            this.consumedItem = consumedItem;
            this.enableCT = enableCT;
        }

        public CompoundTag serialize(HolderLookup.Provider registries) {
            CompoundTag root = new CompoundTag();
            root.put("material", NbtUtils.writeBlockState(material));
            root.put("consumedItem", ItemUtils.serializeNBT(consumedItem, registries));
            root.putBoolean("enableCT", enableCT);
            return root;
        }

        public CompoundTag serializeSafe(HolderLookup.Provider registries) {
            CompoundTag root = new CompoundTag();
            root.put("material", NbtUtils.writeBlockState(material));
            ItemStack stackWithoutComponents = new ItemStack(consumedItem.getItemHolder(), consumedItem.getCount(), DataComponentPatch.EMPTY);
            root.put("consumedItem", ItemUtils.serializeNBT(stackWithoutComponents, registries));
            root.putBoolean("enableCT", enableCT);
            return root;
        }

        public static MaterialItem deserialize(CompoundTag tag, HolderLookup.Provider registries) {
            return new MaterialItem(
                    NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("material")),
                    ItemStack.parseOptional(registries, tag.getCompound("consumedItem")),
                    !tag.contains("enableCT") || tag.getBoolean("enableCT")
            );
        }

        public BlockState material() {
            return material;
        }

        public ItemStack consumedItem() {
            return consumedItem;
        }

        public boolean enableCT() {
            return enableCT;
        }

        public void setMaterial(BlockState material) {
            this.material = material;
        }

        public void setConsumedItem(ItemStack stack) {
            consumedItem = ItemUtils.copyStackWithSize(stack, 1);
        }

        public void setEnableCT(boolean enableCT) {
            this.enableCT = enableCT;
        }

        public boolean hasCustomMaterial() {
            return !material.is(AllBlocks.COPYCAT_BASE.get());
        }
    }

}
