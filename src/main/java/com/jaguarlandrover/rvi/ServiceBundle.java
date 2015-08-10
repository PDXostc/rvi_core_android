package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    ServiceBundle.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceBundle
{
    private final static String TAG = "RVI:ServiceBundle";

    private String mBundleIdentifier;
    private String mDomain;

//    private String mRemotePrefix;
//    private String mLocalPrefix;

    private HashMap<String, VehicleService> mServices;

    public interface ServiceBundleListener
    {
        public void onServiceUpdated(String serviceIdentifier, Object value);
    }

    private ServiceBundleListener mListener;

    public ServiceBundle(Context context, String domain, String bundleIdentifier, /*String remotePrefix,*/ ArrayList<String> servicesIdentifiers) {
        mBundleIdentifier = bundleIdentifier;
        mDomain = domain;

        //mRemotePrefix = remotePrefix;
        //mLocalPrefix = RVINode.getLocalServicePrefix(context);
        //"/android/" + UUID.randomUUID().toString();//987654321"; // TODO: Generate randomly

        mServices = makeServices(servicesIdentifiers, RVINode.getLocalServicePrefix(context));
    }

    private HashMap<String, VehicleService> makeServices(ArrayList<String> serviceIdentifiers, String localPrefix) {
        HashMap<String, VehicleService> services = new HashMap<>(serviceIdentifiers.size());
        for (String serviceIdentifier : serviceIdentifiers)
            services.put(serviceIdentifier, makeService(serviceIdentifier, localPrefix));

        return services;
    }

    private VehicleService makeService(String serviceIdentifier, String localPrefix) {
        return new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, null, localPrefix);//mRemotePrefix, mLocalPrefix);
    }

    public VehicleService getService(String serviceIdentifier) {
//        for (VehicleService service : mServices)
//            if (service.getServiceIdentifier().equals(serviceIdentifier) || service.getServiceIdentifier()
//                                                                                   .equals("/" + serviceIdentifier))
//                return service;

        VehicleService service;
        if (null != (service = mServices.get(serviceIdentifier)))
            return service;

        if (null != (service = mServices.get("/" + serviceIdentifier)))
            return service;

        mServices.put(serviceIdentifier, service = new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, null, null));

        return service;
    }

    public void updateService(String serviceIdentifier, Object parameters, Long timeout) {
        VehicleService service = getService(serviceIdentifier);

        //if (service == null) mServices.put(serviceIdentifier, service = new VehicleService(serviceIdentifier, mBundleIdentifier, mDomain, null, null));

        service.setParameters(parameters);
        service.setTimeout(timeout);

        RVINode.updateService(service);
    }

    public void serviceUpdated(VehicleService service) {
//        VehicleService ourService = getService(service.getServiceIdentifier());
//
//        ourService.setParameters(service.getParameters());

        if (mListener != null) mListener.onServiceUpdated(service.getServiceIdentifier(), service.getParameters()); // TODO: This code can pass through a service that might not exist locally
    }

//    public String getDomain() {
//        return mDomain;
//    }

//    public String getRemotePrefix() {
//        return mRemotePrefix;
//    }
//
//    public void setRemotePrefix(String remotePrefix) {
//        mRemotePrefix = remotePrefix;
//        //mServices.removeAll(mServices);
//
//        for (VehicleService service : mServices)
//            service.setRemotePrefix(remotePrefix);
//    }

//    public ServiceBundleListener getListener() {
//        return mListener;
//    }

    public void setListener(ServiceBundleListener listener) {
        mListener = listener;
    }

    public HashMap<String, VehicleService> getServices() {
        return mServices;
    }

    public ArrayList<VehicleService> getLocalServices() {
        ArrayList<VehicleService> localServices = new ArrayList<>(mServices.size());
        for (VehicleService service : mServices.values())
            if (service.getFullyQualifiedLocalServiceName() != null)
                localServices.add(service);

        return localServices;
    }

    public ArrayList<VehicleService> getRemoteServices() {
        ArrayList<VehicleService> remoteServices = new ArrayList<>(mServices.size());
        for (VehicleService service : mServices.values())
            if (service.getFullyQualifiedRemoteServiceName() != null)
                remoteServices.add(service);

        return remoteServices;
    }

    public String getBundleIdentifier() {
        return mBundleIdentifier;
    }

//    public void setAppIdentifier(String appIdentifier) {
//        mBundleIdentifier = appIdentifier;
//    }
}
