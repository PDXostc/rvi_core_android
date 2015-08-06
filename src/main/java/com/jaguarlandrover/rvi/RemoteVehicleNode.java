package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RemoteVehicleNode.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 7/1/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class RemoteVehicleNode implements RemoteConnectionManagerListener
{
    private final static String TAG = "RVI:RemoteVehicleNode";

    private static RemoteVehicleNode ourInstance = new RemoteVehicleNode();

    private RemoteVehicleNode() {
        RemoteConnectionManager.setListener(this);
    }

    private static HashSet<VehicleApplication> allApps = new HashSet<>();

    public static RemoteVehicleNodeListener getListener() {
        return ourInstance.mListener;
    }

    public static void setListener(RemoteVehicleNodeListener listener) {
        ourInstance.mListener = listener;
    }

    public interface RemoteVehicleNodeListener
    {
        public void nodeDidConnect();

        public void nodeDidFailToConnect();

        public void nodeDidDisconnect();

    }

    private RemoteVehicleNodeListener mListener;

    public static void connect() {
        // are we configured
        // connect
        RemoteConnectionManager.connect();

    }

    public static void disconnect() {
        // disconnect

        RemoteConnectionManager.disconnect();
    }

    // TODO: Change allApps to a set, to remove duplication
    public static void addApp(VehicleApplication app) {
        RemoteVehicleNode.allApps.add(app);
        RemoteVehicleNode.announceServices();
    }

    public static void removeApp(VehicleApplication app) {
        RemoteVehicleNode.allApps.remove(app);
        RemoteVehicleNode.announceServices();
    }

    // TODO: Change all services to a set, to remove duplication
    private static void announceServices() {
        ArrayList<VehicleService> allServices = new ArrayList<>();
        for (VehicleApplication app : allApps)
            allServices.addAll(app.getServices());

        RemoteConnectionManager.sendPacket(new DlinkServiceAnnouncePacket(allServices));
    }

    @Override
    public void onRVIDidConnect() {
        RemoteConnectionManager.sendPacket(new DlinkAuthPacket());

        announceServices();

        mListener.nodeDidConnect();
    }

    @Override
    public void onRVIDidFailToConnect(Error error) {
        mListener.nodeDidFailToConnect();
    }

    @Override
    public void onRVIDidDisconnect() {
        mListener.nodeDidDisconnect();
    }

    @Override
    public void onRVIDidReceivePacket(DlinkPacket packet) {
        if (packet == null) return;

        if (packet.getClass().equals(DlinkReceivePacket.class)) {
            VehicleService service = ((DlinkReceivePacket) packet).getService();

            for (VehicleApplication app : allApps) {
                if (app.getAppIdentifier().equals(service.getAppIdentifier())) {
                    app.serviceUpdated(service);
                }
            }
        }
    }

    @Override
    public void onRVIDidSendPacket() {

    }

    @Override
    public void onRVIDidFailToSendPacket(Error error) {

    }

    private final static String SHARED_PREFS_STRING         = "com.rvisdk.settings";
    private final static String LOCAL_SERVICE_PREFIX_STRING = "localServicePrefix";

    // TODO: Test and verify this function
    private static String uuidB58String() {
        UUID uuid = UUID.randomUUID();
        String b64Str;

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        b64Str = Base64.encodeToString(bb.array(), Base64.DEFAULT);
        b64Str = b64Str.split("=")[0];

        b64Str = b64Str.replace('+', 'P');
        b64Str = b64Str.replace('/', 'S'); /* Reduces likelihood of uniqueness but stops non-alphanumeric characters from screwing up any urls or anything */

        return b64Str;
    }

    public static String getLocalServicePrefix(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS_STRING, MODE_PRIVATE);
        String localServicePrefix;

        if ((localServicePrefix = sharedPrefs.getString(LOCAL_SERVICE_PREFIX_STRING, null)) == null)
            localServicePrefix = "/android/" + uuidB58String();

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(LOCAL_SERVICE_PREFIX_STRING, localServicePrefix);
        editor.apply();

        return localServicePrefix;
    }
}
