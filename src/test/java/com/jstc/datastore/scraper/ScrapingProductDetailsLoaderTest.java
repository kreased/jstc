package com.jstc.datastore.scraper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import com.jstc.product.ProductDetails;

/**
 * NOTE - this test is NOT comprehensive - as a scraper, it's only as good as
 * sample inputs verified. I don't really feel like verifying all of amazon.com
 * works in this scraper, so I'm only testing a handful of pages. Each page I
 * tested revealed something new, so I suspect there's a lot of pages that won't
 * work right now.
 */
public class ScrapingProductDetailsLoaderTest {

    private static final String TEST_PATTERN = "src/test/java/com/jstc/datastore/scraper/testResources/%s.xml";
    private static File getTestFile(String asin) {
        String resolvedPath = String.format(TEST_PATTERN, asin);
        return new File(resolvedPath);
    }
    
    /**
     * Not a real test - used to generate sample static input files to drive other tests
     * @throws Exception
     */
    @Test
    @Ignore
    public void testBuilder() throws Exception {
        final String asin = "B01MSSJM77";
        
        String result = new ScrapingProductDetailsLoader(asin).loadDocument().toString();
        final File f = getTestFile(asin);
        FileUtils.writeStringToFile(f, result, UTF_8);
        System.out.println("Wrote: " + f.getAbsolutePath());
    }
    
    private static ScrapingProductDetailsLoader createCut(String asin) throws IOException {
        return new ScrapingProductDetailsLoader(asin) {
            @Override
            public Document loadDocument() throws IOException {
                File input = getTestFile(asin);
                return Jsoup.parse(input, UTF_8.toString(), "https://www.amazon.com/");
            }
        };
    }
    
    // Baby Banana - initial sample listing with all properties 
    @Test
    public void B002QYW8LW() throws IOException {
        ScrapingProductDetailsLoader cut = createCut("B002QYW8LW");
        Map<ProductDetails, String> details = cut.getAllDetails();

        assertEquals("Baby Banana Infant Training Toothbrush and Teether, Yellow", details.get(ProductDetails.TITLE));
        assertEquals("Baby", details.get(ProductDetails.CATEGORY));
        assertEquals("16", details.get(ProductDetails.RANK));
        assertEquals("4.3 x 0.4 x 7.9 inches", details.get(ProductDetails.DIMENSIONS));
    }
    
    // Fire Tablet - doesn't show dimensions/rank in feasible way to scrape; category uses different selector
    @Test
    public void B01GEW27DA() throws IOException {
        ScrapingProductDetailsLoader cut = createCut("B01GEW27DA");
        Map<ProductDetails, String> details = cut.getAllDetails();

        assertEquals("Fire 7 Tablet with Alexa, 7\" Display, 8 GB, Black - with Special Offers", details.get(ProductDetails.TITLE));
        assertEquals("Computers & Accessories", details.get(ProductDetails.CATEGORY));
        assertEquals(null, details.get(ProductDetails.RANK));
        assertEquals(null, details.get(ProductDetails.DIMENSIONS));
    }
    
    // Google wifi - yet another different category/rank/dimensions selector
    @Test
    public void B01MAW2294() throws IOException {
        ScrapingProductDetailsLoader cut = createCut("B01MAW2294");
        Map<ProductDetails, String> details = cut.getAllDetails();

        assertEquals("Google WiFi system, 3-Pack - Router replacement for whole home coverage", details.get(ProductDetails.TITLE));
        assertEquals("Computers & Accessories", details.get(ProductDetails.CATEGORY));
        assertEquals("10", details.get(ProductDetails.RANK));
        assertEquals("4.2 x 4.2 x 2.7 inches", details.get(ProductDetails.DIMENSIONS));
    }

    // Canadian flag - helps to refine some selectors
    @Test
    public void B018TKJP0S() throws IOException {
        ScrapingProductDetailsLoader cut = createCut("B018TKJP0S");
        Map<ProductDetails, String> details = cut.getAllDetails();

        assertEquals("Anley Fly Breeze 3x5 Foot Canada Flag - Vivid Color and UV Fade Resistant - Canvas Header and Double Stitched - Canadian National Flags Polyester with Brass Grommets 3 X 5 Ft", details.get(ProductDetails.TITLE));
        assertEquals("Patio, Lawn & Garden", details.get(ProductDetails.CATEGORY));
        assertEquals("1,575", details.get(ProductDetails.RANK));
        assertEquals("8 x 6 x 0.5 inches", details.get(ProductDetails.DIMENSIONS));
    }
    
    // JungleScout t-shirt - SalesRank isn't in a table, no dimensions available
    @Test
    public void B01MSSJM77() throws IOException {
        ScrapingProductDetailsLoader cut = createCut("B01MSSJM77");
        Map<ProductDetails, String> details = cut.getAllDetails();
        System.out.println(details);

        assertEquals("Jungle Scout T-Shirt, 100% Organic Cotton, Crew Neck Tee, White", details.get(ProductDetails.TITLE));
        assertEquals("Clothing, Shoes & Jewelry", details.get(ProductDetails.CATEGORY));
        assertEquals("2,363,391", details.get(ProductDetails.RANK));
        assertEquals(null, details.get(ProductDetails.DIMENSIONS));
    }
}
