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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Dlink "authorization" packet. This request is used to authorize an RVI node.
 */
class DlinkAuthPacket extends DlinkPacket
{
    private final static String TAG = "RVI:DlinkAuthPacket";

    @SerializedName("addr")
    private String mAddr;

    @SerializedName("port")
    private Integer mPort;

    @SerializedName("ver")
    private String mVer;

    @SerializedName("creds")
    private ArrayList<String> mCreds;

    /**
     * Default constructor
     */
    DlinkAuthPacket() {
        super(Command.AUTHORIZE);

        mAddr = "0.0.0.0";
        mPort = 0;
        mVer = "1.0";
        mCreds = new ArrayList<String>(Arrays.asList("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyaWdodF90b19pbnZva2UiOlsiZ2VuaXZpLm9yZy8iXSwiaXNzIjoiZ2VuaXZpLm9yZyIsImRldmljZV9jZXJ0IjoiTUlJQjh6Q0NBVndDQVFFd0RRWUpLb1pJaHZjTkFRRUxCUUF3UWpFTE1Ba0dBMVVFQmhNQ1ZWTXhEekFOQmdOVkJBZ01Cazl5WldkdmJqRVJNQThHQTFVRUJ3d0lVRzl5ZEd4aGJtUXhEekFOQmdOVkJBb01Ca2RGVGtsV1NUQWVGdzB4TlRFeE1qY3lNekUwTlRKYUZ3MHhOakV4TWpZeU16RTBOVEphTUVJeEN6QUpCZ05WQkFZVEFsVlRNUTh3RFFZRFZRUUlEQVpQY21WbmIyNHhFVEFQQmdOVkJBY01DRkJ2Y25Sc1lXNWtNUTh3RFFZRFZRUUtEQVpIUlU1SlZra3dnWjh3RFFZSktvWklodmNOQVFFQkJRQURnWTBBTUlHSkFvR0JBSnR2aU04QVJJckZxdVBjMG15QjlCdUY5TWRrQS8yU2F0cWJaTVdlVE9VSkhHcmpCREVFTUxRN3prOEF5Qm1pN1JxdVlZWnM2N1N5TGh5bFZHS2g2c0pBbGVjeGJIVXdqN2NaU1MxYm1LTWplNkw2MWdLd3hCbTJOSUZVMWNWbDJqSmxUYVU5VlloTTR4azU3eWoyOG5rTnhTWVdQMXZiRlgyTkRYMmlIN2I1QWdNQkFBRXdEUVlKS29aSWh2Y05BUUVMQlFBRGdZRUFoYnFWcjlFLzBNNzI5bmM2REkrcWdxc1JTTWZveXZBM0Ntbi9FQ3hsMXliR2t1ek83c0I4ZkdqZ01ROXp6Y2I2cTF1UDN3R2pQaW9xTXltaVlZalVtQ1R2emR2UkJaKzZTRGpyWmZ3VXVZZXhpS3FJOUFQNlhLYUhsQUwxNCtySys2SE40dUlrWmNJelB3U01IaWgxYnNUUnB5WTVaM0NVRGNESmtZdFZiWXM9IiwidmFsaWRpdHkiOnsic3RhcnQiOjE0NDg2ODM3NDIsInN0b3AiOjE0ODAyMTk3NDJ9LCJyaWdodF90b19yZWdpc3RlciI6WyJnZW5pdmkub3JnLyJdLCJjcmVhdGVfdGltZXN0YW1wIjoxNDQ4NjgzNzQyLCJpZCI6Inh4eCJ9.OPRklok0vZDNMHwwpOVx7lq8lDU0ukXFOAZsYBqUbD6ydy4yq-EZoFl9unTm4yQzZ9z-s31sCZyC5-qnQgpZl85oloqJA4gD0E1c4JDMRf0-arRUlCsMW74SWMRj3zTDTItc2D-R4Nhk-D_f1ZqkadhYiYFyKRcw_vhJ03OZowQ"));
        //mCreds = new ArrayList<String>(Arrays.asList("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVfdGltZXN0YW1wIjoxNDM5OTI1NDE2LCJyaWdodF90b19pbnZva2UiOlsiamxyLmNvbS8iXSwicmlnaHRfdG9fcmVnaXN0ZXIiOlsiamxyLmNvbS8iXSwiaWQiOiJpbnNlY3VyZV9jZXJ0IiwiaXNzIjoiamFndWFybGFuZHJvdmVyLmNvbSIsIng1MDlfY2VydCI6Ik1JSURORENDQWh3Q0NRQ0N1UkZyZnhrM3ZqQU5CZ2txaGtpRzl3MEJBUXNGQURCY01Rc3dDUVlEVlFRR0V3SlZVekVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUkV3RHdZRFZRUUhEQWhRYjNKMGJHRnVaREVhTUJnR0ExVUVDZ3dSU21GbmRXRnlJRXhoYm1RZ1VtOTJaWEl4RFRBTEJnTlZCQXNNQkU5VFZFTXdIaGNOTVRVeE1URXlNakkwT1RFNFdoY05NVFl4TVRFeE1qSTBPVEU0V2pCY01Rc3dDUVlEVlFRR0V3SlZVekVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUkV3RHdZRFZRUUhEQWhRYjNKMGJHRnVaREVhTUJnR0ExVUVDZ3dSU21GbmRXRnlJRXhoYm1RZ1VtOTJaWEl4RFRBTEJnTlZCQXNNQkU5VFZFTXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFET1hoNHE3Zi9rd1VmcHhzL3ZUMVUwZVNNRXVKdkR0dFFoSWhMWStVNEVFZmF5SWxCcTRQaEZsenQ0QUlIaThXU0FqdUsybUNwYnpFYmtTcFpiYU1nS2t0YUxPTlNGeGRJTnJnWE1WdGgvSjVLVkNpYnQ4b2RlODZ0ZFRJZ0RNQkZUTnZpRzdTeVFZYkM3RHhwbS9OMXI5TDdqM0V5OUZldGhJcUJmUk9tV0Z6VE84U0dhNnN1VXRTb3JQNlhGeFZnZzRGMGEvMUQ2M2Q0aWgyeDBxT01uYldicDFjVEk1SFFNSVhJc1VSY2F0SndPTlpsSENWTitabmRHN0t6WkhzS3lDQmZXbHVYQ1ZIdHVDWVBSRVBFQlp2Y0VvdFpUNlMxRjVGMXk5d0ZHWHpoR2x4Q212Zzh1OFZ6N1pzMk14dWc2SFU3cXgwL3FEUk40cjlYMVlYOGxBZ01CQUFFd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFEU2Vya3c2a2hlSm52ZWNyQU1DSVBxMGxqQ1ZndW1nMUJoKzVpUU5jNkJhVFpvUmdGTWJ0b3hJWG1XNWlQT2poSWQrdm53NFB2OEFTaG42WWVyNUswQUZ4a0NWV3pZU3plR1ViQ29ISnBoY1ovWTMrOUJDbDZvNzVGemowSy9sTWZ3UVkrRXFhbDUzdEN1SEdIOHFqNEMybWthczhTR1Q3TkIwbng0aitNUmdxNllZcHlsZkM2anZYdHRiVWtodStPZmV6ZlUxZ3hoaVdpM1ZiT1E1bWx4ZHQxbk94QWt2V0ZRNFhGODlaOE4vK205WW91TnNXaXUxTmVub0FKWFJXQUJTcWUwUWQycHpvd0RmOEdRR1pFZVB3NUVndnkrMHREMHFpMGFmNytOWWZ0eDFwbm80WXNVaWx1aVZFbWJDaytHNkM5TUxwUWVSWW53RFd2OTd5dWs9IiwidmFsaWRpdHkiOnsic3RhcnQiOjE0MjAwOTkyMDAsInN0b3AiOjE5MjUwMjA3OTl9fQ.m7CctCmaskjsYmSWJPuOErwZ3bQoppj2h9AERH0OZjg"));
    }
}
