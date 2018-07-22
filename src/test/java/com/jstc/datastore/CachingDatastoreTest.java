package com.jstc.datastore;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CachingDatastoreTest {
    
    private static class MappedDatastore<V> implements MutableDatastore<V> {
        
        private final Map<String, V> map;
        
        public MappedDatastore(Map<String, V> map) {
            this.map = map;
        }
        
        @Override
        public V get(String key) throws IOException {
            return map.get(key);
        }

        @Override
        public void put(String key, V value) throws IOException {
            map.put(key, value);
        }
    }
    
    private Map<String, String> cacheMap = new HashMap<>();
    private Map<String, String> originMap = new HashMap<>();
    
    private MutableDatastore<String> cache = new MappedDatastore<>(cacheMap);
    private MutableDatastore<String> origin = new MappedDatastore<>(originMap);
    
    @Test
    public void WHEN_cacheEmpty_THEN_cachePopulated() throws IOException {
        originMap.put("foo", "bar");
        
        CachingDatastore<String> cut = new CachingDatastore<>(cache, origin);
        
        String result = cut.get("foo");
        assertEquals("bar", result);
        assertEquals("bar", cacheMap.get("foo"));
    }
    
    @Test
    public void WHEN_cachePopulated_THEN_cacheUsed() throws IOException {
        originMap.put("foo", "bar");
        cacheMap.put("foo", "cachedBar");
        
        CachingDatastore<String> cut = new CachingDatastore<>(cache, origin);
        
        String result = cut.get("foo");
        assertEquals("cachedBar", result);
        assertEquals("cachedBar", cacheMap.get("foo"));
        assertEquals("bar", originMap.get("foo"));
    }

    // Leaving the exceptional cases out to save time for this demo...
    // should also test when cache.get/put throws, and when origin.get throws...
    // and branch based on whether cache is populated or not...

}
