package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    DlinkAuthPacket.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 6/15/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

class DlinkAuthPacket extends DlinkPacket
{
    private final static String TAG = "RVI:DlinkAuthPacket";

    @SerializedName("addr")
    private String mAddr;

    @SerializedName("port")
    private Integer mPort;

    @SerializedName("ver")
    private String mVer;

    @SerializedName("cert")
    private String mCert;

    /**
     * Helper method to get an authorization json object
     */
    DlinkAuthPacket() {
        super(Command.AUTHORIZE);

        mAddr = "0.0.0.0";
        mPort = 0;
        mVer = "1.0";
        mCert = "";
    }

//    public DlinkAuthPacket(HashMap jsonHash) {
//        super(Command.AUTHORIZE, jsonHash);
//
//        mAddr = (String)  jsonHash.get("addr");
//        mPort = ((Double) jsonHash.get("port")).intValue();
//        mVer  = (String)  jsonHash.get("ver");
//        mCert = (String)  jsonHash.get("cert");
//    }

}
