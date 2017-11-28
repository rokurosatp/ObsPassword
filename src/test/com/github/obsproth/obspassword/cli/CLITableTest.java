import junit.framework.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.obsproth.obspassword.cli.CLITable;

public class CLITableTest extends TestCase {
    public CLITableTest(String name) {
        super(name);
    }
    
    public void testToString() {
        CLITable table = new CLITable();
        table.addColumn("A", "d", true);
        table.addColumn("B", "d", true);
        table.addRow(new Integer(0), new Integer(1));
        TestCase.assertEquals(
            "A  B \n"+
            "-- --\n"+
            "0  1 \n",
            table.toString()
        );
        table = new CLITable();
        table.addColumn("A", "d", true);
        table.addColumn("B", "s", 10);
        table.addRow(new Integer(0), "test");
        TestCase.assertEquals(
            "A  B             \n"+
            "-- --------------\n"+
            "0  test          \n",
            table.toString()
        );
    }
    
    public void testAddRow() throws NoSuchFieldException, IllegalAccessException {
        CLITable table = new CLITable();
        table.addColumn("A", "d", true);
        table.addColumn("B", "d", true);
        table.addRow(new Integer(1), new Integer(1));
        boolean thrown = false;
        // 引数の数が合わないと失敗する
        try {
            table.addRow(new Integer(1));
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        TestCase.assertTrue("Exception must be thrown when illegal count of addRow arguments", thrown == true);
        thrown = false;
        // 引数の数が合わないと失敗する
        try {
            table.addRow(new Integer(1), new Integer(1), new Integer(1));
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        TestCase.assertTrue("Exception must be thrown when illegal count of addRow arguments", thrown == true);
        thrown = false;
        // 引数の型が合わないと失敗する
        try {
            table.addRow(new Integer(1), "test");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        TestCase.assertTrue("Exception must be thrown when illegal type of addRow arguments", thrown == true);
        
        Field field = CLITable.class.getDeclaredField("rows");
        field.setAccessible(true);
        List<List<Object>> actual = (List<List<Object>>)field.get(table);
        List<List<Object>> expected = new ArrayList<List<Object>>();
        expected.add(new ArrayList<Object>());
        expected.get(0).add(new Integer(1));
        expected.get(0).add(new Integer(1));
        for(int i = 0; i < actual.size(); i++) {
            TestCase.assertEquals(expected.get(i), actual.get(i));
        }
    }
}