package io.github.adv4nt4ge.common;


import io.github.adv4nt4ge.common.utils.BrowserStarter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Application application;

    @BeforeEach
    public void setUp() {
        application = new Application();
        new BrowserStarter().initPage();
    }

    @AfterEach
    public void tearDown() {
        BrowserStarter.getPlaywright().close();
    }
}
