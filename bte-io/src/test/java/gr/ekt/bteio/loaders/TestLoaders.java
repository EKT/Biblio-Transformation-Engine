package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.exceptions.EmptySourceException;

import java.util.ArrayList;
import java.util.List;


//import static org.junit.Assert.*;
import org.junit.Test;

public class TestLoaders {
    @Test
    public void testCreation() {
        List<String> fields = new ArrayList<String>();
        try {
            DataLoader dl = new CSVDataLoader("Test", fields);
        } catch(EmptySourceException e) {
        }
    }
}
