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

import static org.openhab.binding.serial.internal.SerialBindingConstants.CHANNEL_1;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.io.transport.serial.SerialPortManager;
import org.openhab.binding.serial.internal.connector.SerialConnector;
import org.openhab.binding.serial.internal.connector.SerialConnectorErrorEvent;
import org.openhab.binding.serial.internal.connector.SerialConnectorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SerialHandler} is responsible for handling commands and connections.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public class SerialHandler extends BaseThingHandler implements SerialConnectorListener {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(SerialHandler.class);

    /**
     * Serial Port Manager.
     */
    private final SerialPortManager serialPortManager;

    /**
     * Connector
     */
    private @Nullable SerialConnector connector;

    /**
     * Check connection and restart if necessary.
     */
    private @NonNullByDefault({}) ScheduledFuture<?> watchdog;

    /**
     * Create new Serial Handler
     *
     * @param thing The thing
     * @param serialPortManager The serial port manager
     */
    public SerialHandler(Thing thing, SerialPortManager serialPortManager) {
        super(thing);
        this.serialPortManager = serialPortManager;
    }

    @Override
    public void initialize() {
        final SerialConfiguration config = getConfigAs(SerialConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Initializing Serial Binding with configuration: {}", config.toString());
        connector = new SerialConnector(serialPortManager, config, this);
        connector.connect();
        if (config.restartTimer > 0) {
            watchdog = scheduler.scheduleWithFixedDelay(this::isAlive, config.restartTimer, config.restartTimer,
                    TimeUnit.SECONDS);
        }
    }

    @Override
    public void dispose() {
        if (watchdog != null) {
            watchdog.cancel(true);
            watchdog = null;
        }
        if (connector != null) {
            connector.close();
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command
        }
    }

    private void isAlive() {
        if (connector != null) {
            connector.connect();
        }
    }

    @Override
    public void handleError(SerialConnectorErrorEvent errorEventS) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleData(byte[] buffer, int length) {
        // TODO Auto-generated method stub

    }

}
