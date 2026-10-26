package com.mantelgen.mantelcats.fabric.mixin.foundation.copycat.multistate;

import com.mantelgen.mantelcats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.multistate.MaterialItemStorage;
import com.mantelgen.mantelcats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;
import com.mantelgen.mantelcats.content.copycat.cogwheel.CopycatCogWheelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;

/**
 * Implement platform-specific methods for multi-state copycat block entities.
 * <p>
 * All multi-state copycats should register their block entities here instead of writing their own platform-specific implementations.
 */
@SuppressWarnings("deprecation")
public class MultiStateCopycatBlockEntityMixin {
    @Mixin({
            MultiStateCopycatBlockEntity.class,
            CopycatCogWheelBlockEntity.class
    })
    public static abstract class BlockEntityWithoutAttachmentData extends SmartBlockEntity implements IMultiStateCopycatBlockEntity, RenderAttachmentBlockEntity {

        public BlockEntityWithoutAttachmentData(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public @Nullable Object getRenderAttachmentData() {
            MaterialItemStorage storage = getMaterialItemStorage();
            if (storage == null)
                return Collections.emptyMap();
            synchronized (storage) {
                return Collections.synchronizedMap(storage.getMaterialMap());
            }
        }
    }
}

