/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.serial.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SerialBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public class SerialBindingConstants {

    /**
     * Binding ID
     */
    private static final String BINDING_ID = "serial";

    /**
     * Serial port identifier name
     */
    public static final String SERIAL_PORT_IDENTIFIER = "org.openhab.binding.serial";

    /**
     * Thing(s)
     */
    public static final ThingTypeUID THING_TYPE_SERIAL = new ThingTypeUID(BINDING_ID, "device");

    /**
     * List of channels
     */
    public static final String CHANNEL_1 = "channel1";

    /**
     * Serial port read time out in milliseconds.
     */
    public static final int SERIAL_PORT_READ_TIMEOUT_MILLISECONDS = 15 * 1000;

    /**
     * The receive threshold/time out set on the serial port.
     */
    public static final int SERIAL_TIMEOUT_MILLISECONDS = 1000;
}
