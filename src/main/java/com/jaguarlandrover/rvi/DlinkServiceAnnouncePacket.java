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

import android.util.Log;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

class DlinkServiceAnnouncePacket extends DlinkPacket
{
    private final static String TAG = "RVI:DlinkServi...Packet";

    @SerializedName("stat")
    private String mStatus;

    @SerializedName("svcs")
    private ArrayList<String> mServices;

    private ArrayList<String> getServiceFQNames(ArrayList<VehicleService> services) {
        ArrayList<String> newList = new ArrayList<>(services.size());
        for (VehicleService service : services)
            newList.add(service.getFullyQualifiedLocalServiceName());

        return newList;
    }

    DlinkServiceAnnouncePacket() {
    }

    /**
     * Helper method to get a service announce dlink json object
     *
     * @param services The array of services to announce
     *
     */
    DlinkServiceAnnouncePacket(ArrayList<VehicleService> services) {
        super(Command.SERVICE_ANNOUNCE);

        mStatus   = "av"; // TODO: Confirm what this is/where is comes from
        mServices = getServiceFQNames(services);
    }

    ArrayList<String> getServices() {
        return mServices;
    }

//    public DlinkServiceAnnouncePacket(HashMap jsonHash) {
//        super(Command.SERVICE_ANNOUNCE, jsonHash);
//
//        mStatus   = (String) jsonHash.get("stat");
//        mServices = (ArrayList<String>) jsonHash.get("svcs");
//    }

}
