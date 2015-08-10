package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    VehicleService.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 5/19/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The type Vehicle service.
 */
class VehicleService
{
    private final static String TAG = "RVI:VehicleService";

    private String mServiceIdentifier;

    private String mBundleIdentifier;
    private String mDomain;

    private String mLocalPrefix;
    private String mRemotePrefix;

    private Object mParameters;

    private Long mTimeout;

    /**
     * Instantiates a new Vehicle service.
     *
     * @param serviceIdentifier the service identifier
     * @param domain the domain
     * @param bundleIdentifier the bundle identifier
     * @param remotePrefix the remote prefix
     * @param localPrefix the local prefix
     */
    VehicleService(String serviceIdentifier, String domain, String bundleIdentifier, String remotePrefix, String localPrefix) {
        mServiceIdentifier = serviceIdentifier;
        mBundleIdentifier = bundleIdentifier;
        mDomain = domain;
        mRemotePrefix = remotePrefix;
        mLocalPrefix = localPrefix;
    }

    /**
     * Instantiates a new Vehicle service.
     *
     * @param jsonString the json string
     */
    VehicleService(String jsonString) {
        Log.d(TAG, "Service data: " + jsonString);

        Gson gson = new Gson();
        HashMap jsonHash = gson.fromJson(jsonString, HashMap.class);

        String[] serviceParts = ((String) jsonHash.get("service")).split("/");

        if (serviceParts.length != 5) return;

        mDomain = serviceParts[0];
        mRemotePrefix = "/" + serviceParts[1] + "/" + serviceParts[2];
        mBundleIdentifier = "/" + serviceParts[3];
        mServiceIdentifier = "/" + serviceParts[4];

        LinkedTreeMap<Object, Object> parameters = ((ArrayList<LinkedTreeMap>) jsonHash.get("parameters")).get(0);

        // TODO: Why are parameters arrays of object, not just an object?

        mParameters = parameters.get("value"); // TODO: This concept is HVAC specific; extract to an hvac-layer class
    }

    /**
     * Gets parameters.
     *
     * @return the parameters
     */
    Object getParameters() {
        return mParameters;
    }

    /**
     * Sets parameters.
     *
     * @param parameters the parameters
     */
    void setParameters(Object parameters) {
        this.mParameters = parameters;
    }

    /**
     * Gets service identifier.
     *
     * @return the service identifier
     */
    String getServiceIdentifier() {
        return mServiceIdentifier;
    }

    /**
     * Gets fully qualified local service name.
     *
     * @return the fully qualified local service name
     */
    String getFullyQualifiedLocalServiceName() {
        return mDomain + mLocalPrefix + mBundleIdentifier + mServiceIdentifier;
    }

    /**
     * Gets fully qualified remote service name.
     *
     * @return the fully qualified remote service name
     */
    String getFullyQualifiedRemoteServiceName() {
        return mDomain + mRemotePrefix + mBundleIdentifier + mServiceIdentifier;
    }

    /**
     * Has remote prefix.
     *
     * @return the boolean
     */
    boolean hasRemotePrefix() {
        return mRemotePrefix != null;
    }

    /**
     * Generate request params.
     *
     * @return the object
     */
    Object generateRequestParams() {
        HashMap<String, Object> params = new HashMap<>(4);

        params.put("service", getFullyQualifiedRemoteServiceName());
        params.put("parameters", Arrays.asList(mParameters));
        params.put("timeout", mTimeout);
        params.put("signature", "signature");
        params.put("certificate", "certificate");

        return params;
    }

    /**
     * Json string.
     *
     * @return the string
     */
    String jsonString() {
        Gson gson = new Gson();

        Log.d(TAG, "Service data: " + gson.toJson(generateRequestParams()));

        return gson.toJson(generateRequestParams());
    }

    /**
     * Gets bundle identifier.
     *
     * @return the bundle identifier
     */
    String getBundleIdentifier() {
        return mBundleIdentifier;
    }

    /**
     * Gets remote prefix.
     *
     * @return the remote prefix
     */
    String getRemotePrefix() {
        return mRemotePrefix;
    }

    /**
     * Sets remote prefix.
     *
     * @param remotePrefix the remote prefix
     */
    void setRemotePrefix(String remotePrefix) {
        mRemotePrefix = remotePrefix;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    Long getTimeout() {
        return mTimeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    void setTimeout(Long timeout) {
        mTimeout =  System.currentTimeMillis() + timeout;
    }
}
