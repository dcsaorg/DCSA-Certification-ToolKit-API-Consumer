package org.dcsa.ctk.consumer.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SignatureUtility {

    public static String getSignature(String encodedKey, String payload)  {
        String notificationSignature="sha256=";
        byte[] key = Base64.getDecoder().decode(encodedKey.getBytes(StandardCharsets.UTF_8));
        byte[] payloadByteArray = payload.getBytes(StandardCharsets.UTF_8);
        byte[] signature;
        try {
            signature = computeSignature(key, payloadByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        notificationSignature+= Hex.encodeHexString(signature);
      //  System.out.println("Notification-Signature:"+ notificationSignature);
      //  System.out.println("< --- PAY LOAD (" + payloadByteArray.length + " bytes) --->");
      //  System.out.println(payload);
        return notificationSignature;
    }

    private static byte[]   computeSignature(byte[] secretKey, byte[] payload) throws Exception {
        final String javaAlgorithmName = "HmacSHA256";
        Mac mac = Mac.getInstance(javaAlgorithmName);
        mac.init(new SecretKeySpec(secretKey, javaAlgorithmName));
        return mac.doFinal(payload);
    }
}
