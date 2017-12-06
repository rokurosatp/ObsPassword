package com.github.obsproth.obspassword.common.reductor;
//
// パスワードを生成するアルゴリズムの一覧
//
public interface IReductor {
    char[] generate(byte[] hash, int length);
}
