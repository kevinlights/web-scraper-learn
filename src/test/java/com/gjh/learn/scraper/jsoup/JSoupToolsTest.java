package com.gjh.learn.scraper.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class JSoupToolsTest {
    private static final Logger LOG = LoggerFactory.getLogger(JSoupToolsTest.class);

    JSoupTools jSoupTools;

    @Before
    public void setUp() {
        jSoupTools = new JSoupTools();
    }

    @Test
    public void parseHtml() {
        String html = "<html><head><title>First parse</title></head><body><p>Parsed HTML into a doc.</p></body></html>";
        Document doc = jSoupTools.parseHtml(html);
        Element body = doc.body();

    }

    @Test
    public void parseDouban() throws IOException {
        String url = "https://movie.douban.com/top250";
        Document doc = jSoupTools.parse(url, 1000000);
        Element gridView = jSoupTools.getFirstElementByTagClass(doc, "ol.grid_view");
        Elements lis = gridView.getElementsByTag("li");
        Iterator<Element> container = lis.iterator();
        while (container.hasNext()) {
            Element item = container.next();
            String name = jSoupTools.getFirstElementByTagClass(item, "span.title").text();
            String sortNo = item.getElementsByTag("em").text();
            String score = jSoupTools.getFirstElementByTagClass(item, "span.rating_num").text();
            String comment = jSoupTools.getFirstElementByTagClass(item, "span.inq").text();
            LOG.info("name = {}\tsortNo = {}\tscore = {}\tcomment = {}", name, sortNo, score, comment);
        }
    }

    @Test
    public void parseGitHubPending() throws IOException {
        String url = "https://github.com/trending?since=weekly";
        Document doc = jSoupTools.parse(url, 1000000);
        Elements list = doc.select("article.Box-row");
        Iterator<Element> iterator = list.iterator();
        while (iterator.hasNext()) {
            Element row = iterator.next();
            String repoName = row.select("h1.h3 a").text();
            String repoDesc = row.select("p.col-9") != null ? row.select("p.col-9").text() : "";
            String repoLang = row.select("span.d-inline-block span:nth-of-type(2)").text();
            String repoStars = row.select("a.muted-link:nth-of-type(1)").text();
            String repoForks = row.select("a.muted-link:nth-of-type(2)").text();
            String repoUrl = row.select("h1.h3 a").attr("href");
            String repoNewStars = row.select("span.d-inline-block.float-sm-right").text();
            LOG.info("repoName={}\trepoDesc={}\trepoLang={}\trepoStars={}\trepoForks={}\trepoUrl={}\trepoNewStars={}", repoName, repoDesc, repoLang, repoStars, repoForks, repoUrl, repoNewStars);
        }

    }
}