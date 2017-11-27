import junit.framework.*;
import com.github.obsproth.obspassword.common.testutil.*;

import java.text.ParseException;

import com.github.obsproth.obspassword.common.reductor.Base64Reductor;

public class Base64ReductorTest extends TestCase {
    public Base64ReductorTest(String name) {
        super(name);
    }
    private static void assertCharsEquals(char[] a, String b) {
        TestCase.assertEquals(a.length, b.length());
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b.charAt(i)) {
                TestCase.fail(String.format("\"{}\" != \"{}\"", a.toString(), b));
            }
        }
    }
    // test of Base64Reductor::generate function
    // generateの挙動が仕様通りに動くか(generateが存在しないので動かない)
    public void testGenerate() {
        //IReductor reductor = new Base64Reductor();
        try {
            byte[] hash = ByteEncoder.fromHexString("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
            //char[] password = reductor.generate(hash, );
            //Base64ReductorTest.assertCharsEquals(hash, "test");
        } catch (ParseException exc) {
            TestCase.fail(exc.getStackTrace().toString()+exc.toString());
        }
    }
}