package org.genivi.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2016 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    Privilege.java
 * Project: RVI
 *
 * Created by Lilli Szafranski on 10/4/16.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;

class Privilege {
    private final static String TAG = "RVI/Privilege__________";

    @SerializedName("right_to_invoke")
    private ArrayList<String> mRightToInvoke = null;

    @SerializedName("right_to_receive")
    private ArrayList<String> mRightToReceive = null;

    @SerializedName("iss")
    private String mIssuer = null;

    @SerializedName("device_cert")
    private String mEncodedDeviceCertificate = null;

    @SerializedName("validity")
    private Validity mValidity = null;

    @SerializedName("id")
    private String mId = null;

    private Certificate mCertificate = null;

    private String mJwt = null;

    public Privilege() {
    }

    Privilege(String jwt) {
        this.mJwt = jwt;
    }

    Boolean parse(Key key) {
        try {
            this.mRightToInvoke            = null;
            this.mRightToReceive           = null;
            this.mIssuer                   = null;
            this.mEncodedDeviceCertificate = null;
            this.mValidity                 = null;
            this.mIssuer                   = null;

            DefaultClaims claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(getJwt()).getBody();

            this.mRightToInvoke            = (ArrayList<String>) claims.get("right_to_invoke", ArrayList.class);
            this.mRightToReceive           = (ArrayList<String>) claims.get("right_to_receive", ArrayList.class);
            this.mIssuer                   = claims.get("iss", String.class);
            this.mEncodedDeviceCertificate = claims.get("device_cert", String.class);
            this.mId                       = claims.get("id", String.class);

            this.mValidity = new Validity(Long.valueOf((Integer)claims.get("validity", HashMap.class).get("start")),
                                                   Long.valueOf((Integer)claims.get("validity", HashMap.class).get("stop")));

            byte [] decodedDeviceCert = Base64.decode(mEncodedDeviceCertificate, Base64.DEFAULT);
            this.mCertificate = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decodedDeviceCert));

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    boolean deviceCertificateMatches(Certificate matching) {
        return mCertificate.equals(matching);
    }

    private boolean rightMatchesServiceIdentifierNonTailHashMatching(String right, String serviceIdentifier) {
        String rightParts[] = right.split("/", -1);
        String serviceParts[] = serviceIdentifier.split("/", -1);

        if (rightParts.length > serviceParts.length)
            return false;

        for (int i = 0; i < rightParts.length; i++) {
            if (!rightParts[i].toLowerCase().equals(serviceParts[i].toLowerCase()) && !rightParts[i].equals("+"))
                return false;
        }

        return true;
    }

    private static boolean rightMatchesServiceIdentifier(String right, String serviceIdentifier) {
        /* If for whatever reason, the service identifier or topic filter (rights string) contains 2+ '/'s in a row,
           then this is considered an empty topic level, which is not allowed, so return 'false' */
        if (right.contains("//") || serviceIdentifier.contains("//"))
            return false;

        String rightParts[] = right.split("/", -1);
        String serviceParts[] = serviceIdentifier.split("/", -1);

        if (rightParts.length == 0)
            return false;

        for (int i = 0; i < rightParts.length; i++) {
            if (i == (rightParts.length - 1) && rightParts[i].equals("#"))
                return true;

            if (i >= serviceParts.length)
                return false;

            if (!rightParts[i].toLowerCase().equals(serviceParts[i].toLowerCase()) && !rightParts[i].equals("+"))
                return false;
        }

        if (rightParts.length < serviceParts.length)
            return false;

        return true;
    }

    boolean grantsRightToReceive(String fullyQualifiedServiceIdentifier) {
        if (fullyQualifiedServiceIdentifier == null || mRightToReceive == null)
            return false;

        for (String right : mRightToReceive) {
            if (rightMatchesServiceIdentifier(right, fullyQualifiedServiceIdentifier))
                return true;
        }

        return false;
    }

    boolean grantsRightToInvoke(String fullyQualifiedServiceIdentifier) {
        if (fullyQualifiedServiceIdentifier == null || mRightToInvoke == null)
            return false;

        for (String right : mRightToInvoke) {
            if (rightMatchesServiceIdentifier(right, fullyQualifiedServiceIdentifier))
                return true;
        }

        return false;
    }

    String getJwt() {
        return mJwt;
    }

    ArrayList<String> getRightToInvoke() {
        return mRightToInvoke;
    }

    ArrayList<String> getRightToReceive() {
        return mRightToReceive;
    }

    String getIssuer() {
        return mIssuer;
    }

    String getEncodedDeviceCertificate() {
        return mEncodedDeviceCertificate;
    }

    Validity getValidity() {
        return mValidity;
    }

    String getId() {
        return mId;
    }
}

enum ValidityStatus
{
    PENDING,
    VALID,
    EXPIRED,
    INVALID
}

class Validity {
    private final static String PRETTY_DATE_TIME_FORMATTER = "MM/dd/yyyy h:mm a z";

    private Long mStart;

    private Long mStop;

    Validity(Long start, Long stop) {
        mStart = start;
        mStop = stop;
    }

    ValidityStatus getStatus() {
        Long currentTime = System.currentTimeMillis() / 1000;

        if (mStart > mStop) return ValidityStatus.INVALID;
        if (currentTime < mStart) return ValidityStatus.PENDING;
        if (currentTime > mStop) return ValidityStatus.EXPIRED;

        return ValidityStatus.VALID;
    }

    public Long getStart() {
        return mStart;
    }

    public Long getStop() {
        return mStop;
    }
}