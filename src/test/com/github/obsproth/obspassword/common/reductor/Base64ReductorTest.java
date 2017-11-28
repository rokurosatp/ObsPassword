import junit.framework.*;
import com.github.obsproth.obspassword.common.testutil.*;

import java.text.ParseException;

import com.github.obsproth.obspassword.common.reductor.Base64Reductor;
import com.github.obsproth.obspassword.common.reductor.IReductor;

public class Base64ReductorTest extends TestCase {
    public Base64ReductorTest(String name) {
        super(name);
    }
    //
    // char配列とStringに対するassertEquals
    //
    private static void assertCharsEquals(char[] a, String b) {
        TestCase.assertEquals(a.length, b.length());
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b.charAt(i)) {
                TestCase.fail(String.format("\"%s\" != \"%s\"", String.valueOf(a), b));
            }
        }
    }
    // test of Base64Reductor::generate function
    // generateの挙動が仕様通りに動くか(generateが存在しないので動かない)
    public void testGenerate() {
        IReductor reductor = new Base64Reductor();
        try {
            // エンコード前
            byte[] hash = ByteEncoder.fromHexString("0d020008030e020507100e0b070701090608010b0b0e0410010503050a0e0e0a080f06020705020c09070a010d080f010202060210070d030e020c0902010b0f020f07040d03080e030b0800000d0c0210020e0c0d0410000d010d02100e100b0b0f090810060310070203040d0b08020a0910070e0d0309060d050f0e0f0d0c0e030f09040a0b040602100f04100a070007020e0d0a0c08060e070708040310080d091008040e0a040e0a0c0a08090f0e0c0d08070f0508090c0d01080b01050510020f0b0d06080a0a0f010b040500090305040f060e0d00070b0b0505081006010307030b031005020d060c030f070b0600060309090009080f050f080c");
            // エンコード後の文字列
            //"DQIACAMOAgUHEA4LBwcBCQYIAQsLDgQQAQUDBQoODgoIDwYCBwUCDAkHCgENCA8BAgIGAhAHDQMO\nAgwJAgELDwIPBwQNAwgOAwsIAAANDAIQAg4MDQQQAA0BDQIQDhALCw8JCBAGAxAHAgMEDQsIAgoJ\nEAcODQMJBg0FDw4PDQwOAw8JBAoLBAYCEA8EEAoHAAcCDg0KDAgGDgcHCAQDEAgNCRAIBA4KBA4K\nDAoICQ8ODA0IBw8FCAkMDQEICwEFBRACDwsNBggKCg8BCwQFAAkDBQQPBg4NAAcLCwUFCBAGAQMH\nAwsDEAUCDQYMAw8HCwYABgMJCQAJCA8FDwgM\n";
            char[] password = reductor.generate(hash, 4);
            Base64ReductorTest.assertCharsEquals(password, "DQIA");
            password = reductor.generate(hash, 16);
            Base64ReductorTest.assertCharsEquals(password, "DQIACAMOAgUHEA4L");
            password = reductor.generate(hash, 76);
            Base64ReductorTest.assertCharsEquals(password, "DQIACAMOAgUHEA4LBwcBCQYIAQsLDgQQAQUDBQoODgoIDwYCBwUCDAkHCgENCA8BAgIGAhAHDQMO");
            password = reductor.generate(hash, 77);
            Base64ReductorTest.assertCharsEquals(password, "DQIACAMOAgUHEA4LBwcBCQYIAQsLDgQQAQUDBQoODgoIDwYCBwUCDAkHCgENCA8BAgIGAhAHDQMOA");
            password = reductor.generate(hash, 80);
            Base64ReductorTest.assertCharsEquals(password, "DQIACAMOAgUHEA4LBwcBCQYIAQsLDgQQAQUDBQoODgoIDwYCBwUCDAkHCgENCA8BAgIGAhAHDQMOAgwJ");
            password = reductor.generate(hash, 340);
            Base64ReductorTest.assertCharsEquals(password, "DQIACAMOAgUHEA4LBwcBCQYIAQsLDgQQAQUDBQoODgoIDwYCBwUCDAkHCgENCA8BAgIGAhAHDQMOAgwJAgELDwIPBwQNAwgOAwsIAAANDAIQAg4MDQQQAA0BDQIQDhALCw8JCBAGAxAHAgMEDQsIAgoJEAcODQMJBg0FDw4PDQwOAw8JBAoLBAYCEA8EEAoHAAcCDg0KDAgGDgcHCAQDEAgNCRAIBA4KBA4KDAoICQ8ODA0IBw8FCAkMDQEICwEFBRACDwsNBggKCg8BCwQFAAkDBQQPBg4NAAcLCwUFCBAGAQMHAwsDEAUCDQYMAw8HCwYABgMJCQAJCA8FDwgM");
        } catch (ParseException exc) {
            TestCase.fail(exc.getStackTrace().toString()+exc.toString());
        }
    }
}