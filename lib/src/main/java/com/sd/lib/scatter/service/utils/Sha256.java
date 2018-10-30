package com.sd.lib.scatter.service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256
{
    private Sha256()
    {
    }

    public static String sha(String text)
    {
        try
        {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(text.getBytes());

            final byte[] digestBytes = digest.digest();
            return toHex(digestBytes);
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes)
    {
        final StringBuilder builder = new StringBuilder();

        final int length = bytes.length;
        for (int i = 0; i < length; i++)
        {
            final int unsignedByte = bytes[i] & 0xFF;
            if (unsignedByte < 16)
                builder.append("0");

            builder.append(Integer.toHexString(unsignedByte));
        }
        return builder.toString();
    }
}
