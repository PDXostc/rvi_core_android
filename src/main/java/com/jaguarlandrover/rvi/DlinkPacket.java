package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    DlinkPacket.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 6/15/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

class DlinkPacket
{
    private final static String TAG = "RVI:DlinkPacket";

    protected enum Command
    {
        @SerializedName("au")   AUTHORIZE("au"),
        @SerializedName("sa")   SERVICE_ANNOUNCE("sa"),
        @SerializedName("rcv")  RECEIVE("rcv"),
        @SerializedName("ping") PING("ping");

        private final String mString;

        Command(String string) {
            mString = string;
        }
    }

    /**
     * The TID.
     */
    @SerializedName("tid")
    protected Integer mTid = null;

    /**
     * The cmd that was used in the request.
     */
    @SerializedName("cmd")
    protected Command mCmd = null;

    @SerializedName("sign")
    protected String mSig = null;

    private static Integer tidCounter = 0;

    /**
     * Serializes the object into json strVal
     */
    protected String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    protected DlinkPacket() {

    }

    /**
     * Base constructor of the DlinkPacket
     */
    protected DlinkPacket(Command command) {
        if (command == null) {
          throw new IllegalArgumentException("Command can't be null");
        }

        mCmd = command;

        mTid = tidCounter++;
        mSig = "";
    }

    protected DlinkPacket(Command command, HashMap jsonHash) {
        if (command == null || jsonHash == null)  {
          throw new IllegalArgumentException("Constructor arguments can't be null");
        }

        mCmd = command;

        // TODO: What other args should be required?
        if (jsonHash.containsKey("tid"))
            mTid = ((Double) jsonHash.get("tid")).intValue();

        if (jsonHash.containsKey("sign"))
            mSig = (String) jsonHash.get("sign"); // TODO: Push for sign->sig

    }
}
