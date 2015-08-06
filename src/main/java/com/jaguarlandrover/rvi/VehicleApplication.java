package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    VehicleApplication.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.content.Context;

import java.util.ArrayList;

public class VehicleApplication
{
    private final static String TAG = "RVI:VehicleApplication";

    private String mAppIdentifier;
    private String mDomain;
    private String mRemotePrefix;

    private String mLocalPrefix;

    private ArrayList<VehicleService> mServices;

    public interface VehicleApplicationListener
    {
        public void onServiceUpdated(VehicleService service);
    }

    private VehicleApplicationListener mListener;

    public VehicleApplication(Context context, String appIdentifier, String domain, String remotePrefix, ArrayList<String> services) {
        mAppIdentifier = appIdentifier;
        mDomain = domain;
        mRemotePrefix = remotePrefix;

        mLocalPrefix = RemoteVehicleNode.getLocalServicePrefix(context);
        //"/android/" + UUID.randomUUID().toString();//987654321"; // TODO: Generate randomly

        mServices = makeServices(services);
    }

    private ArrayList<VehicleService> makeServices(ArrayList<String> serviceIdentifiers) {
        ArrayList<VehicleService> services = new ArrayList<>(serviceIdentifiers.size());
        for (String serviceIdentifier : serviceIdentifiers)
            services.add(makeService(serviceIdentifier));

        return services;
    }

    private VehicleService makeService(String serviceIdentifier) {
        return new VehicleService(serviceIdentifier, mAppIdentifier, mDomain, mRemotePrefix, mLocalPrefix);
    }

    public VehicleService getService(String serviceIdentifier) {
        for (VehicleService service : mServices)
            if (service.getServiceIdentifier().equals(serviceIdentifier) || service.getServiceIdentifier()
                                                                                   .equals("/" + serviceIdentifier))
                return service;

        return null;
    }

    public void updateService(String service) {
        DlinkReceivePacket serviceInvokeJSONObject = new DlinkReceivePacket(getService(service));
        RemoteConnectionManager.sendPacket(serviceInvokeJSONObject);
    }

    public void serviceUpdated(VehicleService service) {
        VehicleService ourService = getService(service.getServiceIdentifier());

        ourService.setValue(service.getValue());

        mListener.onServiceUpdated(ourService);
    }

    public String getDomain() {
        return mDomain;
    }

    public String getRemotePrefix() {
        return mRemotePrefix;
    }

    public void setRemotePrefix(String remotePrefix) {
        mRemotePrefix = remotePrefix;
        //mServices.removeAll(mServices);

        for (VehicleService service : mServices)
            service.setRemotePrefix(remotePrefix);
    }

    public VehicleApplicationListener getListener() {
        return mListener;
    }

    public void setListener(VehicleApplicationListener listener) {
        mListener = listener;
    }

    public ArrayList<VehicleService> getServices() {
        return mServices;
    }

    public String getAppIdentifier() {
        return mAppIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        mAppIdentifier = appIdentifier;
    }
}
