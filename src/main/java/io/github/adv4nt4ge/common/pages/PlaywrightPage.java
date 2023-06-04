package io.github.adv4nt4ge.common.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.Application;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import org.junit.jupiter.api.Assertions;

import java.util.List;


public class PlaywrightPage extends BasePage {
    @FindBy(locator = ".getStarted_Sjon")
    public List<ElementHandle> getStartedBtn;

    @FindBy(locator = ".gh-btn")
    public Locator githubLinkBtn;

    public PlaywrightPage(Page page, Application application) {
        super(page, application);
    }

    public PlaywrightPage checkIsVisibleStartedButtons() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(getStartedBtn.get(0).isVisible()),
                () -> Assertions.assertTrue(githubLinkBtn.isVisible())
        );
        return this;
    }

    public PlaywrightDocsPage clickOnGitHubLinkButton() {
        getStartedBtn.get(0).click();
        page.waitForURL("**/java/docs/intro");
        return application.getPages(PlaywrightDocsPage.class);
    }
}
