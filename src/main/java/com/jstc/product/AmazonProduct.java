package com.jstc.product;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.common.collect.ImmutableMap;

/**
 * Extremely basic model for an Amazon product
 */
public final class AmazonProduct {
    private final String asin;
    private final Map<ProductDetails, String> details;

    // Used for deserialization only
    public AmazonProduct() {
        this.asin = "";
        this.details = ImmutableMap.of();
    }
    
    public AmazonProduct(String asin, Map<ProductDetails, String> details) {
        this.asin = checkNotNull(asin);
        this.details = ImmutableMap.copyOf(checkNotNull(details));
    }
    
    public String getAsin() {
        return asin;
    }
    
    public String getDetail(ProductDetails detail) {
        checkNotNull(detail);
        return details.get(detail);
    }
    
    public Map<ProductDetails, String> getDetails() {
        return details;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(asin)
                .append(details)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AmazonProduct)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        AmazonProduct rhs = (AmazonProduct)other;
        return new EqualsBuilder()
                .append(asin, rhs.asin)
                .append(details, rhs.details)
                .isEquals();
    }
    
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }
}
