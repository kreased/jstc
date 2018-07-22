package com.jstc.datastore.scraper;

import static java.util.function.Function.identity;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.jstc.product.ProductDetails;

/**
 * Loads product details from a jsoup document representing a scraped Amazon.com
 * page.
 * 
 * It's far from complete, since there are many different ways amazon pages get
 * displayed, and probably some ASINs won't fit within the selectors tested.
 * However, as those products get found, we can update how we find that info.
 * 
 * This is probably the most fragile class in this scraping system, as it
 * depends heavily on how amazon.com displays the data on their website.
 * 
 * Note - items can have multiple categories & ranks within those categories.
 * For purposes of this demo, I'm only finding the first category and rank.
 *
 * It would be WAY better to use the real Amazon product API...
 */
public class ScrapingProductDetailsLoader {

    // Map of detail to selector-cleaner mapping - some selectors need special post-processing to extract the info
    private static final EnumMap<ProductDetails, Map<String, Function<String, String>>> QUERIES = new EnumMap<>(ProductDetails.class);
    static {
        QUERIES.put(ProductDetails.TITLE, ImmutableMap.of(
                "span[id=productTitle]", identity()
                ));
        
        QUERIES.put(ProductDetails.CATEGORY, ImmutableMap.of(
                "tr#SalesRank>td.value", ScrapeCleaners::extractCategoryFromSalesRank,
                "#SalesRank", ScrapeCleaners::extractCategoryFromSalesRank,
                "table#productDetails_detailBullets_sections1>tbody>tr>td>span>span", ScrapeCleaners::extractCategoryFromSalesRank,
                "span.cat-link", identity()
//                "a.a-link-normal.a-color-tertiary", identity()      // not the most accurate category... should we use it?
                ));
        
        QUERIES.put(ProductDetails.RANK, ImmutableMap.of(
                //"i.p13n-best-seller-badge", identity()            // this is not reliable at all, grabs extra noise on the page
                "tr#SalesRank>td.value", ScrapeCleaners::extractRankFromSalesRank,
                "table#productDetails_detailBullets_sections1>tbody>tr>td>span>span", ScrapeCleaners::extractRankFromSalesRank,
                "#SalesRank", ScrapeCleaners::extractRankFromSalesRank
                ));
        
        QUERIES.put(ProductDetails.DIMENSIONS, ImmutableMap.of(
                "tr.size-weight:contains(Dimensions) > td.value", identity(),
                "table.prodDetTable>tbody>tr>td.a-size-base:contains(inches)", identity()
                ));
    }
    
    private static final String SCRAPE_BASE = "https://www.amazon.com/dp/%s";
    
    // User agent used to get the web page.
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";

    // Spoof the scraping by telling the page from where the request has been sent:
    private static final String REFERRER = "https://www.google.com";

    private final String asin;
    
    public ScrapingProductDetailsLoader(String asin) {
        this.asin = asin;
    }
    
    @VisibleForTesting
    Document loadDocument() throws IOException {
        return Jsoup.connect(String.format(SCRAPE_BASE, asin))
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();
    }
    
    public Map<ProductDetails, String> getAllDetails() throws IOException {
        final Document document = loadDocument();
        Map<ProductDetails, String> result = new EnumMap<>(ProductDetails.class);
        for (ProductDetails detail : ProductDetails.values()) {
            String value = getDetail(document, detail);
            if (value != null) {
                result.put(detail, value);
            }
        }
        return result;
    }
    
    /**
     * @param document
     * @param detail
     * @return detail value if available in the document, null otherwise
     */
    private String getDetail(Document document, ProductDetails detail) {
        Map<String, Function<String, String>> queryMap = QUERIES.get(detail);
        if (queryMap == null) {
            return null;
        }

        // go over each possible query, return the first valid result
        for (Entry<String, Function<String, String>> queryEntry : queryMap.entrySet()) { 
            try {
                final String query = queryEntry.getKey();
                final Function<String, String> cleaner = queryEntry.getValue();
                Elements elements = document.select(query);
    
                if (elements != null && elements.size() > 0) {
                    final String rawValue = elements.get(0).text();
                    if (!StringUtils.isEmpty(rawValue)) {
                        final String cleanedValue = cleaner.apply(rawValue);
                        if (!StringUtils.isEmpty(cleanedValue)) {
                            return cleanedValue;
                        }
                    }
                }
    
            } catch (Selector.SelectorParseException e) {
                e.printStackTrace();    // oops, invalid css query
            }
        
        }
        return null;
    }
    
    private static class ScrapeCleaners {
        public static String extractCategoryFromSalesRank(String input) {
            int beginIndex = input.indexOf("in ") + 3;
            int endIndex = input.indexOf(" (See ");
            if (endIndex == -1) {
                endIndex = input.length();
            }
            
            return input.substring(beginIndex, endIndex);
        }
        
        public static String extractRankFromSalesRank(String input) {
            int beginIndex = input.indexOf("#") + 1;
            input = input.substring(beginIndex);        // pre-trim because # might not be at beginning
            int endIndex = input.indexOf(" ");
            return input.substring(0, endIndex);
        }
    }
}
