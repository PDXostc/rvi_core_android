package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    Service.java
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
class Service
{
    private final static String TAG = "RVI:Service";

    private String mServiceIdentifier;

    private String mBundleIdentifier;

    private String mDomain;

    private String mNodeIdentifier;

    private Object mParameters;

    private Long mTimeout;

    /**
     * Instantiates a new Vehicle service.
     *
     * @param serviceIdentifier the service identifier
     * @param domain the domain
     * @param bundleIdentifier the bundle identifier
     * @param prefix the service's prefix
     */
    Service(String serviceIdentifier, String domain, String bundleIdentifier, String prefix) {
        mServiceIdentifier = serviceIdentifier;
        mBundleIdentifier = bundleIdentifier;
        mDomain = domain;
        mNodeIdentifier = prefix;
    }

    /**
     * Instantiates a new Vehicle service.
     *
     * @param jsonString the json string
     */
    Service(String jsonString) {
        Log.d(TAG, "Service data: " + jsonString);

        Gson gson = new Gson();
        HashMap jsonHash = gson.fromJson(jsonString, HashMap.class);

        String[] serviceParts = ((String) jsonHash.get("service")).split("/");

        if (serviceParts.length != 5) return;

        mDomain = serviceParts[0];
        mNodeIdentifier = serviceParts[1] + "/" + serviceParts[2];
        mBundleIdentifier = serviceParts[3];
        mServiceIdentifier = serviceParts[4];

        // TODO: Why are parameters arrays of object, not just an object? This should probably get fixed everywhere.
        if  (jsonHash.get("parameters").getClass().equals(ArrayList.class))
            mParameters = ((ArrayList<LinkedTreeMap>) jsonHash.get("parameters")).get(0);
        else
            mParameters = jsonHash.get("parameters");
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
     * Gets fully qualified service name.
     *
     * @return the fully qualified service name
     */
    String getFullyQualifiedServiceName() {
        return mDomain + "/" + mNodeIdentifier + "/" + mBundleIdentifier + "/" + mServiceIdentifier;
    }

    /**
     * Has the node identifier portion of the fully-qualified service name. This happens if the remote node is
     * connected and has announced this service.
     *
     * @return the boolean
     */
    boolean hasNodeIdentifier() {
        return mNodeIdentifier != null;
    }

    /**
     * Generate request params.
     *
     * @return the object
     */
    Object generateRequestParams() {
        HashMap<String, Object> params = new HashMap<>(4);

        params.put("service", getFullyQualifiedServiceName());
        params.put("parameters", mParameters);//Arrays.asList(mParameters));
        params.put("timeout", mTimeout);
        params.put("signature", "signature");
        params.put("certificate", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXlzIjpbeyJhbGciOiJSUzI1NiIsInVzZSI6InNpZyIsImUiOiJBUUFCIiwia3R5IjoiUlNBIiwibiI6InBrUkZUNzVPaE52TEdaaXhrc2xXazZiVlhXd01SWHg1cmo0NkhxT09wOUFXM3JHdEVxd2JYSEdQRE9OWTl1cjdSQklSSGk5bEZUakdfVjRZY3ZkdXp3dDNjcjdZcWdyR1c3YklZaXRnWFlydjh5bXZlWDBaVmVCUnd2MklqOTZZYmgxUDdCUmVxMG9pQUpOWXNYcEx3clBYMjRCeHoyN0kyb0w2WldMdTc5RXROWG5HZGtEaVdiV1hkand4TjVBOE1nQmtuLXF6QkR2RU1wdkNsTlY1czNkT3RCckZVaUZIWTk5and6bkNOOHRwdU1SZUFTSGpjTTQ2bG1GSERFVXlVbWlCYnk0cFNHcFFWVGdfUWhPMDhSclROREh5dHZIX3hNZHJEOTlJNEhyQkhnTTZlR0pZU3pmdW95STNseUtabGtPeEZsWXQ4OHpuQ1lWejJ1bHV1cmp3RHFFR3p1cVozVEMtV2JCbm1kUXJZdXZnaDB4eUZYWmEzREhTOGRDb3JVTXQwVzl2V3NCN21hQy1LWmdCNDBQX0lfanNGQzFEbFlOSXpSWUozVWE0bm5qOEliYVh2YnlNZERvUTV0UVBtYm5vS3hvNlpJTTNodi1LMTk2T0czaVA4YzFUZHpkQW1SaGpGRW9YTFNocDFZM0VrNU9faWZQYzZuWS1JRGdieU9GQ28wTXBGV0NqUk9ES2doNTFhWTFuZkowMHNGYzg2SUFtdGdRWXlqZEZVYkJ4X1RwZGh6Mmg3RTdzOXdtNDhJMldROEtsdjFLU0VXX251SWRySC1ubWFiMFY1Q1NZRnNVczBaekkzaVRSYm9kVDUzdnlKLVNiQjR4SzN2aFh4cDdtSmFCbTR4SGpjMGNueXBVYUFVTVhUaTBjZHVKNlp0NjhpZVZSYzNNIn1dLCJpc3MiOiJqYWd1YXJsYW5kcm92ZXIuY29tIiwidmFsaWRpdHkiOnsic3RhcnQiOjE0MjAwOTkyMDAsInN0b3AiOjE5MjUwMjA3OTl9LCJzb3VyY2VzIjpbImpsci5jb20iXSwiY3JlYXRlX3RpbWVzdGFtcCI6MTQzOTkyNTQxNiwiaWQiOiJpbnNlY3VyZV9jZXJ0IiwiZGVzdGluYXRpb25zIjpbImpsci5jb20iXX0.Nb2ez5oX0AgZCRzTPuzxHVOGtcN4JVVsmfS8nuhjMOM7I22EVueCzIjImqsfk4hA2Yi7pSQrhaM97UodIQ9uxTSGg8J8RLZGulsT3xxLCxRn95FrJGbAvWdH5fhVWJZl4gCxnvYsGQrNDOB5lzXy4bYpS0sBO6EfRN1trQe7bojbBf0yL-kRjbO9Fmj7OFvCUr47KkZCZehkpt4Jtl6WGxmUg7_SM8h4HUGVvzO9iDKUFPpgUmt4kqfa7wZUvPYQzT4KsePxiXfmehQ6Axx9LjE7nZ-BhWrKctiU2LbtymsL2svPuaWblW7z39O3vNW4OQEOzXmcvXXMIXLxSA77aZOqffYBgV51ybpsZt8HJySF5nUO_JSMJEwdMOUuNfiBZZpUs44IXnQ26DKzIGHSJAzsEdBdux_QXgCMFr-bmdI8usMojRPAZZKpVO8WFGdie8yn8GfRTPp2VfxpXDCDeqs2R33WWAaFgsyvpWPt0mn6nKgUGJ-GnBHtycTZoMs5ZcsG07Z-f_HpwaAW056gqkni2jm2WyA3QtkcVfMDJOhwenwQA5LPb5Otji_qeuxuTyMGSY80-9G9LNJR0Ut-bAXkBXUbbMfrOAEi4Z-JIUOPJH7O6ndx-t30FcGT0RCqdqe_8vr_B_H5xAcD6xDo0jRIunemXVt4vrX4ASoLTik");

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
     * Gets the domain.
     *
     * @return the domain
     */
    String getDomain() {
        return mDomain;
    }

    /**
     * Sets the node identifier portion of the fully-qualified service name
     *
     * @param nodeIdentifier the local or remote RVI node's identifier
     */
    void setNodeIdentifier(String nodeIdentifier) {
        mNodeIdentifier = nodeIdentifier;
    }

    /**
     * Gets the timeout. This value is the timeout, in milliseconds, from the epoch.
     *
     * @return the timeout
     */
    Long getTimeout() {
        return mTimeout;
    }

    /**
     * Sets the timeout.
     *
     * @param timeout the timeout in milliseconds from the epoch.
     */
    void setTimeout(Long timeout) {
        mTimeout = timeout;
    }
}
