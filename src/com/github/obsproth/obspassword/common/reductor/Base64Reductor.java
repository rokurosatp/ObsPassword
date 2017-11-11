package com.github.obsproth.obspassword.common.reductor;
//
// Base64を利用した英数記号混在ハッシュ→パスワード変換
//
import java.util.Base64;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
//
// Base64を利用した英数記号混在ハッシュ→パスワード変換
//
public class Base64Reductor implements IReductor {

    char[] resizedPassword(char[] password, int length) {
        char[] subChars = Arrays.copyOfRange(password, 0, length);
        Arrays.fill(password, '\n');
        return subChars;
    }

    public char[] generate(byte[] hash, int length) {
        byte[] encodedBytes = Base64.getEncoder().encode(hash);
        char[] encoded = Charset.forName("UTF-8")
            .decode(ByteBuffer.wrap(encodedBytes)).array();
        Arrays.fill(encodedBytes, (byte)0x20);
        return resizedPassword(encoded, length);
    }
}
