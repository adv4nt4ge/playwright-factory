package io.github.adv4nt4ge.common;


import io.github.adv4nt4ge.common.pages.BasePage;
import io.github.adv4nt4ge.common.page.pageproducer.PageProducer;

public class Application {
    protected PageProducer pageProducer;

    public Application() {
        pageProducer = createPageProducer();
    }

    protected PageProducer createPageProducer() {
        return new PageProducer(this);
    }

    public <T extends BasePage> T getPages(Class<? extends BasePage> page) {
        return pageProducer.initPages(page);
    }
}