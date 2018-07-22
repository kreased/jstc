package com.jstc.datastore;

import java.io.IOException;

/**
 * Key-value store with basic get/put operations. Keys and values must be non-null.
 * @param <V> - type of value to store
 */
public interface Datastore<V> {
    /**
     * @param key
     * @return result for the key, or null if not found
     * @throws IOException
     */
    V get(String key) throws IOException;
}
