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
 * The type RVI node.
 */
public class RVINode
{
    private final static String TAG = "RVI:RVINode";

    private static RVINode ourInstance = new RVINode();

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
                    VehicleService service = ((DlinkReceivePacket) packet).getService();

                    allServiceBundles.get(service.getBundleIdentifier()).serviceUpdated(service);

                } else if (packet.getClass().equals(DlinkServiceAnnouncePacket.class)) { // TODO: Handle 'remove service' dlink packet, and handle case where new SA packet doesn't announce all the previous services
                    for (String fullyQualifiedRemoteServiceName : ((DlinkServiceAnnouncePacket) packet).getServices()) {

                        String[] serviceParts = fullyQualifiedRemoteServiceName.split("/");

                        if (serviceParts.length != 5) return;

                        String remotePrefix = "/" + serviceParts[1] + "/" + serviceParts[2];
                        String appIdentifier = "/" + serviceParts[3];
                        String serviceIdentifier = "/" + serviceParts[4];

                        allServiceBundles.get(appIdentifier).getService(serviceIdentifier).setRemotePrefix(remotePrefix);
                    }

                    for (VehicleService service : pendingServiceUpdates) {
                        if (service.hasRemotePrefix() && service.getTimeout() >= System.currentTimeMillis()) {
                            RemoteConnectionManager.sendPacket(new DlinkReceivePacket(service));

                            pendingServiceUpdates.remove(service);

                        } else if (service.getTimeout() < System.currentTimeMillis()) {
                            pendingServiceUpdates.remove(service);
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
        });
    }

    private static HashMap<String, ServiceBundle> allServiceBundles = new HashMap<>();

    private static HashSet<VehicleService> pendingServiceUpdates = new HashSet<>();

    //public static RVINodeListener getListener() {
    //    return ourInstance.mListener;
    //}

    /**
     * Sets listener.
     *
     * @param listener the listener
     */
    public static void setListener(RVINodeListener listener) {
        ourInstance.mListener = listener;
    }

    /**
     * The interface RVI node listener.
     */
    public interface RVINodeListener
    {
        /**
         * Node did connect.
         */
        public void nodeDidConnect();

        /**
         * Node did fail to connect.
         */
        public void nodeDidFailToConnect();

        /**
         * Node did disconnect.
         */
        public void nodeDidDisconnect();

    }

    private RVINodeListener mListener;

    /**
     * Connect void.
     */
    public static void connect() {
        // are we configured
        // connect
        RemoteConnectionManager.connect();

    }

    /**
     * Disconnect void.
     */
    public static void disconnect() {
        // disconnect

        RemoteConnectionManager.disconnect();
    }

    /**
     * Add bundle.
     *
     * @param bundle the bundle
     */
    public static void addBundle(ServiceBundle bundle) {
        RVINode.allServiceBundles.put(bundle.getBundleIdentifier(), bundle);
        RVINode.announceServices();
    }

    /**
     * Remove bundle.
     *
     * @param bundle the bundle
     */
    public static void removeBundle(ServiceBundle bundle) {
        RVINode.allServiceBundles.remove(bundle.getBundleIdentifier());
        RVINode.announceServices();
    }

    private static void announceServices() {
        ArrayList<VehicleService> allServices = new ArrayList<>();
        for (ServiceBundle bundle : allServiceBundles.values())
            allServices.addAll(bundle.getLocalServices());

        RemoteConnectionManager.sendPacket(new DlinkServiceAnnouncePacket(allServices));
    }

    /**
     * Update service.
     *
     * @param service the service
     */
    protected static void updateService(VehicleService service) {
        if (service.hasRemotePrefix()) {
            RemoteConnectionManager.sendPacket(new DlinkReceivePacket(service));
        } else {
            pendingServiceUpdates.add(service);
        }
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
     * Gets local service prefix.
     *
     * @param context the context
     * @return the local service prefix
     */
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
