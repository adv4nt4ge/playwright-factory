package io.github.adv4nt4ge.common.tests;

import io.github.adv4nt4ge.common.BaseTest;
import io.github.adv4nt4ge.common.pages.PlaywrightPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PageFactoryTests extends BaseTest {
    PlaywrightPage playwrightPage;

    @BeforeEach
    public void setupPreconditions() {
        playwrightPage = application.getPages(PlaywrightPage.class);
    }

    @Test
    public void checkStartedButtonsTest() {
        playwrightPage.checkIsVisibleStartedButtons()
                .clickOnGitHubLinkButton()
                .checkIsVisibleStartedButtons();
    }
}
