package com.jstc.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link Datastore} that uses Jackson to serialize objects to string and store in an underlying delegate
 * @param <V> type of object this datastore has
 */
public class SerializingDatastore<V> implements MutableDatastore<V> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final MutableDatastore<String> delegateStore;
    private final Class<V> valueType;
    
    public SerializingDatastore(MutableDatastore<String> delegateStore, Class<V> valueType) {
        this.delegateStore = checkNotNull(delegateStore);
        this.valueType = checkNotNull(valueType);
    }
    
    @Override
    public V get(String key) throws IOException {
        final String valueString = delegateStore.get(key);
        if (valueString == null) {
            return null;
        }
        return MAPPER.readValue(valueString, valueType);
    }

    @Override
    public void put(String key, V value) throws IOException {
        final String valueString = MAPPER.writeValueAsString(value);
        delegateStore.put(key, valueString);
    }
}
