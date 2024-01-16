package by.veremei.api.tests;

import by.veremei.config.ConfigReader;
import by.veremei.config.ProjectConfiguration;
import by.veremei.config.web.WebConfig;
import by.veremei.helpers.Attach;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    private static final WebConfig webConfig = ConfigReader.Instance.read();

    @BeforeAll
    public static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        ProjectConfiguration projectConfiguration = new ProjectConfiguration(webConfig);
        projectConfiguration.webConfig();
    }

    @AfterEach
    void addAttachment() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();
        Selenide.closeWebDriver();
    }
}