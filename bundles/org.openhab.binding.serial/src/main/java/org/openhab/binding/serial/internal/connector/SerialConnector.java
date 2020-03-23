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

import static org.openhab.binding.serial.internal.SerialBindingConstants.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.TooManyListenersException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.util.HexUtils;
import org.eclipse.smarthome.io.transport.serial.PortInUseException;
import org.eclipse.smarthome.io.transport.serial.SerialPort;
import org.eclipse.smarthome.io.transport.serial.SerialPortEvent;
import org.eclipse.smarthome.io.transport.serial.SerialPortEventListener;
import org.eclipse.smarthome.io.transport.serial.SerialPortIdentifier;
import org.eclipse.smarthome.io.transport.serial.SerialPortManager;
import org.eclipse.smarthome.io.transport.serial.UnsupportedCommOperationException;
import org.openhab.binding.serial.internal.SerialConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SerialConnector} is responsible for handling connection to serial port.
 *
 * @author Miika Jukka - Initial contribution
 */
@NonNullByDefault
public class SerialConnector implements SerialPortEventListener {

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(SerialConnector.class);

    /**
     * Connector listener.
     */
    private final SerialConnectorListener listener;

    /**
     * Manager to get new port from.
     */
    private final SerialPortManager portManager;

    /**
     * The serial port to use
     */
    private @Nullable SerialPort serialPort;

    /**
     * Input stream of the serial port.
     */
    private @Nullable BufferedInputStream inputStream;

    /**
     * Output stream of the serial port
     */
    private @Nullable BufferedOutputStream outputStream;

    /**
     * 1Kbyte buffer for storing received data.
     */
    private final byte[] buffer = new byte[1024];

    /**
     * Read lock to have 1 process reading at a time.
     */
    private final Object readLock = new Object();

    /**
     * Connection state.
     */
    private boolean open;

    /**
     * Configuration
     */
    private @Nullable SerialConfiguration config;

    /**
     * Create connection manager
     */
    public SerialConnector(SerialPortManager portManager, @Nullable SerialConfiguration config,
            SerialConnectorListener listener) {
        this.portManager = portManager;
        this.config = config;
        this.listener = listener;
    }

    @SuppressWarnings("null")
    public void connect() {
        if (isOpen() || config != null) {
            return;
        }
        try {
            logger.debug("Connecting to {}", config.serialPort);

            SerialPortIdentifier portIdentifier = portManager.getIdentifier(config.serialPort);

            if (portIdentifier == null) {
                throw new IOException("No such port: " + config.serialPort);
            }
            serialPort = portIdentifier.open(SERIAL_PORT_IDENTIFIER, SERIAL_PORT_READ_TIMEOUT_MILLISECONDS);
            serialPort.setSerialPortParams(config.baudRate, config.dataBits, config.stopBits, config.parity);

            this.inputStream = new BufferedInputStream(serialPort.getInputStream());
            this.outputStream = new BufferedOutputStream(serialPort.getOutputStream());
            open = true;

            serialPort.addEventListener(this);

            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnBreakInterrupt(true);
            serialPort.notifyOnFramingError(true);
            serialPort.notifyOnOverrunError(true);
            serialPort.notifyOnParityError(true);

            serialPort.enableReceiveThreshold(SERIAL_TIMEOUT_MILLISECONDS);
            serialPort.enableReceiveTimeout(SERIAL_TIMEOUT_MILLISECONDS);

            logger.debug("Connected to {}", config.serialPort);
        } catch (TooManyListenersException e) {
            logger.debug("Too many listeners", e);

        } catch (PortInUseException e) {
            logger.debug("Port in use", e);

        } catch (UnsupportedCommOperationException | IOException e) {
            logger.debug("Unsupported com operation -> " + e.getMessage(), e);
        }
    }

    /**
     * Check connection state
     *
     * @return Return true if connector is open
     */
    public boolean isOpen() {
        return open;
    }

    public void restart() throws IOException {
        close();
        connect();
    }

    /**
     * Closes the connector.
     */
    @SuppressWarnings("null")
    public void close() {
        open = false;
        if (serialPort != null) {
            serialPort.removeEventListener();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ioe) {
                logger.debug("Failed to close serial port inputstream", ioe);
            }
            serialPort.close();
        }
        serialPort = null;
        logger.debug("Serial connection closed");
    }

    @Override
    public void serialEvent(@Nullable SerialPortEvent seEvent) {
        if (seEvent == null) {
            return;
        }
        if (logger.isTraceEnabled() && SerialPortEvent.DATA_AVAILABLE != seEvent.getEventType()) {
            logger.trace("Serial event: {}, value:{}", seEvent.getEventType(), seEvent.getNewValue());
        }
        try {
            switch (seEvent.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    dataAvailable();
                    break;
                case SerialPortEvent.BI:
                    handleError("Break interrupt", seEvent);
                    break;
                case SerialPortEvent.FE:
                    handleError("Frame error", seEvent);
                    break;
                case SerialPortEvent.OE:
                    handleError("Overrun error", seEvent);
                    break;
                case SerialPortEvent.PE:
                    handleError("Parity error", seEvent);
                    break;
                default: // do nothing
            }
        } catch (RuntimeException re) {
            logger.warn("RuntimeException during handling serial event: {}", seEvent.getEventType(), re);
        }
    }

    /**
     * Handle error event
     *
     * @param typeName
     * @param portEvent
     */
    public void handleError(String typeName, SerialPortEvent portEvent) {
        logger.trace("New serial port event: {}", typeName);
        listener.handleError(SerialConnectorErrorEvent.READ_ERROR);
    }

    /**
     * Read available data
     */
    public void dataAvailable() {
        try {
            synchronized (readLock) {
                BufferedInputStream localInputStream = inputStream;

                if (localInputStream != null) {
                    int bytesAvailable = localInputStream.available();
                    while (bytesAvailable > 0) {
                        int bytesAvailableRead = localInputStream.read(buffer, 0,
                                Math.min(bytesAvailable, buffer.length));

                        if (open && bytesAvailableRead > 0) {
                            listener.handleData(buffer, bytesAvailableRead);
                        } else {
                            logger.debug("Expected bytes {} to read, but {} bytes were read", bytesAvailable,
                                    bytesAvailableRead);
                        }
                        bytesAvailable = localInputStream.available();
                    }
                }
            }
        } catch (IOException e) {
            listener.handleError(SerialConnectorErrorEvent.READ_ERROR);
            logger.debug("Exception while reading: ", e);
        }
    }

    /**
     * Write and flush to output stream
     *
     * @param bytes the bytes to write
     */
    @SuppressWarnings("null")
    public void writeToOutputStream(byte[] bytes) {
        if (outputStream != null) {
            try {
                outputStream.write(bytes);
                outputStream.flush();
                logger.debug("Wrote {}", HexUtils.bytesToHex(bytes, "-"));
            } catch (IOException e) {
                listener.handleError(SerialConnectorErrorEvent.WRITE_ERROR);
                logger.debug("Write to output stream failed: ", e);

            }
        } else {
            logger.debug("Output stream is null");
        }
    }
}