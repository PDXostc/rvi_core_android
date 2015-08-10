package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RemoteConnectionInterface.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/**
 * The interface Remote connection interface.
 */
interface RemoteConnectionInterface
{
    /**
     * Send rvi request.
     *
     * @param dlinkPacket the dlink packet
     */
    void sendRviRequest(DlinkPacket dlinkPacket);

    /**
     * Is connected.
     *
     * @return the boolean
     */
    boolean isConnected();

    /**
     * Is enabled.
     *
     * @return the boolean
     */
    boolean isEnabled();

    /**
     * Connect void.
     */
    void connect();

    /**
     * Disconnect void.
     */
    void disconnect();

    /**
     * Sets remote connection listener.
     *
     * @param remoteConnectionListener the remote connection listener
     */
    void setRemoteConnectionListener(RemoteConnectionListener remoteConnectionListener); // TODO: Probably bad architecture to expect interface implementations to correctly set and use an

    /**
     * The interface Remote connection listener.
     */
// TODO, cont: instance of the RemoteConnectionListener. Not sure what the best Java paradigm would be in this case
    interface RemoteConnectionListener
    {
        /**
         * On remote connection did connect.
         */
        void onRemoteConnectionDidConnect();

        /**
         * On remote connection did disconnect.
         */
        void onRemoteConnectionDidDisconnect();

        /**
         * On remote connection did fail to connect.
         *
         * @param error the error
         */
        void onRemoteConnectionDidFailToConnect(Error error);

        /**
         * On remote connection did receive data.
         *
         * @param data the data
         */
        void onRemoteConnectionDidReceiveData(String data);

        /**
         * On did send data to remote connection.
         */
        void onDidSendDataToRemoteConnection();

        /**
         * On did fail to send data to remote connection.
         *
         * @param error the error
         */
        void onDidFailToSendDataToRemoteConnection(Error error);
    }
}
