package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RemoteConnectionManager.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Log;

public class RemoteConnectionManager implements RemoteConnectionInterface.RemoteConnectionListener, DlinkPacketParser.DlinkPacketParserListener
{
    private final static String TAG = "RVI:RemoteCon...Manager";

    private static RemoteConnectionManager ourInstance = new RemoteConnectionManager();

    private boolean mUsingProxyServer;

    private ServerConnection    mProxyServerConnection;
    private BluetoothConnection mBluetoothConnection;
    private ServerConnection    mDirectServerConnection;

    private DlinkPacketParser mDataParser;

    private RemoteConnectionManagerListener mListener;

    private RemoteConnectionManager() {
        mDataParser = new DlinkPacketParser(this);

        mProxyServerConnection = new ServerConnection();
        mBluetoothConnection = new BluetoothConnection();
        mDirectServerConnection = new ServerConnection();
    }

    public static void connect() {
        ourInstance.closeConnections();

        RemoteConnectionInterface remoteConnection = ourInstance.selectEnabledRemoteConnection();

        if (remoteConnection == null) return;

        remoteConnection
                .setRemoteConnectionListener(ourInstance); // TODO: Doing it this way, dynamically selecting a connection at the beginning and later when sending messages,
        // TODO, cont': but only setting the listener here, will lead to funny async race conditions later; fix.
        remoteConnection.connect();
    }

    public static void disconnect() {
        ourInstance.closeConnections();
        ourInstance.mDataParser.clear();
        ourInstance.mListener.onRVIDidDisconnect();
    }

    public static void sendPacket(DlinkPacket dlinkPacket) {
        Log.d(TAG, Util.getMethodName());

        RemoteConnectionInterface remoteConnection = ourInstance.selectConnectedRemoteConnection();

        if (remoteConnection == null) return; // TODO: Implement a cache to send out stuff after a connection has been established

        remoteConnection.sendRviRequest(dlinkPacket);
    }

    private RemoteConnectionInterface selectConnectedRemoteConnection() {
        if (mDirectServerConnection.isEnabled() && mDirectServerConnection.isConnected() && !mUsingProxyServer)
            return mDirectServerConnection;
        if (mProxyServerConnection.isEnabled() && mProxyServerConnection.isConnected() && mUsingProxyServer)
            return mProxyServerConnection;
        if (mBluetoothConnection.isEnabled() && mBluetoothConnection.isConnected()) {
            return mBluetoothConnection;
        }

        return null;
    }

    private RemoteConnectionInterface selectEnabledRemoteConnection() { // TODO: This is going to be buggy if a connection is enabled but not connected; the other connections won't have connected
        if (mDirectServerConnection.isEnabled() && !mUsingProxyServer)     // TODO: Rewrite better 'chosing' code
            return mDirectServerConnection;
        if (mProxyServerConnection.isEnabled() && mUsingProxyServer)
            return mProxyServerConnection;
        if (mBluetoothConnection.isEnabled()) {
            return mBluetoothConnection;
        }

        return null;
    }

    private void closeConnections() {
        mDirectServerConnection.disconnect();
        mProxyServerConnection.disconnect();
        mBluetoothConnection.disconnect();
    }

    @Override
    public void onRemoteConnectionDidConnect() {
        mListener.onRVIDidConnect();
    }

    @Override
    public void onRemoteConnectionDidFailToConnect(Error error) {
        mListener.onRVIDidFailToConnect(error);
    }

    @Override
    public void onRemoteConnectionDidReceiveData(String data) {
        mDataParser.parseData(data);
    }

    @Override
    public void onDidSendDataToRemoteConnection() {
        mListener.onRVIDidSendPacket();
    }

    @Override
    public void onDidFailToSendDataToRemoteConnection(Error error) {
        mListener.onRVIDidFailToSendPacket(error);
    }

    @Override
    public void onPacketParsed(DlinkPacket packet) {
        mListener.onRVIDidReceivePacket(packet);
    }

    public static void setServerUrl(String serverUrl) {
        RemoteConnectionManager.ourInstance.mDirectServerConnection.setServerUrl(serverUrl);
    }

    public static void setServerPort(Integer serverPort) {
        RemoteConnectionManager.ourInstance.mDirectServerConnection.setServerPort(serverPort);
    }

    public static void setProxyServerUrl(String proxyServerUrl) {
        RemoteConnectionManager.ourInstance.mProxyServerConnection.setServerUrl(proxyServerUrl);
    }

    public static void setProxyServerPort(Integer proxyServerPort) {
        RemoteConnectionManager.ourInstance.mProxyServerConnection.setServerPort(proxyServerPort);
    }

    public static void setUsingProxyServer(boolean usingProxyServer) {
        RemoteConnectionManager.ourInstance.mUsingProxyServer = usingProxyServer;
    }

    public static RemoteConnectionManagerListener getListener() {
        return RemoteConnectionManager.ourInstance.mListener;
    }

    public static void setListener(RemoteConnectionManagerListener listener) {
        RemoteConnectionManager.ourInstance.mListener = listener;
    }
}
