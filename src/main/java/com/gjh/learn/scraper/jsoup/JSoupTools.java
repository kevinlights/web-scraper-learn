package com.gjh.learn.scraper.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

public class JSoupTools {
    public Document parseHtml(String html) {
        return Jsoup.parse(html);
    }

    public Document parse(String url, int timeoutMillis) throws IOException {
        return Jsoup.parse(new URL(url), timeoutMillis);
    }

    /**
     * return first element with pattern
     * @param item element item
     * @param pattern tag.class "span.title"
     */
    public Element getFirstElementByTagClass(Element item, String pattern) {
        String[] patterns = pattern.split("\\.");
        if (patterns.length != 2) {
            return null;
        }
        return item.getElementsByTag(patterns[0]).stream().filter(e -> e.hasClass(patterns[1])).findFirst().get();
    }
}
