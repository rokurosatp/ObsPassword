package com.github.obsproth.prismkey.common.reductor;

public class AlNumReductor implements IReductor {
    public char[] generate(byte[] hash, int length) {
        char[] result = new char[length];
        int codePoint;
        for(int i = 0; i < length; i++) {
            // やってることは非常に簡単でハッシュの1バイト文に対して64(文字種数)で割った余りを求める
            // a-z,A-Z,0-9のマッピングに基づいて得られる文字を文字列中の対応する部分に割り当てる
            // ハッシュ1バイト -> パスワード１文字　対応になっている。
            codePoint = Byte.toUnsignedInt(hash[i]) % 62;
            if (0 <= codePoint && codePoint < 26) {
                Character.toChars('a' + codePoint, result, i);
            } else if (26 <= codePoint && codePoint < 52) {
                Character.toChars('A' + codePoint - 26, result, i);
            } else {
                Character.toChars('0' + codePoint - 52, result, i);
            }
        }
        codePoint = 0;
        return result;
    }  
}