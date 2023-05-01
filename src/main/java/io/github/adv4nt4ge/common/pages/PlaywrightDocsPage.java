package io.github.adv4nt4ge.common.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.Application;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import org.junit.jupiter.api.Assertions;


public class PlaywrightDocsPage extends BasePage {

    @FindBy(locator = ".navbar__brand")
    public Locator navigationLogo;

    public PlaywrightDocsPage(Page page, Application application) {
        super(page, application);
    }

    public void checkIsVisibleStartedButtons() {
        Assertions.assertTrue(navigationLogo.isVisible());
    }
}
