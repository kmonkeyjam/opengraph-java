package opengraph;

import java.io.IOException;
import java.util.Hashtable;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import java.net.URL;

/**
 * A Java object representation of an Open Graph enabled webpage
 * @author Callum Jones
 */
public class OpenGraph {
    private String pageUrl;
    private Hashtable<String, String> metaAttributes;
    private String baseType;

    public final static String[] REQUIRED_META = new String[]{"title", "type", "image", "url" };
    
    public final static Hashtable<String, String[]> BASE_TYPES = new Hashtable<String, String[]>();
       static {
		BASE_TYPES.put("activity", new String[] {"activity", "sport"});
		BASE_TYPES.put("business", new String[] {"bar", "company", "cafe", "hotel", "restaurant"});
		BASE_TYPES.put("group", new String[] {"cause", "sports_league", "sports_team"});
                BASE_TYPES.put("organization", new String[] {"band", "government", "non_profit", "school", "university"});
                BASE_TYPES.put("person", new String[] {"actor", "athlete", "author", "director", "musician", "politician", "public_figure"});
                BASE_TYPES.put("place", new String[] {"city", "landmark", "state_province"});
                BASE_TYPES.put("product", new String[] {"album", "book", "drink", "food", "game", "movie", "product", "song", "tv_show"});
                BASE_TYPES.put("website", new String[] {"blog", "website"});
	}

    /**
     * Fetch the open graph representation from a web site
     * @param url The address to the web page to fetch Open Graph data
     * @throws java.io.IOException If a network error occurs, the HTML parser will throw an IO Exception
     * @throws java.lang.Exception A generic exception is throw if the specific page fails to conform to the basic Open Graph standard as define by the constant REQUIRED_META
     */
    public OpenGraph(String url) throws java.io.IOException, Exception {
        //init the attribute storage
        metaAttributes = new Hashtable<String, String>();
        pageUrl = url;
        //download the (X)HTML content
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode pageData = cleaner.clean(new URL(url));
        //open only the meta tags
        TagNode[] metaData = pageData.getElementsByName("meta", true);
        for (TagNode metaElement : metaData) {
            if (metaElement.hasAttribute("property"))
                if (metaElement.getAttributeByName("property").startsWith("og:"))
                    metaAttributes.put(metaElement.getAttributeByName("property").replaceFirst("og:", ""), metaElement.getAttributeByName("content"));    
        }

        /**
         * Check that page conforms to Open Graph protocol
         */
        for (String req : REQUIRED_META) {
            if (!metaAttributes.containsKey(req))
                throw new Exception("Does not conform to Open Graph protocol");
        }

        /**
         * Has conformed, now get basic sub type.
         */
        baseType = null;
        for (String base : BASE_TYPES.keySet()) {
            String[] baseList = BASE_TYPES.get(base);
            boolean finished = false;
            for (String expandedType : baseList) {
                if (expandedType.equals(metaAttributes.get("type"))) {
                    baseType = base;
                    finished = true;
                    break;
                }
            }
            if (finished) break;
        }


    }

    /**
     * Get the basic type of the Open graph page as per the specification
     * @return Base type is defined by specification, null otherwise
     */
    public String getBaseType() {
        return baseType;
    }

    /**
     * Get a value of a given Open Graph property
     * @param property The Open graph property key
     * @return Returns the value of the property if defined, null otherwise
     */
    public String getMeta(String property) {
        return metaAttributes.get(property);
    }

    /**
     * Get the original URL the Open Graph page was obtained from
     * @return The address to the Open Graph object page
     */
    public String getOriginalUrl() {
        return pageUrl;
    }
}