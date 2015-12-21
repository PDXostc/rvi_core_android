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
        mCreds = new ArrayList<String>(Arrays.asList("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVfdGltZXN0YW1wIjoxNDUwMzg4ODc1LCJkZXZpY2VfY2VydCI6Ik1JSUI4ekNDQVZ3Q0FRRXdEUVlKS29aSWh2Y05BUUVMQlFBd1FqRUxNQWtHQTFVRUJoTUNWVk14RHpBTkJnTlZCQWdNQms5eVpXZHZiakVSTUE4R0ExVUVCd3dJVUc5eWRHeGhibVF4RHpBTkJnTlZCQW9NQmtkRlRrbFdTVEFlRncweE5URXhNamN5TXpFME5USmFGdzB4TmpFeE1qWXlNekUwTlRKYU1FSXhDekFKQmdOVkJBWVRBbFZUTVE4d0RRWURWUVFJREFaUGNtVm5iMjR4RVRBUEJnTlZCQWNNQ0ZCdmNuUnNZVzVrTVE4d0RRWURWUVFLREFaSFJVNUpWa2t3Z1o4d0RRWUpLb1pJaHZjTkFRRUJCUUFEZ1kwQU1JR0pBb0dCQUp0dmlNOEFSSXJGcXVQYzBteUI5QnVGOU1ka0EvMlNhdHFiWk1XZVRPVUpIR3JqQkRFRU1MUTd6azhBeUJtaTdScXVZWVpzNjdTeUxoeWxWR0toNnNKQWxlY3hiSFV3ajdjWlNTMWJtS01qZTZMNjFnS3d4Qm0yTklGVTFjVmwyakpsVGFVOVZZaE00eGs1N3lqMjhua054U1lXUDF2YkZYMk5EWDJpSDdiNUFnTUJBQUV3RFFZSktvWklodmNOQVFFTEJRQURnWUVBaGJxVnI5RS8wTTcyOW5jNkRJK3FncXNSU01mb3l2QTNDbW4vRUN4bDF5YkdrdXpPN3NCOGZHamdNUTl6emNiNnExdVAzd0dqUGlvcU15bWlZWWpVbUNUdnpkdlJCWis2U0Rqclpmd1V1WWV4aUtxSTlBUDZYS2FIbEFMMTQrcksrNkhONHVJa1pjSXpQd1NNSGloMWJzVFJweVk1WjNDVURjREprWXRWYllzPSIsImlkIjoiaW5zZWN1cmVfY3JlZGVudGlhbHMiLCJpc3MiOiJqbHIuY29tIiwicmlnaHRfdG9faW52b2tlIjpbImpsci5jb20vIl0sInJpZ2h0X3RvX3JlZ2lzdGVyIjpbImpsci5jb20vIl0sInZhbGlkaXR5Ijp7InN0YXJ0IjoxNDUwMzg4ODc1LCJzdG9wIjoxNDgxOTI0ODc1fX0.yGlwcumyTgUQk4kCPaI0XuBaJdHJS-vVF0Tu1CKCiCSvK43iY02XDXmsIYbErb_600vIbzQdVYoIc9lbcM99O-xk6xb1aY8hrEUwLUVORdAniI4lidP0J88atCFPIDxHZDwk0ae9YnyaR2KInq0Rt2vYRJWoeRDjUr1zh-3Wp8k"));
        //mCreds = new ArrayList<String>(Arrays.asList("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVfdGltZXN0YW1wIjoxNDM5OTI1NDE2LCJyaWdodF90b19pbnZva2UiOlsiamxyLmNvbS8iXSwicmlnaHRfdG9fcmVnaXN0ZXIiOlsiamxyLmNvbS8iXSwiaWQiOiJpbnNlY3VyZV9jZXJ0IiwiaXNzIjoiamFndWFybGFuZHJvdmVyLmNvbSIsIng1MDlfY2VydCI6Ik1JSURORENDQWh3Q0NRQ0N1UkZyZnhrM3ZqQU5CZ2txaGtpRzl3MEJBUXNGQURCY01Rc3dDUVlEVlFRR0V3SlZVekVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUkV3RHdZRFZRUUhEQWhRYjNKMGJHRnVaREVhTUJnR0ExVUVDZ3dSU21GbmRXRnlJRXhoYm1RZ1VtOTJaWEl4RFRBTEJnTlZCQXNNQkU5VFZFTXdIaGNOTVRVeE1URXlNakkwT1RFNFdoY05NVFl4TVRFeE1qSTBPVEU0V2pCY01Rc3dDUVlEVlFRR0V3SlZVekVQTUEwR0ExVUVDQXdHVDNKbFoyOXVNUkV3RHdZRFZRUUhEQWhRYjNKMGJHRnVaREVhTUJnR0ExVUVDZ3dSU21GbmRXRnlJRXhoYm1RZ1VtOTJaWEl4RFRBTEJnTlZCQXNNQkU5VFZFTXdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElCRHdBd2dnRUtBb0lCQVFET1hoNHE3Zi9rd1VmcHhzL3ZUMVUwZVNNRXVKdkR0dFFoSWhMWStVNEVFZmF5SWxCcTRQaEZsenQ0QUlIaThXU0FqdUsybUNwYnpFYmtTcFpiYU1nS2t0YUxPTlNGeGRJTnJnWE1WdGgvSjVLVkNpYnQ4b2RlODZ0ZFRJZ0RNQkZUTnZpRzdTeVFZYkM3RHhwbS9OMXI5TDdqM0V5OUZldGhJcUJmUk9tV0Z6VE84U0dhNnN1VXRTb3JQNlhGeFZnZzRGMGEvMUQ2M2Q0aWgyeDBxT01uYldicDFjVEk1SFFNSVhJc1VSY2F0SndPTlpsSENWTitabmRHN0t6WkhzS3lDQmZXbHVYQ1ZIdHVDWVBSRVBFQlp2Y0VvdFpUNlMxRjVGMXk5d0ZHWHpoR2x4Q212Zzh1OFZ6N1pzMk14dWc2SFU3cXgwL3FEUk40cjlYMVlYOGxBZ01CQUFFd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFEU2Vya3c2a2hlSm52ZWNyQU1DSVBxMGxqQ1ZndW1nMUJoKzVpUU5jNkJhVFpvUmdGTWJ0b3hJWG1XNWlQT2poSWQrdm53NFB2OEFTaG42WWVyNUswQUZ4a0NWV3pZU3plR1ViQ29ISnBoY1ovWTMrOUJDbDZvNzVGemowSy9sTWZ3UVkrRXFhbDUzdEN1SEdIOHFqNEMybWthczhTR1Q3TkIwbng0aitNUmdxNllZcHlsZkM2anZYdHRiVWtodStPZmV6ZlUxZ3hoaVdpM1ZiT1E1bWx4ZHQxbk94QWt2V0ZRNFhGODlaOE4vK205WW91TnNXaXUxTmVub0FKWFJXQUJTcWUwUWQycHpvd0RmOEdRR1pFZVB3NUVndnkrMHREMHFpMGFmNytOWWZ0eDFwbm80WXNVaWx1aVZFbWJDaytHNkM5TUxwUWVSWW53RFd2OTd5dWs9IiwidmFsaWRpdHkiOnsic3RhcnQiOjE0MjAwOTkyMDAsInN0b3AiOjE5MjUwMjA3OTl9fQ.m7CctCmaskjsYmSWJPuOErwZ3bQoppj2h9AERH0OZjg"));
    }
}
