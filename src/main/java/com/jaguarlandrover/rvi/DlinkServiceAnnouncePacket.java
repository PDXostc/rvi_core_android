package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    DlinkServiceAnnouncePacket.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 7/1/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.internal.LinkedTreeMap;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64UrlCodec;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

/**
 * The type Dlink "service announce" request packet. This request is used to announce RVI node services.
 */
class DlinkServiceAnnouncePacket extends DlinkPacket
{
    private final static String TAG = "RVI:DlinkServi...Packet";

    /**
     * The status.
     */
    @SerializedName("stat")
    private String mStatus;

    /**
     * The list of fully-qualified service names that are being announced.
     */
    @SerializedName("svcs")
    private ArrayList<String> mServices;

//    /**
//     * Helper method that takes a list of @VehicleServices, and returns a list of fully-qualified local service names
//     * @param services a list of @VehicleServices
//     * @return a list of fully-qualified local service names
//     */
//    private ArrayList<String> getServiceFQNames(ArrayList<Service> services) {
//        ArrayList<String> newList = new ArrayList<>(services.size());
//        for (Service service : services)
//            newList.add(service.getFullyQualifiedLocalServiceName());
//
//        return newList;
//    }

    /**
     * Instantiates a new Dlink service announce packet.
     */
    DlinkServiceAnnouncePacket() {
    }

    /**
     * Helper method to get a service announce dlink json object
     *
     * @param services The array of services to announce
     */
    DlinkServiceAnnouncePacket(ArrayList<String> services) {
        super(Command.SERVICE_ANNOUNCE);

        if (services == null) throw new InvalidParameterException("Service Announce Packet cannot be initialized with null services.");

        mStatus = "av"; // TODO: Confirm what this is/where is comes from
        mServices = services;//getServiceFQNames(services);
    }

    /**
     * Gets list of fully-qualified local service names.
     *
     * @return the list of fully-qualified local service names
     */
    ArrayList<String> getServices() {

        if (mServices == null) {
            String sigSplit[] = mSig.split("\\.");
            String jsonString = sigSplit[1];

            jsonString = new String(Base64.decode(jsonString, Base64.DEFAULT));

            Gson gson = new Gson();
            HashMap jsonHash = gson.fromJson(jsonString, HashMap.class);

            if  (jsonHash.get("svcs").getClass().equals(ArrayList.class))
                mServices = ((ArrayList<String>) jsonHash.get("svcs"));

            //String servicesString = Jwts.parser().setSigningKey(key).parseClaimsJws(mSig).getBody().getSubject();

            Log.d(TAG, "Decoded sig subject: " + jsonString);
        }

        return mServices;
    }

//    public DlinkServiceAnnouncePacket(HashMap jsonHash) {
//        super(Command.SERVICE_ANNOUNCE, jsonHash);
//
//        mStatus   = (String) jsonHash.get("stat");
//        mServices = (ArrayList<String>) jsonHash.get("svcs");
//    }

}
