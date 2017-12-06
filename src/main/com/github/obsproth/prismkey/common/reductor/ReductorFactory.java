package com.github.obsproth.prismkey.common.reductor;

//
// PasswordMixerの実装を簡単めに生成するためのファクトリ
//
public class ReductorFactory {
    private static ReductorFactory instance = null;
    private static ReductorFactory getInstance() {
        if (instance == null) {
            instance = new ReductorFactory();
        }
        return instance;
    }
    // 新しいMixerクラスを作ったらここに登録してください
    IReductor alnum, base64;
    private ReductorFactory () {
        this.alnum = new AlNumReductor();
        this.base64 = new Base64Reductor();
    }

    public static int ALNUM = 0;
    public static int BASE64 = 1;
    
    public static IReductor getMixer(String name) {
        if (name.toLowerCase().equals("alnum")) {
            return getInstance().alnum;
        } else if (name.toLowerCase().equals("base64")) {
            return getInstance().base64;
        }
        throw new IllegalArgumentException("Theres no Mixer named "+name);
    }

    public static IReductor getMixer(int typeId) {
        if (typeId == ALNUM) {
            return getInstance().alnum;
        } else if (typeId == BASE64) {
            return getInstance().base64;
        }
        throw new IllegalArgumentException("No such typeId "+Integer.toString(typeId));
    }

}