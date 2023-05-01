package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Page;

import java.lang.reflect.Field;

/**
 * This class is a decorator that helps in locating elements on a page.
 */
public class LocatorFieldDecorator implements FieldDecorator {
    Page page;
    LocatorFactory locatorFactory;

    /**
     * Constructor for the LocatorFieldDecorator.
     * Initializes the page and locatorFactory with the given page.
     *
     * @param page The page to be used to create locators
     */
    public LocatorFieldDecorator(Page page) {
        this.page = page;
        this.locatorFactory = new LocatorFactory(page);
    }

    /**
     * This method creates a locator for a given field on a specific page object instance.
     *
     * @param field              The field for which to create a locator
     * @param pageObjectInstance The instance of the page object on which the field resides
     * @return The created locator for the specified field
     */
    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        return locatorFactory.createLocator(field, pageObjectInstance);
    }

}
