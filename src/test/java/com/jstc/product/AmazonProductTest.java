package com.jstc.product;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AmazonProductTest {

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(AmazonProduct.class).verify();
    }
    
    @Test
    public void testSerDes() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final Map<ProductDetails, String> details = new EnumMap<>(ProductDetails.class);
        for (ProductDetails detail : ProductDetails.values()) {
            details.put(detail, randomString());
        }
        final String asin = randomString();

        
        AmazonProduct initial = new AmazonProduct(asin, details);
        String serialized1 = mapper.writeValueAsString(initial);
        AmazonProduct deserialized1 = mapper.readValue(serialized1, AmazonProduct.class);
        assertEquals(initial, deserialized1);
        
        String serialized2 = mapper.writeValueAsString(deserialized1);
        assertEquals(serialized1, serialized2);
    }
    
    private static String randomString() {
        return UUID.randomUUID().toString();
    }
    
}
