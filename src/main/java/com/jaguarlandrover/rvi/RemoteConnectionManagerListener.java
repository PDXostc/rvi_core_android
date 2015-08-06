package com.jaguarlandrover.rvi;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RemoteConnectionManagerListener.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 6/30/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public interface RemoteConnectionManagerListener
{
    public void onRVIDidConnect();

    public void onRVIDidFailToConnect(Error error);

    public void onRVIDidDisconnect();

    public void onRVIDidReceivePacket(DlinkPacket packet);

    public void onRVIDidSendPacket();

    public void onRVIDidFailToSendPacket(Error error);
}
