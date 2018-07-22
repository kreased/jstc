package com.jstc.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

/**
 * Datastore that tries to do lookup in a "cache" before lookup in origin. Will update cache with origin results as needed.
 * Doesn't support negative caching, uses basic synchronization to support thread-safety of gets (which may result in cache writes)
 *
 * @param <V> - type of value stored
 */
public class CachingDatastore <V> implements Datastore<V> {

    private final MutableDatastore<V> cache;
    private final Datastore<V> origin;
    
    public CachingDatastore(MutableDatastore<V> cache, Datastore<V> origin) {
        this.cache = checkNotNull(cache);
        this.origin = checkNotNull(origin);
    }
    
    @Override
    public synchronized V get(String key) throws IOException {
        V value = cache.get(key);
        if (value == null) {
            value = origin.get(key);
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }
}
