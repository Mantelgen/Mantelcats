package com.mantelgen.mantelcats;

import com.mantelgen.mantelcats.config.FeatureToggle;
import com.mantelgen.mantelcats.content.copycat.board.CopycatBoxItem;
import com.mantelgen.mantelcats.content.copycat.board.CopycatCatwalkItem;
import com.mantelgen.mantelcats.foundation.tooltip.CopycatCharacteristics;
import com.mantelgen.mantelcats.foundation.tooltip.CopycatDescription;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class CCItems {

    private static final CreateRegistrate REGISTRATE = Copycats.getRegistrate();


    public static final ItemEntry<CopycatBoxItem> COPYCAT_BOX =
            REGISTRATE.item("copycat_box", CopycatBoxItem::new)
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.CT_TOGGLE,
                            CopycatCharacteristics.MULTI_STATE,
                            CopycatCharacteristics.PRE_ASSEMBLED
                    ))
                    .model(AssetLookup.customBlockItemModel("copycat_base", "box"))
                    .transform(FeatureToggle.registerDependent(CCBlocks.COPYCAT_BOARD))
                    .tag(CCTags.Items.COPYCAT_BOX.tag)
                    .register();

    public static final ItemEntry<CopycatCatwalkItem> COPYCAT_CATWALK =
            REGISTRATE.item("copycat_catwalk", CopycatCatwalkItem::new)
                    .onRegister(CopycatDescription.register(
                            CopycatCharacteristics.COPYCAT,
                            CopycatCharacteristics.CT_TOGGLE,
                            CopycatCharacteristics.MULTI_STATE,
                            CopycatCharacteristics.PRE_ASSEMBLED
                    ))
                    .model(AssetLookup.customBlockItemModel("copycat_base", "catwalk"))
                    .transform(FeatureToggle.registerDependent(CCBlocks.COPYCAT_BOARD))
                    .tag(CCTags.Items.COPYCAT_CATWALK.tag)
                    .register();

    public static void register() {
    }
}
