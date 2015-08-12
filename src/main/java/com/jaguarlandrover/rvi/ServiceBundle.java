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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * The bundle of related services, a.k.a. an application. For example "body" or "hvac".
 */
public class ServiceBundle
{
    private final static String TAG = "RVI:ServiceBundle";

    private String mBundleIdentifier;
    private String mDomain;
    private String mLocalNodeIdentifier;

    private HashMap<String, VehicleService> mLocalServices;

    private HashMap<String, VehicleService> mRemoteServices = new HashMap<>();

    private HashMap<String, VehicleService> mPendingServiceUpdates = new HashMap<>();

    /**
     * The Service bundle listener interface.
     */
    public interface ServiceBundleListener
    {
        /**
         * Callback for when a local service belonging to the bundle was updated.
         *
         * @param serviceIdentifier the service identifier
         * @param parameters the parameters receieved in the update
         */
        public void onServiceUpdated(String serviceIdentifier, Object parameters);
    }

    private ServiceBundleListener mListener;

    private String validated(String identifier) {
        if (identifier == null) throw new IllegalArgumentException("Input parameter can't be null.");
        if (identifier.equals("")) throw new IllegalArgumentException("Input parameter can't be an empty string.");

        String regex = "^[a-zA-Z0-9_\\.]*$";
        boolean hasSpecialChar = !identifier.matches(regex);

        if (hasSpecialChar) throw new IllegalArgumentException("Input parameter contains a non-alphanumeric/underscore character.");

        return identifier;
    }

    /**
     * Instantiates a new Service bundle.
     *
     * @param context          the Application context. This value cannot be null.
     * @param domain           the domain portion of the RVI node's prefix (e.g., "jlr.com"). The domain must only contain
     *                         alphanumeric characters, underscores, and/or periods. No other characters or whitespace are
     *                         allowed. This value cannot be an empty string or null.
     * @param bundleIdentifier the bundle identifier (e.g., "hvac") The bundle identifier must only contain
     *                         alphanumeric characters, underscores, and/or periods. No other characters or whitespace
     *                         are allowed.  This value cannot be an empty string or null.
     * @param servicesIdentifiers a list of the identifiers for all the local services. The service identifiers must only contain
     *                            alphanumeric characters, underscores, and/or periods. No other characters or whitespace are allowed.
     *                            This value cannot be an empty string or null.
     *
     * @exception java.lang.IllegalArgumentException Throws an exception when the context is null, or if the domain, bundle identifier
     *                                               or any of the service identifiers are an empty string, contain special characters,
     *                                               or are null.
     */
    public ServiceBundle(Context context, String domain, String bundleIdentifier, ArrayList<String> servicesIdentifiers) throws IllegalArgumentException {
        if (context == null) throw new InvalidParameterException("Input parameter can't be null");

        mDomain = validated(domain);
        mBundleIdentifier = validated(bundleIdentifier);

        mLocalNodeIdentifier = RVINode.getLocalNodeIdentifier(context);

        mLocalServices = makeServices(servicesIdentifiers);
    }

    private HashMap<String, VehicleService> makeServices(ArrayList<String> serviceIdentifiers) {
        HashMap<String, VehicleService> services = new HashMap<>(serviceIdentifiers.size());
        for (String serviceIdentifier : serviceIdentifiers)
            services.put(validated(serviceIdentifier), new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, mLocalNodeIdentifier));

        return services;
    }

    /**
     * Gets the service object, given the service identifier. If one does not exist with that identifier, it is created.
     *
     * @param serviceIdentifier the service identifier
     * @return the service
     */
    VehicleService getRemoteService(String serviceIdentifier) {
        VehicleService service;
        if (null != (service = mRemoteServices.get(serviceIdentifier)))
            return service;

//        if (null != (service = mRemoteServices.get("/" + serviceIdentifier)))
//            return service;

        return new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, null);
    }

    /**
     * Add a local service to the service bundle. Adding services triggers a service-announce by the local RVI node.
     * @param serviceIdentifier the identifier of the service
     */
    public void addLocalService(String serviceIdentifier) {
        if (!mLocalServices.containsKey(serviceIdentifier))
            mLocalServices.put(serviceIdentifier, new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, mLocalNodeIdentifier));

        RVINode.announceServices();
    }

    /**
     * Add several local services to the service bundle. Adding services triggers a service-announce by the local RVI node.
     * @param serviceIdentifiers a list of service identifiers
     */
    public void addLocalServices(ArrayList<String> serviceIdentifiers) {
        for (String serviceIdentifier : serviceIdentifiers)
            mLocalServices.put(serviceIdentifier, new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, mLocalNodeIdentifier));

        RVINode.announceServices();
    }

    /**
     * Remote a local service from the service bundle. Removing services triggers a service-announce by the local RVI node.
     * @param serviceIdentifier the identifier of the service
     */
    public void removeLocalService(String serviceIdentifier) {
        mLocalServices.remove(serviceIdentifier);

        RVINode.announceServices();
    }

    /**
     * Removes all the local services from the service bundle. Removing services triggers a service-announce by the local RVI node.
     */
    public void removeAllLocalServices() {
        mLocalServices.clear();

        RVINode.announceServices();
    }

    /**
     * Add a remote service to the service bundle. If there is a pending service update with a matching service
     * identifier, this update is sent to the remote node.
     *
     * @param serviceIdentifier the identifier of the service
     */
    void addRemoteService(String serviceIdentifier, String remoteNodeIdentifier) {
        if (!mRemoteServices.containsKey(serviceIdentifier))
            mRemoteServices.put(serviceIdentifier, new VehicleService(serviceIdentifier, mDomain, mBundleIdentifier, remoteNodeIdentifier));

        VehicleService pendingServiceUpdate = mPendingServiceUpdates.get(serviceIdentifier);
        if (pendingServiceUpdate != null) {
            if (pendingServiceUpdate.getTimeout() >= System.currentTimeMillis()) {
                pendingServiceUpdate.setNodeIdentifier(remoteNodeIdentifier);
                RVINode.updateService(pendingServiceUpdate);
            }

            mPendingServiceUpdates.remove(serviceIdentifier);
        }
    }

    /**
     * Remote a remote service from the service bundle.
     * @param serviceIdentifier the identifier of the service
     */
    void removeRemoteService(String serviceIdentifier) {
        mRemoteServices.remove(serviceIdentifier);
    }

    /**
     * Remove all remote services from the service bundle.
     */
    void removeAllRemoteServices() {
        mRemoteServices.clear();
    }

    /**
     * Update a remote service on the remote RVI node
     *
     * @param serviceIdentifier the service identifier
     * @param parameters the parameters
     * @param timeout the timeout
     */
    public void updateService(String serviceIdentifier, Object parameters, Long timeout) {
        VehicleService service = getRemoteService(serviceIdentifier);

        service.setParameters(parameters);
        service.setTimeout(timeout);

        if (service.hasNodeIdentifier())
            RVINode.updateService(service);
        else
            mPendingServiceUpdates.put(serviceIdentifier, service);
    }

    /**
     * Service updated.
     *
     * @param service the service
     */
    void serviceUpdated(VehicleService service) {
        if (mListener != null) mListener.onServiceUpdated(service.getServiceIdentifier(), service.getParameters()); // TODO: This code can pass through a service that might not exist locally
    }

    /**
     * Sets the @ServiceBundleListener listener.
     *
     * @param listener the listener
     */
    public void setListener(ServiceBundleListener listener) {
        mListener = listener;
    }

    /**
     * Gets a list of fully-qualified services names of all the local services.
     *
     * @return the local services
     */
    ArrayList<String> getFullyQualifiedLocalServiceNames() {
        ArrayList<String> fullyQualifiedLocalServiceNames = new ArrayList<>(mLocalServices.size());
        for (VehicleService service : mLocalServices.values())
            if (service.getFullyQualifiedServiceName() != null)
                fullyQualifiedLocalServiceNames.add(service.getFullyQualifiedServiceName());

        return fullyQualifiedLocalServiceNames;
    }

    /**
     * Gets bundle identifier.
     *
     * @return the bundle identifier
     */
    public String getBundleIdentifier() {
        return mBundleIdentifier;
    }

}
