package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.lang.reflect.Field;

public class ElementFactory {
    private final LocatorFactory locatorFactory;

    public ElementFactory(Page page) {
        this.locatorFactory = new LocatorFactory(page);
    }

    public Object createElement(Field field, Object pageObjectInstance) {
        Locator locator = locatorFactory.createLocator(field, pageObjectInstance);

        if (Locator.class.equals(field.getType())) {
            return locator;
        }
        throw new RuntimeException("Unknown element");

    }
}
