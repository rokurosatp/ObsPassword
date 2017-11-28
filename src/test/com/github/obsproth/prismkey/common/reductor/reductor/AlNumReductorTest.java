import junit.framework.*;
import com.github.obsproth.prismkey.common.testutil.*;

import java.text.ParseException;

import com.github.obsproth.prismkey.common.reductor.AlNumReductor;
import com.github.obsproth.prismkey.common.reductor.IReductor;

public class AlNumReductorTest extends TestCase {
    public AlNumReductorTest(String name){
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
    public void testGenerate() {
        IReductor reductor = new AlNumReductor();
        try {
            // エンコード前
            byte[] hash = ByteEncoder.fromHexString("9ed5b67f9d130534dd9e7be81bc077b2f78d7042c8612ec26dca1eb6fd6a2cd29b7c5d22072e090de44aff80934313589740ed8f0c1792e639c50f1e68e33054da6597fe0a54150714c0295660bb016a1c94f2ee37cd51c3de735237db7eb4129606ab70dc5b2fedc4561571edf9181bd782e98b11ea7fe646926f8998bc2a8714b11b84f7493274b72e22654c668f9b0b28ecd7c4d972e403422dc5f72b0b42370d17dc33c4045576c13c786b7ea5e38e85bde1605b95bbd77505c0d2d7c053a8c210f034b66a5bd55052c493eb83fa6aeef5a14d19b5d3f008040a58ce7e0a468effc1edc9cccac2f0d7822301234947f50e601a3fa9438e3108851b75bf");
            // エンコード後の文字列
            //"IB6dHtf0JI9UBg529rYeoJUiVqE6fSSyFaFIhUjnQmhexftABcZtmxwS5lpEQPWwGNBgkwvhugPyIbbSCy403ttjK1u3Hc4sAgVYIDVZkyvZZbyBDgVprWdSiwXnCcQlu1Bi9lY27UINoOtFlOYDkF0QdeTl9Rle3nxIZkex4h86TcPPsjdNIDzbD3fgyDgvSiq206SDBsukxXhcS07Lpz5z2iekAuckishhZpsqi2DgJbJlj7oIAbTfsXijB3f"
            char[] password;
            password = reductor.generate(ByteEncoder.fromHexString("00"), 1);
            AlNumReductorTest.assertCharsEquals(password, "a");
            //16 10 20 14 26 1a
            password = reductor.generate(ByteEncoder.fromHexString("1a"), 1);
            AlNumReductorTest.assertCharsEquals(password, "A");
            password = reductor.generate(ByteEncoder.fromHexString("34"), 1);
            AlNumReductorTest.assertCharsEquals(password, "0");
            password = reductor.generate(hash, 4);
            AlNumReductorTest.assertCharsEquals(password, "IB6d");
            password = reductor.generate(hash, 16);
            AlNumReductorTest.assertCharsEquals(password, "IB6dHtf0JI9UBg52");
            password = reductor.generate(hash, 255);
            AlNumReductorTest.assertCharsEquals(password, "IB6dHtf0JI9UBg529rYeoJUiVqE6fSSyFaFIhUjnQmhexftABcZtmxwS5lpEQPWwGNBgkwvhugPyIbbSCy403ttjK1u3Hc4sAgVYIDVZkyvZZbyBDgVprWdSiwXnCcQlu1Bi9lY27UINoOtFlOYDkF0QdeTl9Rle3nxIZkex4h86TcPPsjdNIDzbD3fgyDgvSiq206SDBsukxXhcS07Lpz5z2iekAuckishhZpsqi2DgJbJlj7oIAbTfsXijB3f");
        } catch (ParseException exc) {
            TestCase.fail(exc.getStackTrace().toString()+exc.toString());
        }
    }
}