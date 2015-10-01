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
 * The Dlink request packet base class
 */
class DlinkPacket
{
    private final static String TAG = "RVI:DlinkPacket";

    /**
     * The Command enumeration, used to enumerate the different commands, or dlink packet types
     */
    protected enum Command
    {
        /**
         * The AUTHORIZE dlink packet type ("cmd":"au").
         */
        @SerializedName("au")AUTHORIZE("au"),
        /**
         * The SERVICE_ANNOUNCE dlink packet type ("cmd":"sa").
         */
        @SerializedName("sa")SERVICE_ANNOUNCE("sa"),
        /**
         * The RECEIVE dlink packet type ("cmd":"rcv").
         */
        @SerializedName("rcv")RECEIVE("rcv"),
        /**
         * The PING dlink packet type ("cmd":"ping").
         */
        @SerializedName("ping")PING("ping");

        private final String mString;

        /**
         * Instantiates a new Command enumerated string.
         *
         * @param string the string expected in the dlink request packet ("au", "sa", "rcv", or "ping")
         */
        Command(String string) {
            mString = string;
        }
    }

    private static Integer tidCounter = 0;

    /**
     * The transaction id.
     */
    @SerializedName("tid")
    protected Integer mTid = null;

    /**
     * The cmd that was used in the request ("au", "sa", "rcv", or "ping").
     */
    @SerializedName("cmd")
    protected Command mCmd = null;

    /**
     * The signature.
     */
    @SerializedName("sign")
    protected String mSig = null;

    /**
     * Serializes the object into json strVal
     * @return the serialized json string
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
     * @param command the command ("au", "sa", "rcv", or "ping")
     */
    protected DlinkPacket(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command can't be null");
        }

        mCmd = command;

        mTid = tidCounter++;
        mSig = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXlzIjpbeyJhbGciOiJSUzI1NiIsInVzZSI6InNpZyIsImUiOiJBUUFCIiwia3R5IjoiUlNBIiwibiI6InBrUkZUNzVPaE52TEdaaXhrc2xXazZiVlhXd01SWHg1cmo0NkhxT09wOUFXM3JHdEVxd2JYSEdQRE9OWTl1cjdSQklSSGk5bEZUakdfVjRZY3ZkdXp3dDNjcjdZcWdyR1c3YklZaXRnWFlydjh5bXZlWDBaVmVCUnd2MklqOTZZYmgxUDdCUmVxMG9pQUpOWXNYcEx3clBYMjRCeHoyN0kyb0w2WldMdTc5RXROWG5HZGtEaVdiV1hkand4TjVBOE1nQmtuLXF6QkR2RU1wdkNsTlY1czNkT3RCckZVaUZIWTk5and6bkNOOHRwdU1SZUFTSGpjTTQ2bG1GSERFVXlVbWlCYnk0cFNHcFFWVGdfUWhPMDhSclROREh5dHZIX3hNZHJEOTlJNEhyQkhnTTZlR0pZU3pmdW95STNseUtabGtPeEZsWXQ4OHpuQ1lWejJ1bHV1cmp3RHFFR3p1cVozVEMtV2JCbm1kUXJZdXZnaDB4eUZYWmEzREhTOGRDb3JVTXQwVzl2V3NCN21hQy1LWmdCNDBQX0lfanNGQzFEbFlOSXpSWUozVWE0bm5qOEliYVh2YnlNZERvUTV0UVBtYm5vS3hvNlpJTTNodi1LMTk2T0czaVA4YzFUZHpkQW1SaGpGRW9YTFNocDFZM0VrNU9faWZQYzZuWS1JRGdieU9GQ28wTXBGV0NqUk9ES2doNTFhWTFuZkowMHNGYzg2SUFtdGdRWXlqZEZVYkJ4X1RwZGh6Mmg3RTdzOXdtNDhJMldROEtsdjFLU0VXX251SWRySC1ubWFiMFY1Q1NZRnNVczBaekkzaVRSYm9kVDUzdnlKLVNiQjR4SzN2aFh4cDdtSmFCbTR4SGpjMGNueXBVYUFVTVhUaTBjZHVKNlp0NjhpZVZSYzNNIn1dfQ.Fvs55POPsNMayGQFZmXz6rKiBM8GIuXG5mqNkvc90Ucw_LxAzOxnnmacQsuFcOvJpjwxAOjQZbm8dHoIQLkYvgDMSDu1mOGiiuyamlR_opLFH9igsKo9u3LNSzzIoVxODYb07lig6nNh7CHikzIobzdIjpK4vwTQ_53PqKr5gS3HXuh60mRtxcoezuyBqn_zv9qRP-E134sYYbAMnPUeGaELm3EleSbrUNCHUEBGVZX1GX04V6aTqpzZU-qru2O3amWBbC8DvyhoHOsE1K_B2xMu7wrQBuKDwlP9VqQJYLcsU-sqw8KZYGpdb0t4-qQtnklgVSr0uHRWQUjjLATSMSCky9z6P_97vP_r6T44nEGxSSPhE8spkzA24ErZMyUUMrhVtW8s2XEUz5ySK_8Nj_cSLwsnNrX3va3yulcq8X3ocLbis7s87zBaGmU9BYU660fA4yvHeAOxcPc-YYPrkCTlpJoecfOGQ8mmtvdRz0wgiUaw78UpVndPEZStEae4g91QZRNDpBms5_G9Xj4mnMkObMFMGLlt-CzBVc9QPpvysPI-GGnzbi7ep6enGRlLyRFXEmtnPHwI2mk1HwA9yVaCLQdIXWtfs7tn0xywoOs73fVt9Gtz7NLuVb3SYNWhWzjmvsm-qyo8qanlAU849ZMg9aGz0VjhGIJzCbuF4J8";
    }

    // TODO: 47765, probably need to remove this constructor and update tests
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
