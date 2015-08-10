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

/**
 * The type Dlink packet.
 */
class DlinkPacket
{
    private final static String TAG = "RVI:DlinkPacket";

    /**
     * The enum Command.
     */
    protected enum Command
    {
        /**
         * The AUTHORIZE.
         */
        @SerializedName("au")AUTHORIZE("au"),
        /**
         * The SERVICE_ANNOUNCE.
         */
        @SerializedName("sa")SERVICE_ANNOUNCE("sa"),
        /**
         * The RECEIVE.
         */
        @SerializedName("rcv")RECEIVE("rcv"),
        /**
         * The PING.
         */
        @SerializedName("ping")PING("ping");

        private final String mString;

        /**
         * Instantiates a new Command.
         *
         * @param string the string
         */
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

    /**
     * The M sig.
     */
    @SerializedName("sign")
    protected String mSig = null;

    private static Integer tidCounter = 0;

    /**
     * Serializes the object into json strVal
     * @return the string
     */
    protected String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Instantiates a new Dlink packet.
     */
    protected DlinkPacket() {

    }

    /**
     * Base constructor of the DlinkPacket
     * @param command the command
     */
    protected DlinkPacket(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command can't be null");
        }

        mCmd = command;

        mTid = tidCounter++;
        mSig = "";
    }

    /**
     * Instantiates a new Dlink packet.
     *
     * @param command the command
     * @param jsonHash the json hash
     */
    protected DlinkPacket(Command command, HashMap jsonHash) {
        if (command == null || jsonHash == null) {
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
