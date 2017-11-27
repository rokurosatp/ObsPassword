package com.github.obsproth.obspassword.common.testutil;
import java.text.ParseException;

public class ByteEncoder {
    public static byte[] fromHexString(String hex) throws ParseException {
        int length = hex.length();
        byte[] result = new byte[length / 2 + (length % 2)];
        for(int i = 0; i < length; i += 2) {
            result[i / 2] = Byte.parseByte(hex.substring(i, Integer.min(i + 2, length)));
        }
        return result;
    }
}