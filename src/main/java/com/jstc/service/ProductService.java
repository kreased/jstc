package com.jstc.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jstc.datastore.Datastore;
import com.jstc.product.AmazonProduct;

@Service
public class ProductService {
    
    @Autowired
    private Datastore<AmazonProduct> datastore;
    
    public AmazonProduct getProduct(String asin) throws IOException {
        return datastore.get(asin);
    }
}
