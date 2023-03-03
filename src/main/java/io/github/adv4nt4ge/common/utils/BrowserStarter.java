package io.github.adv4nt4ge.common.utils;

import com.microsoft.playwright.*;
import lombok.SneakyThrows;

public class BrowserStarter {

    private static final ThreadLocal<Page> PAGE_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> BROWSER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static Playwright getPlaywright() {
        return PLAYWRIGHT_THREAD_LOCAL.get();
    }

    public static Browser getBrowser() {
        return BROWSER_THREAD_LOCAL.get();
    }

    public static BrowserContext getBrowserContext() {
        return BROWSER_CONTEXT_THREAD_LOCAL.get();
    }

    public static Page getPage() {
        return PAGE_THREAD_LOCAL.get();
    }

    public void initPage() {
        PLAYWRIGHT_THREAD_LOCAL.set(Playwright.create());
        BrowserType chromium = createSession(2);
        BROWSER_THREAD_LOCAL.set(chromium.launch(new BrowserType.LaunchOptions()
                .setHeadless(true)));
        BROWSER_CONTEXT_THREAD_LOCAL.set(getBrowser().newContext(new Browser.NewContextOptions().setIgnoreHTTPSErrors(true)
                .setViewportSize(1920, 1080)));
        PAGE_THREAD_LOCAL.set(getBrowserContext().newPage());
        String url = "https://playwright.dev/java/";
        getPage().navigate(url).finished();
    }

    @SneakyThrows
    public static BrowserType createSession(int attempts) {
        for (int i = 0; i < attempts; i++) {
            try {
                return getPlaywright().chromium();
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }
        throw new RuntimeException("The session hasn't been created after " + attempts + " attempts");
    }
}
