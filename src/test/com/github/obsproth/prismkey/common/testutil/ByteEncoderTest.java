import junit.framework.*;

import java.text.ParseException;

import com.github.obsproth.prismkey.common.testutil.*;

public class ByteEncoderTest extends TestCase {
    public ByteEncoderTest(String name) {
        super(name);
    }

    public void testFromHex() throws ParseException {
        TestCase.assertEquals((byte)(-0x01), (byte)(0xff));
        TestCase.assertEquals((byte)(0xa1), (byte)(Integer.parseInt("a1", 16)));
        TestCase.assertEquals((byte)0 , ByteEncoder.fromHexString("00")[0]);
        TestCase.assertEquals((byte)0xa1 , ByteEncoder.fromHexString("a1")[0]);
        TestCase.assertEquals((byte)0xff , ByteEncoder.fromHexString("ff")[0]);
        TestCase.assertEquals((byte)0xff , ByteEncoder.fromHexString("FF")[0]);
    }
}