package com.payleven.inapp.hmac;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class CryptoUtils {

    public final static String BOUNCY_CASTLE_SECURITY_PROVIDER = "BC";

    public final static String HMAC_256 = "HmacSHA256";

    public final static String SHA_256 = "SHA-256";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String toBase64HmacString(String value, String key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] keyBytes = Base64.decodeBase64(new String(key.getBytes(StandardCharsets.UTF_8)));
        Mac mac = createMac(keyBytes);
        return toTrickyBase64(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    public static String toBase64HashString(String value) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] signature = createSHA256SignatureFor(value);
        return toTrickyBase64(signature);
    }

    private static Mac createMac(byte[] keyBytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        Mac mac = Mac.getInstance(HMAC_256, BOUNCY_CASTLE_SECURITY_PROVIDER);
        mac.init(new SecretKeySpec(keyBytes, HMAC_256));
        return mac;
    }

    private static byte[] createSHA256SignatureFor(String value) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest md = MessageDigest.getInstance(SHA_256, BOUNCY_CASTLE_SECURITY_PROVIDER);
        md.update(value.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    private static String toTrickyBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes).replace('/', '_').replace('+', '-');
    }

}
