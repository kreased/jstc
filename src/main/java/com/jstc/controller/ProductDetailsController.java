package com.jstc.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jstc.product.AmazonProduct;
import com.jstc.product.ProductDetails;
import com.jstc.service.ProductService;

@Controller
public class ProductDetailsController {

    @Autowired
    private ProductService productService;
    
    @GetMapping("/productDetails/{asin}")
    public String productDetails(@PathVariable String asin, Model model) throws IOException {
        model.addAttribute("asin", asin);
        
        AmazonProduct product = productService.getProduct(asin); 
        for (ProductDetails detail : ProductDetails.values()) {
            String value = product.getDetail(detail);
            if (value == null) {
                value = "UNKNOWN";
            }
            
            model.addAttribute(detail.toString(), value);
        }
        
        return "productDetails";
    }

}