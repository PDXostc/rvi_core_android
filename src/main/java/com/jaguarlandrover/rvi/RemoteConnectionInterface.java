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

public interface RemoteConnectionInterface
{
    void sendRviRequest(DlinkPacket dlinkPacket);

    boolean isConnected();
    boolean isEnabled();

    void connect();
    void disconnect();

    void setRemoteConnectionListener(RemoteConnectionListener remoteConnectionListener); // TODO: Probably bad architecture to expect interface implementations to correctly set and use an
                                                                                         // TODO, cont: instance of the RemoteConnectionListener. Not sure what the best Java paradigm would be in this case
    public interface RemoteConnectionListener
    {
        public void onRemoteConnectionDidConnect();

        public void onRemoteConnectionDidFailToConnect(Error error);

        public void onRemoteConnectionDidReceiveData(String data);

        public void onDidSendDataToRemoteConnection();

        public void onDidFailToSendDataToRemoteConnection(Error error);
    }
}
