import junit.framework.*;

import com.github.obsproth.obspassword.ServiceElement;
import com.github.obsproth.obspassword.cli.ServiceTable;


public class ServiceTableTest extends TestCase {
    public ServiceTableTest(String name) {
        super(name);
    }

    public void testAdd() {
        ServiceTable table = new ServiceTable();
        TestCase.assertEquals(0, table.size());
        table.add("test", 4, "hogehoge");
        TestCase.assertEquals(1, table.size());
        ServiceElement elem = table.get(0);
        TestCase.assertEquals("test", elem.getServiceName());
        TestCase.assertEquals(4, elem.getLength());
        TestCase.assertEquals("hogehoge", elem.getBaseHash());
    }
}
