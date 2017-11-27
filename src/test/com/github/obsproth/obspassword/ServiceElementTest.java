import junit.framework.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

import com.github.obsproth.obspassword.ServiceElement;

public class ServiceElementTest extends TestCase {
    public ServiceElementTest(String name) {
        super(name);
    }

    public void testToCSV() throws InstantiationException,
        NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Constructor<ServiceElement> constructor = ServiceElement.class.getDeclaredConstructor(String.class, int.class, String.class, int.class);
        constructor.setAccessible(true);
        ServiceElement elem = constructor.newInstance("name", 10, "hogehoge", 1);
        TestCase.assertEquals("name,10,hogehoge,1", elem.asCSV());
            
        elem = new ServiceElement("name2", 10, "hogehog2");
        TestCase.assertEquals("name2,10,hogehog2,1", elem.asCSV());
    }

    public void testFromCSV() {
        ServiceElement elem = ServiceElement.buildFromCSV("name3,10,hogepal3,1");
        TestCase.assertEquals("name3", elem.getServiceName());
        TestCase.assertEquals(10, elem.getLength());
        TestCase.assertEquals("hogepal3", elem.getBaseHash());
        TestCase.assertEquals(1, elem.getVersion());
    }
}
