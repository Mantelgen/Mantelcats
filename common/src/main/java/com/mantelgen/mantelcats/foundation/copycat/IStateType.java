package com.mantelgen.mantelcats.foundation.copycat;

import com.mantelgen.mantelcats.foundation.copycat.multistate.IMultiStateCopycatBlock;

/**
 * An interface to identify a copycats type.
 *
 * @deprecated To identify copycats, check that the block class is an instanceof {@link ICopycatBlock}. To identify multi-state copycats, check that the block class is an instanceof {@link IMultiStateCopycatBlock}.
 */
@Deprecated(since = "1.4")
public interface IStateType {

    /*
     * Used to identify a copycats type.
     */
    default StateType stateType() {
        return StateType.SINGULAR;
    }
}
