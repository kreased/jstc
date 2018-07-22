package com.jstc.datastore;

import java.io.IOException;

/**
 * A {@link Datastore} that allows modifications to key-value relations
 * @param <V> - type of value stored
 */
public interface MutableDatastore<V> extends Datastore<V> {
    /**
     * Stores the given value for the key
     * @param key
     * @param value
     * @throws IOException
     */
    void put(String key, V value) throws IOException;
}
