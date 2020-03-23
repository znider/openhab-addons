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
package org.openhab.binding.serial.internal.connector;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Listener interface for connection.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public interface SerialConnectorListener {

    /**
     * Handle error.
     *
     * @param message
     * @param exception
     */
    void handleError(SerialConnectorErrorEvent errorEvent);

    /**
     * Handle data.
     *
     * @param buffer The byte buffer with the data
     * @param length Length of the data in buffer. Buffer may be larger than data in buffer, therefore always use
     *            length
     */
    void handleData(byte[] buffer, int length);

}