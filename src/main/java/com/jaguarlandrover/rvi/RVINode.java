package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    RVINode.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * The local RVI node.
 */
public class RVINode
{
    private final static String TAG = "RVI:RVINode";

    private static RVINode ourInstance = new RVINode();
    private static HashMap<String, ServiceBundle> allServiceBundles = new HashMap<>();

    private RVINode() {
        RemoteConnectionManager.setListener(new RemoteConnectionManagerListener()
        {
            @Override
            public void onRVIDidConnect() {
                RemoteConnectionManager.sendPacket(new DlinkAuthPacket());

                announceServices();

                if (mListener != null) mListener.nodeDidConnect();
            }

            @Override
            public void onRVIDidFailToConnect(Error error) {
                if (mListener != null) mListener.nodeDidFailToConnect();
            }

            @Override
            public void onRVIDidDisconnect() {
                if (mListener != null) mListener.nodeDidDisconnect();
            }

            @Override
            public void onRVIDidReceivePacket(DlinkPacket packet) {
                if (packet == null) return;

                if (packet.getClass().equals(DlinkReceivePacket.class)) {
                    handleReceivePacket((DlinkReceivePacket) packet);

                } else if (packet.getClass().equals(DlinkServiceAnnouncePacket.class)) {
                    handleServiceAnnouncePacket((DlinkServiceAnnouncePacket) packet);

                } else if (packet.getClass().equals(DlinkAuthPacket.class)) {
                    handleAuthPacket((DlinkAuthPacket) packet);

                }
            }

            @Override
            public void onRVIDidSendPacket() {

            }

            @Override
            public void onRVIDidFailToSendPacket(Error error) {

            }
        });
    }

    /**
     * Sets the @RVINodeListener listener.
     *
     * @param listener the listener
     */
    public static void setListener(RVINodeListener listener) {
        ourInstance.mListener = listener;
    }

    /**
     * The RVI node listener interface.
     */
    public interface RVINodeListener
    {
        /**
         * Called when the local RVI node successfully connects to a remote RVI node.
         */
        public void nodeDidConnect();

        /**
         * Called when the local RVI node failed to connect to a remote RVI node.
         */
        public void nodeDidFailToConnect();

        /**
         * Called when the local RVI node disconnects from a remote RVI node.
         */
        public void nodeDidDisconnect();

    }

    private RVINodeListener mListener;

    /**
     * Tells the local RVI node to connect to the remote RVI node.
     */
    public static void connect() {
        // are we configured
        // connect
        RemoteConnectionManager.connect();

    }

    /**
     * Tells the local RVI node to disconnect from the remote RVI node.
     */
    public static void disconnect() {
        // disconnect

        RemoteConnectionManager.disconnect();
    }

    /**
     * Add a service bundle to the local RVI node. Adding a service bundle triggers a service announce over the
     * network to the remote RVI node.
     *
     * @param bundle the bundle
     */
    public static void addBundle(ServiceBundle bundle) {
        RVINode.allServiceBundles.put(bundle.getBundleIdentifier(), bundle);
        RVINode.announceServices();
    }

    /**
     * Remove a service bundle from the local RVI node. Removing a service bundle triggers a service announce over the
     * network to the remote RVI node.
     *
     * @param bundle the bundle
     */
    public static void removeBundle(ServiceBundle bundle) {
        RVINode.allServiceBundles.remove(bundle.getBundleIdentifier());
        RVINode.announceServices();
    }

    /**
     * Have the local node announce all it's available services.
     */
    static void announceServices() {
        ArrayList<String> allServices = new ArrayList<>();
        for (ServiceBundle bundle : allServiceBundles.values())
            allServices.addAll(bundle.getFullyQualifiedLocalServiceNames());

        RemoteConnectionManager.sendPacket(new DlinkServiceAnnouncePacket(allServices));
    }

    /**
     * Update service.
     *
     * @param service the service
     */
    static void updateService(VehicleService service) {
        RemoteConnectionManager.sendPacket(new DlinkReceivePacket(service));
    }

    private void handleReceivePacket(DlinkReceivePacket packet) {
        VehicleService service = packet.getService();

        allServiceBundles.get(service.getBundleIdentifier()).serviceUpdated(service);
    }

    private void handleServiceAnnouncePacket(DlinkServiceAnnouncePacket packet) {
        for (String fullyQualifiedRemoteServiceName : packet.getServices()) {

            String[] serviceParts = fullyQualifiedRemoteServiceName.split("/");

            if (serviceParts.length != 5) return;

            String nodeIdentifier = "/" + serviceParts[1] + "/" + serviceParts[2];
            String bundleIdentifier = "/" + serviceParts[3];
            String serviceIdentifier = "/" + serviceParts[4];

            ServiceBundle bundle = allServiceBundles.get(bundleIdentifier);

            if (bundle != null)
                bundle.addRemoteService(serviceIdentifier, nodeIdentifier);
        }
    }

    private void handleAuthPacket(DlinkAuthPacket packet) {

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

    /**
     * Gets the prefix of the local RVI node
     *
     * @param context the application context
     * @return the local prefix
     */
    public static String getLocalNodeIdentifier(Context context) {
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
