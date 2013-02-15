package gr.ekt.bte.record;

import static org.junit.Assert.*;

import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.Value;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MapRecordTests {
    private MapRecord rec_;
    private ArrayList<Value> val_;
    
    @Before
    public void setUp() {
        rec_ = new MapRecord();
        val_ = new ArrayList<Value>();
        
        val_.add(new StringValue("foo"));
        val_.add(new StringValue("bar"));
    }
    
    @Test
    public void testGetValues() {
        assertNull(rec_.getValues("test"));
        rec_.addField("test", val_);

        List<Value> nvals = rec_.getValues("test");
        assertEquals(val_.size(), nvals.size());
        
        for (int i = 0; i < nvals.size(); i++) {
            assertEquals(val_.get(i), nvals.get(i));
        }
    }

    @Test
    public void testAddField() {
        assertTrue(rec_.addField("test", val_));
        assertFalse(rec_.addField("test", val_));
    }
    
    @Test
    public void testAddValue() {
        rec_.addField("test", val_);
        Value new_val = new StringValue("baz");
        assertTrue(rec_.addValue("test", new_val));
        assertTrue(rec_.getValues("test").contains(new_val));
        assertFalse(rec_.addValue("test", new StringValue("baz")));
        assertFalse(rec_.addValue("test", new_val));
    }
    
    @Test
    public void testRemoveField() {
        assertFalse(rec_.removeField("test"));
        rec_.addField("test", val_);
        assertTrue(rec_.removeField("test"));
        assertFalse(rec_.removeField("test"));
    }
    
    @Test
    public void testRemoveValue() {
        assertFalse(rec_.removeValue("test", new StringValue("foo")));
        
        rec_.addField("test", val_);
        
        assertTrue(rec_.getValues("test").contains(val_.get(0)));
        assertTrue(rec_.removeValue("test", val_.get(0)));
        assertFalse(rec_.getValues("test").contains(new StringValue("foo")));
    }
    
    @Test
    public void testUpdateField() {
        List<Value> val = new ArrayList<Value>();
        val.add(new StringValue("bar"));
        val.add(new StringValue("baz"));
        val.add(new StringValue("moo"));
        assertFalse(rec_.updateField("test", val));
        
        rec_.addField("test", val_);
        
        assertTrue(rec_.updateField("test", val));
        
        List<Value> val2 = rec_.getValues("test");
        assertEquals(val2.size(), val.size());
        for (int i = 0; i < val2.size(); i++) {
            assertEquals(val2.get(i), val.get(i));
        }
    }
    
    @Test
    public void testUpdateValue() {
        Value val = new StringValue("baz");
        assertFalse(rec_.updateValue("test", val_.get(0), val));
        
        rec_.addField("test", val_);
        
        assertTrue(rec_.updateValue("test", val_.get(0), val));
        assertEquals(val, rec_.getValues("test").get(0));
    }
}
