/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.vallox.internal.se.mapper;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.State;

/**
 * Class for ON/OFF channels. The BooleanChannel always has a parent channel. It gets its value using the bit mask from
 * parent channels byte value. Parent channel can have a maximum of 8 childs.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public class BooleanChannel extends ValloxChannel {

    public byte bitMask;
    public String parentChannel;

    /**
     * Create new instance.
     *
     * @param bitMask the bit mask
     * @param parentChannel the parent channel
     */
    public BooleanChannel(int bitMask, String parentChannel) {
        this.bitMask = (byte) bitMask;
        this.parentChannel = parentChannel;
    }

    @Override
    public String getParentChannel() {
        return parentChannel;
    }

    /**
     * Get bit mask
     *
     * @return the bit mask
     */
    public byte getMask() {
        return bitMask;
    }

    @Override
    public byte getVariable() {
        return ChannelDescriptor.get(parentChannel).getVariable();
    }

    @Override
    public State convertToState(byte value) {
        State result = (value & bitMask) != 0 ? OnOffType.ON : OnOffType.OFF;
        return result;
    }

    /**
     * Class for adjustment interval channel
     *
     * @author Miika Jukka - Initial contributor
     */
    public static class AdjustmentInterval extends BooleanChannel {

        /**
         * Create new instance.
         *
         * @param bitMask the bit mask
         * @param parentChannel the parent channel
         */
        public AdjustmentInterval(int bitMask, String parentChannel) {
            super(bitMask, parentChannel);
        }

        @Override
        public byte getVariable() {
            return ChannelDescriptor.get(parentChannel).getVariable();
        }

        @Override
        public String getParentChannel() {
            return parentChannel;
        }

        @Override
        public State convertToState(byte value) {
            int result = value & bitMask;
            return new DecimalType(result);
        }
    }
}
