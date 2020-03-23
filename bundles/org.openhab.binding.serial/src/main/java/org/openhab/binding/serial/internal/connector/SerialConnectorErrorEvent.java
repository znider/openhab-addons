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

/**
 * Connection errors.
 *
 * @author Miika Jukka - Initial contribution
 */
public enum SerialConnectorErrorEvent {

    DONT_EXIST("Serial port does not exist"),
    IN_USE("Serial port is already in use"),
    INTERNAL_ERROR("Unexpected internal error"),
    NOT_COMPATIBLE("Serial port is not compatible"),
    READ_ERROR("Read error"),
    WRITE_ERROR("Write error");

    /**
     * Error event details
     */
    private String errorEventDetails = "none";

    private SerialConnectorErrorEvent(String errorEventDetails) {
        this.errorEventDetails = errorEventDetails;
    }

    /**
     * Get event details
     *
     * @return the event details
     */
    public String getErrorEventDetails() {
        return errorEventDetails;
    }
}
