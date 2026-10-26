package com.mantelgen.mantelcats.neoforge.mixin.foundation.copycat;

import com.mantelgen.mantelcats.content.copycat.sliding_door.CopycatSlidingDoorBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.CCCopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.ICopycatBlockEntity;
import com.mantelgen.mantelcats.foundation.copycat.model.neoforge.CopycatModelNeoForge;
import com.mantelgen.mantelcats.content.copycat.fluid_pipe.CopycatFluidPipeBlockEntity;
import com.mantelgen.mantelcats.content.copycat.fluid_pipe.CopycatStraightPipeBlockEntity;
import com.mantelgen.mantelcats.content.copycat.shaft.CopycatShaftBlockEntity;
import com.mantelgen.mantelcats.utility.neoforge.ModelDataUtils;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Implement platform-specific methods for copycat block entities.
 * <p>
 * All non-multi-state copycats should register their block entities here instead of writing their own platform-specific implementations.
 */
@Mixin({
        CCCopycatBlockEntity.class,
        CopycatFluidPipeBlockEntity.class,
        CopycatStraightPipeBlockEntity.class,
        CopycatShaftBlockEntity.class,
        CopycatSlidingDoorBlockEntity.class
})
public abstract class CopycatBlockEntityMixin extends SmartBlockEntity implements ICopycatBlockEntity {

    public CopycatBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @Unique(silent = true)
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelDataUtils.mergeData(
                super.getModelData(),
                ModelData.builder()
                        .with(CopycatModelNeoForge.MATERIAL_PROPERTY, getMaterial())
                        .build()
        ).build();
    }
}
