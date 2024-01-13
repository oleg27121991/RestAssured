package by.veremei.config;

import by.veremei.config.web.Browser;
import by.veremei.config.web.WebConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Objects;
import java.util.function.Supplier;

public class WebDriverProvider implements Supplier<WebDriver> {
    private final WebConfig webConfig = ConfigReader.Instance.read();

    @Override
    public WebDriver get() {
        WebDriver driver = createWebDriver();
        driver.get(webConfig.baseUrl());
        return driver;
    }

    private WebDriver createWebDriver() {
        if (Objects.isNull(webConfig.remoteUrl())) {
            if (webConfig.browser().equals(Browser.CHROME.toString())) {
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            } else if (webConfig.browser().equals(Browser.FIREFOX.toString())) {
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            }
        } else {
            if (webConfig.browser().equals(Browser.CHROME.toString())) {
                return new RemoteWebDriver(webConfig.remoteUrl(), new ChromeOptions());
            } else if (webConfig.browser().equals(Browser.FIREFOX.toString())) {
                return new RemoteWebDriver(webConfig.remoteUrl(), new FirefoxOptions());
            }
        }
        throw new RuntimeException("No such browser");
    }
}
