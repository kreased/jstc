package com.jstc.lmdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SimpleLmdbDatastoreTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();
    
    private SimpleLmdbDatastore cut;
    
    @Before
    public void setup() throws IOException {
        cut = new SimpleLmdbDatastore(tmp.newFolder(), 1);
    }
    
    @Test
    public void WHEN_datastoreEmpty_THEN_getReturnsNull() {
        assertNull(cut.get("someKey"));
    }
    
    @Test
    public void WHEN_put_THEN_getReturnsValue() {
        final String key = "someKey";
        final String val = "someValue";
        
        cut.put(key, val);
        
        assertEquals(val, cut.get(key));
    }
}
