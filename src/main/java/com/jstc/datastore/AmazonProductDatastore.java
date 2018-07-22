package com.jstc.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;
import com.jstc.product.AmazonProduct;
import com.jstc.product.ProductDetails;

/**
 * Datastore for providing {@link AmazonProduct}, using the specified details loader
 */
public class AmazonProductDatastore implements Datastore<AmazonProduct> {

    private final Datastore<Map<ProductDetails, String>> asinToDetailsLoader;
    
    public AmazonProductDatastore(Datastore<Map<ProductDetails, String>> asinToDetailsLoader) {
        this.asinToDetailsLoader = checkNotNull(asinToDetailsLoader);
    }
    
    @Override
    public AmazonProduct get(String asin) throws IOException {
        Map<ProductDetails, String> productDetails = asinToDetailsLoader.get(asin);
        return new AmazonProduct(asin, productDetails);
    }
}
