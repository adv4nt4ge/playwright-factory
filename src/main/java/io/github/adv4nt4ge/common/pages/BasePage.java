package io.github.adv4nt4ge.common.pages;

import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.Application;


public class BasePage {

    protected final Page page;
    protected Application application;

    public BasePage(Page page, Application application) {
        this.page = page;
        this.application = application;
    }

}
