package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    DlinkReceivePacket.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 6/15/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Base64;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * The type Dlink receive packet.
 */
class DlinkReceivePacket extends DlinkPacket
{
    private final static String TAG = "RVI:DlinkReceivePacket";

    /**
     * The mod parameter.
     * This client is only using 'proto_json_rpc' at the moment.
     */
    @SerializedName("mod")
    private String mMod;

    /**
     * The VehicleService used to create the request params
     */
    private transient VehicleService mService;

    @SerializedName("data")
    private String mData;

    /**
     * Instantiates a new Dlink receive packet.
     */
    DlinkReceivePacket() {
    }

    /**
     * Helper method to get a receive dlink json object
     *
     * @param service The service that is getting invoked
     */
    DlinkReceivePacket(VehicleService service) {
        super(Command.RECEIVE);

        mMod = "proto_json_rpc";
        mService = service; // TODO: With this paradigm, if one of the parameters of mService changes, mData string will still be the same.
        mData = Base64.encodeToString(mService.jsonString().getBytes(), Base64.DEFAULT);
    }

//    public DlinkReceivePacket(HashMap jsonHash) {
//        super(Command.RECEIVE, jsonHash);
//
//        mMod = (String) jsonHash.get("mod");
//
//        mService = new VehicleService(new String(Base64.decode((String)jsonHash.get("data"), Base64.DEFAULT)));
//    }

    /**
     * Gets service.
     *
     * @return the service
     */
    VehicleService getService() {
        if (mService == null && mData != null)
            mService = new VehicleService(new String(Base64.decode(mData, Base64.DEFAULT)));

        return mService;
    }

    /**
     * Sets service.
     *
     * @param service the service
     */
    void setService(VehicleService service) {
        mService = service;
    }
}
