package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    DlinkPacketParser.java
 * Project: RVI SDK
 *
 * Created by Lilli Szafranski on 7/2/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Log;
import com.google.gson.Gson;

/**
 * The type Dlink packet parser.
 */
class DlinkPacketParser
{
    private final static String TAG = "RVI:DlinkPacketParser";

    private String                    mBuffer;
    private DlinkPacketParserListener mDataParserListener;

    /**
     * The interface Dlink packet parser listener.
     */
    interface DlinkPacketParserListener
    {
        /**
         * On packet parsed.
         *
         * @param packet the packet
         */
        void onPacketParsed(DlinkPacket packet);
    }

    /**
     * The interface Dlink packet parser test case listener.
     */
    interface DlinkPacketParserTestCaseListener
    {
        /**
         * On json string parsed.
         *
         * @param jsonString the json string
         */
        void onJsonStringParsed(String jsonString);

        /**
         * On json object parsed.
         *
         * @param jsonObject the json object
         */
        void onJsonObjectParsed(Object jsonObject);
    }

    /**
     * Instantiates a new Dlink packet parser.
     *
     * @param listener the listener
     */
    DlinkPacketParser(DlinkPacketParserListener listener) {
        mDataParserListener = listener;
    }

    /**
     *
     * @param  buffer String to parse out JSON objects from
     * @return The length of the first JSON object found, 0 if it is an incomplete object,
     *                -1 if the string does not start with a '{' or an '['
     */
    private int getLengthOfJsonObject(String buffer) {
        if (buffer.charAt(0) != '{' && buffer.charAt(0) != '[') return -1;

        int numberOfOpens  = 0;
        int numberOfCloses = 0;

        char open  = buffer.charAt(0) == '{' ? '{' : '[';
        char close = buffer.charAt(0) == '{' ? '}' : ']';

        for (int i = 0; i < buffer.length(); i++) {
            if (buffer.charAt(i) == open) numberOfOpens++;
            else if (buffer.charAt(i) == close) numberOfCloses++;

            if (numberOfOpens == numberOfCloses) return i + 1;
        }

        return 0;
    }

    private DlinkPacket stringToPacket(String string) {
        Log.d(TAG, "Received packet: " + string);

        if (mDataParserListener instanceof DlinkPacketParserTestCaseListener)
            ((DlinkPacketParserTestCaseListener) mDataParserListener).onJsonStringParsed(string);

        Gson gson = new Gson();
        DlinkPacket packet;

        try {
            packet = gson.fromJson(string, DlinkPacket.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (mDataParserListener instanceof DlinkPacketParserTestCaseListener)
            ((DlinkPacketParserTestCaseListener) mDataParserListener).onJsonObjectParsed(packet);

        DlinkPacket.Command command = packet.mCmd;

        if (command == null)
            return null;

        if (command == DlinkPacket.Command.AUTHORIZE) {
            return gson.fromJson(string, DlinkAuthPacket.class);
        } else if (command == DlinkPacket.Command.SERVICE_ANNOUNCE) {
            return gson.fromJson(string, DlinkServiceAnnouncePacket.class);
        } else if (command == DlinkPacket.Command.RECEIVE) {
            return gson.fromJson(string, DlinkReceivePacket.class);
        } else {
            return null;
        }
    }

    private String recurse(String buffer) {
        int lengthOfString     = buffer.length();
        int lengthOfJsonObject = getLengthOfJsonObject(buffer);

        DlinkPacket packet;

        if (lengthOfJsonObject == lengthOfString) { /* Current data is 1 json object */
            if ((packet = stringToPacket(buffer)) != null)
                mDataParserListener.onPacketParsed(packet);

            return "";

        } else if (lengthOfJsonObject < lengthOfString && lengthOfJsonObject > 0) { /* Current data is more than 1 json object */
            if ((packet = stringToPacket(buffer.substring(0, lengthOfJsonObject))) != null)
                mDataParserListener.onPacketParsed(packet);

            return recurse(buffer.substring(lengthOfJsonObject));

        } else if (lengthOfJsonObject == 0) { /* Current data is less than 1 json object */
            return buffer;

        } else { /* There was an error */
            return null;

        }
    }

    /**
     * Parse data.
     *
     * @param data the data
     */
    void parseData(String data) {
        if (mBuffer == null) mBuffer = "";

        mBuffer = recurse(mBuffer + data);
    }

    /**
     * Clear void.
     */
    void clear() {
        mBuffer = null;
    }

    @Override
    public String toString() {
        return mBuffer == null ? "" : mBuffer;
    }
}
