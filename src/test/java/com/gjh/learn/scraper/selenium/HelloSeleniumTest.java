package com.gjh.learn.scraper.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HelloSeleniumTest {
    private static final Logger LOG = LoggerFactory.getLogger(HelloSeleniumTest.class);

    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 5);
        LOG.info("set up driver and wait");
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

    @Test
    public void testIframe() {
        driver.navigate().to("http://127.0.0.1:8080/iframe.html");
        // WebElement iframe = driver.findElement(By.cssSelector("#modal>iframe"));
        // driver.switchTo().frame(iframe);

        // driver.switchTo().frame("buttonframe");
        // driver.switchTo().frame("myframe");
        driver.switchTo().frame(0);
        driver.findElement(By.tagName("button")).click();

        driver.switchTo().defaultContent();
    }

    @Test
    public void testWindow() throws InterruptedException {
        driver.manage().window().setSize(new Dimension(1024, 768));
        LOG.info("width={}", driver.manage().window().getSize().getWidth());
        LOG.info("height={}", driver.manage().window().getSize().getHeight());

        driver.manage().window().setPosition(new Point(0, 0));
        Point position = driver.manage().window().getPosition();
        LOG.info("x={}", position.getX());
        LOG.info("y={}", position.getY());

        driver.manage().window().maximize();

        driver.manage().window().fullscreen();


    }

    @Test
    public void testScreenshot() throws IOException {
        driver.navigate().to("https://www.baidu.com/");
        driver.manage().window().maximize();
        String screenshotBase64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        LOG.info("screenshot={}", screenshotBase64);

        WebElement hot = driver.findElement(By.cssSelector("div.s_form_wrapper"));
        File sc = hot.getScreenshotAs(OutputType.FILE);
        FileCopyUtils.copy(sc, new File("./build/tmp/baidu_hot.png"));
    }

    @Test
    public void testScript() {
        driver.navigate().to("https://movie.douban.com/top250");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement button = driver.findElement(By.cssSelector("div.inp-btn input"));
        js.executeScript("arguments[0].click();", button);
        // js.executeScript("return arguments[0].innerText", button);
        js.executeScript("console.log('hello world')");
    }

    @Test
    public void testWait() {
        driver.navigate().to("http://127.0.0.1:8080/wait.html");
        WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a/h3")));

        WebElement foo = wait.until(d -> d.findElement(By.name("q")));

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);
        fluentWait.until(webDriver -> webDriver.findElement(By.id("foo")));
    }

    @After
    public void tearDown() {
        driver.quit();
        LOG.info("quit browser");
    }
}