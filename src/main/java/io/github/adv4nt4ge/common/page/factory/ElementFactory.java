package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Factory class for creating Element objects based on provided field type.
 */
public class ElementFactory {
    private final LocatorFactory locatorFactory;

    /**
     * Constructs an ElementFactory with the specified Page.
     *
     * @param page the Page object for which the ElementFactory is to be created
     */
    public ElementFactory(Page page) {
        this.locatorFactory = new LocatorFactory(page);
    }

    /**
     * Creates an Element based on the provided Field and page object instance.
     * If the type of the Field is Locator, a Locator is created.
     * If the type of the Field is List, a list of Locators is created.
     *
     * @param field the Field for which to create the Element
     * @param pageObjectInstance the instance of the page object for which to create the Element
     * @return the created Element
     * @throws RuntimeException if the type of the Field is not supported
     */
    public Object createElement(Field field, Object pageObjectInstance) {
        if (Locator.class.equals(field.getType())) {
            return locatorFactory.createLocator(field, pageObjectInstance);
        } else if (List.class.equals(field.getType())) {
            return locatorFactory.createLocatorList(field, pageObjectInstance);
        }
        throw new RuntimeException("Unsupported type: " + field.getType() + ". Supported types are Locator and List<Locator>.");
    }
}
