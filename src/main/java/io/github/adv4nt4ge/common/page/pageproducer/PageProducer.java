package io.github.adv4nt4ge.common.page.pageproducer;


import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.Application;
import io.github.adv4nt4ge.common.page.BasePage;
import io.github.adv4nt4ge.common.page.factory.ElementFieldDecorator;
import io.github.adv4nt4ge.common.page.factory.PageFactory;
import lombok.SneakyThrows;

import static io.github.adv4nt4ge.common.utils.BrowserStarter.getPage;

public class PageProducer {

    protected Application application;

    public PageProducer(Application application) {
        this.application = application;
    }

    public <T> T initPageElements(T page) {
        PageFactory.initElements(page, new ElementFieldDecorator(getPage()));
        return page;
    }

    @SneakyThrows
    public <T extends BasePage> T initPages(Class<? extends BasePage> pageClazz) {
        var page = pageClazz.getDeclaredConstructor(Page.class, Application.class);
        var pageInstance = page.newInstance(getPage(), application);
        return (T) initPageElements(pageInstance);
    }
}