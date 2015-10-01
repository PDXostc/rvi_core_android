package com.jaguarlandrover.rvi;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2015 Jaguar Land Rover.
 *
 * This program is licensed under the terms and conditions of the
 * Mozilla Public License, version 2.0. The full text of the
 * Mozilla Public License is at https://www.mozilla.org/MPL/2.0/
 *
 * File:    KeyManager.java
 * Project: HVACDemo
 *
 * Created by Lilli Szafranski on 10/1/15.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.*;

import android.util.Base64;

//import org.bouncycastle.util.encoders.Base64;
//import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import io.jsonwebtoken.impl.crypto.RsaProvider;

import static android.content.Context.MODE_PRIVATE;

/* Code from Stack-Overflow question/answer: http://stackoverflow.com/a/18115456 */

class KeyManager
{
    private final static String TAG = "HVACDemo:KeyManager";

    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    PublicKey pubKey;
    PrivateKey privKey;
    Context context;

//    protected KeyManager(Context context) {
    protected KeyManager() {
//        this.context = context;
//        SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
    }

//    public void generateKeys() {
//        try {
//            KeyPairGenerator generator;
//            generator = KeyPairGenerator.getInstance("RSA", "BC");
//            generator.initialize(4096, new SecureRandom());
//
//            KeyPair pair = generator.generateKeyPair();
//
//            pubKey = pair.getPublic();
//            privKey = pair.getPrivate();
//
//            byte[] publicKeyBytes = pubKey.getEncoded();
//            String pubKeyStr = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));
//
//            byte[] privKeyBytes = privKey.getEncoded();
//            String privKeyStr = new String(Base64.encode(privKeyBytes, Base64.DEFAULT));
//
//            SPE = SP.edit();
//            SPE.putString("PublicKey", pubKeyStr);
//            SPE.putString("PrivateKey", privKeyStr);
//            SPE.commit();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        }
//    }


    public static PublicKey getPublicKey() {
        //if (!SP.contains("PublicKey")) generateKeys();

        //String pubKeyStr = SP.getString("PublicKey", "");
        String pubKeyStr = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApkRFT75OhNvLGZixkslW\n" +
                "k6bVXWwMRXx5rj46HqOOp9AW3rGtEqwbXHGPDONY9ur7RBIRHi9lFTjG/V4Ycvdu\n" +
                "zwt3cr7YqgrGW7bIYitgXYrv8ymveX0ZVeBRwv2Ij96Ybh1P7BReq0oiAJNYsXpL\n" +
                "wrPX24Bxz27I2oL6ZWLu79EtNXnGdkDiWbWXdjwxN5A8MgBkn+qzBDvEMpvClNV5\n" +
                "s3dOtBrFUiFHY99jwznCN8tpuMReASHjcM46lmFHDEUyUmiBby4pSGpQVTg/QhO0\n" +
                "8RrTNDHytvH/xMdrD99I4HrBHgM6eGJYSzfuoyI3lyKZlkOxFlYt88znCYVz2ulu\n" +
                "urjwDqEGzuqZ3TC+WbBnmdQrYuvgh0xyFXZa3DHS8dCorUMt0W9vWsB7maC+KZgB\n" +
                "40P/I/jsFC1DlYNIzRYJ3Ua4nnj8IbaXvbyMdDoQ5tQPmbnoKxo6ZIM3hv+K196O\n" +
                "G3iP8c1TdzdAmRhjFEoXLShp1Y3Ek5O/ifPc6nY+IDgbyOFCo0MpFWCjRODKgh51\n" +
                "aY1nfJ00sFc86IAmtgQYyjdFUbBx/Tpdhz2h7E7s9wm48I2WQ8Klv1KSEW/nuIdr\n" +
                "H+nmab0V5CSYFsUs0ZzI3iTRbodT53vyJ+SbB4xK3vhXxp7mJaBm4xHjc0cnypUa\n" +
                "AUMXTi0cduJ6Zt68ieVRc3MCAwEAAQ==";

        byte[] sigBytes = Base64.decode(pubKeyStr, Base64.DEFAULT);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;

        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            return keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

//    public String getPublicKeyAsString() {
//        return SP.getString("PublicKey", "");
//    }

    public static PrivateKey getPrivateKey() {
        //if (!SP.contains("PrivateKey")) generateKeys();

        //String privKeyStr = SP.getString("PrivateKey", "");
        String privKeyStr = "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";

        byte[] sigBytes = Base64.decode(privKeyStr, Base64.DEFAULT);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(sigBytes);

        KeyFactory keyFact = null;

        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            return keyFact.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPrivateKeyAsString() {
        return SP.getString("PrivateKey", "");
    }
}
