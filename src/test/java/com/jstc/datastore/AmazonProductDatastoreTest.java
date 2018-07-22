package com.jstc.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.jstc.product.AmazonProduct;
import com.jstc.product.ProductDetails;

public class AmazonProductDatastoreTest {

    private Map<ProductDetails, String> productDetails = new EnumMap<>(ProductDetails.class);
    
    @Before
    public void setup() {
        // toss in some random product details
        for (ProductDetails detail : ProductDetails.values()) {
            productDetails.put(detail, UUID.randomUUID().toString());
        }
    }
    
    @Test
    public void WHEN_get_THEN_detailsUsed() throws Exception {
        final Datastore<Map<ProductDetails, String>> detailLoader = (asin) -> productDetails;
        final String asin = "B002QYW8LW";
        
        AmazonProductDatastore cut = new AmazonProductDatastore(detailLoader);
        
        AmazonProduct product = cut.get(asin);
        for (ProductDetails detail : ProductDetails.values()) {
            assertEquals(productDetails.get(detail), product.getDetail(detail));
        }
        assertEquals(asin, product.getAsin());
    }
    
    @Test
    public void WHEN_detailLoaderThrows_THEN_exceptionPropagated() {
        final IOException expected = new IOException("Testing");
        final Datastore<Map<ProductDetails, String>> detailLoader = (asin) -> {
            throw expected;
        };
        final String asin = "B002QYW8LW";
        
        AmazonProductDatastore cut = new AmazonProductDatastore(detailLoader);
        
        try {
            cut.get(asin);
        } catch (IOException e) {
            assertSame(expected, e);
        }
    }
}
