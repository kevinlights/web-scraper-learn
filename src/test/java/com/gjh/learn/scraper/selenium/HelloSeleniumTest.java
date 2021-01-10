package com.gjh.learn.scraper.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class HelloSeleniumTest {
    private static final Logger LOG = LoggerFactory.getLogger(HelloSeleniumTest.class);

    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 5);
    }

    @Test
    public void getGitHubPending() {
        driver.get("https://github.com/trending?since=weekly");
        List<WebElement> article = driver.findElements(By.cssSelector("article.Box-row"));
        if (null == article) {
            return;
        }
        for (WebElement item : article) {
            String repoName = item.findElement(By.cssSelector("h1.h3 a")).getText();
            String repoDesc = findElement(item, "p.col-9") != null ? findElement(item, "p.col-9").getText() : "";
            String repoLang = item.findElement(By.cssSelector("span.d-inline-block span:nth-of-type(2)")).getText();
            String repoStars = item.findElement(By.cssSelector("a.muted-link:nth-of-type(1)")).getText();
            String repoForks = item.findElement(By.cssSelector("a.muted-link:nth-of-type(2)")).getText();
            String repoUrl = item.findElement(By.cssSelector("h1.h3 a")).getAttribute("href");
            LOG.info("repoName={}\trepoDesc={}\trepoLang={}\trepoStars={}\trepoForks={}\trepoUrl={}", repoName, repoDesc, repoLang, repoStars, repoForks, repoUrl);
        }
        driver.close();
    }

    public WebElement findElement(WebElement item, String cssSelector) {
        WebElement el = null;
        try {
            el = item.findElement(By.cssSelector(cssSelector));
        } catch (NoSuchElementException e) {
            LOG.error("No such element, css selector = ", cssSelector);
        }
        return el;
    }

    @Test
    public void testBaidu() {
        // driver.get("https://www.baidu.com/");
        driver.navigate().to("https://www.baidu.com/");
        LOG.info(driver.getCurrentUrl());
        driver.navigate().back();
        driver.navigate().forward();
        driver.navigate().refresh();

        LOG.info(driver.getTitle());

        String originalWindow = driver.getWindowHandle();
        LOG.info("originalWindow={}", originalWindow);
        LOG.info("window size = {}", driver.getWindowHandles().size());

        driver.findElement(By.linkText("新闻")).click();

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                LOG.info("switch to new window");
                break;
            }
        }
        driver.close();
        LOG.info("close current window");
        driver.switchTo().window(originalWindow);
        LOG.info("switch to origin window");

    }

    @After
    public void tearDown() {
        driver.quit();
        LOG.info("quit browser");
    }
}