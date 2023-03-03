package io.github.adv4nt4ge.common.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.Application;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import org.junit.jupiter.api.Assertions;


public class PlaywrightPage extends BasePage {
    @FindBy(locator = ".getStarted_Sjon")
    public Locator getStartedBtn;

    @FindBy(locator = ".gh-btn")
    public Locator githubLinkBtn;

    public PlaywrightPage(Page page, Application application) {
        super(page, application);
    }

    public PlaywrightPage checkIsVisibleStartedButtons() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(getStartedBtn.isVisible()),
                () -> Assertions.assertTrue(githubLinkBtn.isVisible())
        );
        return this;
    }

    public PlaywrightDocsPage clickOnGitHubLinkButton() {
        getStartedBtn.click();
        page.waitForURL("**/java/docs/intro");
        return application.getPages(PlaywrightDocsPage.class);
    }
}
