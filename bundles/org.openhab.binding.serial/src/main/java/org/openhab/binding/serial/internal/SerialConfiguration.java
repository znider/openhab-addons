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

/**
 * The {@link SerialConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public class SerialConfiguration {

    /**
     * Serial port.
     */
    public String serialPort = "none";

    /**
     * Baud rate.
     */
    public int baudRate = 9600;

    /**
     * Data bits.
     */
    public int dataBits = 8;

    /**
     * Stop bits.
     */
    public int stopBits = 1;

    /**
     * Parity.
     */
    public int parity = 0;

    /**
     * Restart timer
     */
    public int restartTimer = 0;

    /**
     * String representation of this class
     */
    @Override
    public String toString() {
        return String.format(
                "Port: [%s], Baudrate: [%s], Data bits: [%s], Stop bits: [%s], Parity: [%s], Restart timer: [%s]",
                serialPort, baudRate, dataBits, stopBits, parity, restartTimer);
    }
}
