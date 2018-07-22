package com.jstc.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.jstc.datastore.MutableDatastore;
import com.jstc.datastore.SerializingDatastore;

public class SerializingDatastoreTest {

    private SerializingDatastore<Dimension> cut;
    
    @Before
    public void setup() throws IOException {
        cut = new SerializingDatastore<>(new MapBackedDatastore<>(), Dimension.class);
    }
    
    @Test
    public void WHEN_datastoreEmpty_THEN_getReturnsNull() throws IOException {
        assertNull(cut.get("someKey"));
    }
    
    @Test
    public void WHEN_put_THEN_getReturnsValue() throws IOException {
        final String key = "someKey";
        final Dimension val = new Dimension(123, 456);
        
        cut.put(key, val);
        
        assertEquals(val, cut.get(key));
    }
    
    /**
     * Simple datastore backed by a {@link HashMap}
     *
     * @param <V> - type of value
     */
    private static class MapBackedDatastore<V> implements MutableDatastore<V> {

        private final Map<String, V> map = new HashMap<>();
        
        @Override
        public V get(String key) {
            return map.get(key);
        }

        @Override
        public void put(String key, V value) {
            map.put(key, value);
        }
    }
}
