package com.jstc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jstc.datastore.AmazonProductDatastore;
import com.jstc.datastore.CachingDatastore;
import com.jstc.datastore.Datastore;
import com.jstc.datastore.MutableDatastore;
import com.jstc.datastore.SerializingDatastore;
import com.jstc.datastore.scraper.ScrapingProductDetailsLoader;
import com.jstc.lmdb.SimpleLmdbDatastore;
import com.jstc.product.AmazonProduct;
import com.jstc.product.ProductDetails;

@Configuration
public class Config {
    
    // if we don't pass this to LMDB, it'll blow up after the first read (maxReaders is 1 by default)
    @Value("${server.tomcat.max-threads}")
    private int maxReadThreads;
    
    @Bean
    public Datastore<AmazonProduct> createDatastore() {

        // Create the origin - can easily change source just by changing detailLoader
        final Datastore<Map<ProductDetails, String>> detailLoader = this::loadFromScraper;
        final Datastore<AmazonProduct> originLoader = new AmazonProductDatastore(detailLoader); 
        
        // Create the db cache 
        final File datastoreLocation = new File("datastore");
        datastoreLocation.mkdirs();
        final MutableDatastore<String> rawDatabase = new SimpleLmdbDatastore(datastoreLocation, maxReadThreads);
        final MutableDatastore<AmazonProduct> cacheDatastore = new SerializingDatastore<>(rawDatabase, AmazonProduct.class);
        
        // Put the db cache in front of the origin
        return new CachingDatastore<>(cacheDatastore, originLoader);
    }
    
    private Map<ProductDetails, String> loadFromScraper(String asin) throws IOException {
        return new ScrapingProductDetailsLoader(asin).getAllDetails();
    }
}
