package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Page;

import java.lang.reflect.Field;

/**
 * This class implements the FieldDecorator interface to decorate fields as elements.
 */
public class ElementFieldDecorator implements FieldDecorator {
    private final ElementFactory elementFactory;

    /**
     * Constructor for the ElementFieldDecorator.
     * Initializes the elementFactory with the given page.
     *
     * @param page The page to be used to create elements
     */
    public ElementFieldDecorator(Page page) {
        this.elementFactory = new ElementFactory(page);
    }

    /**
     * This method creates an element for a given field on a specific page object instance.
     *
     * @param field              The field for which to create an element
     * @param pageObjectInstance The instance of the page object on which the field resides
     * @return The created element for the specified field
     */
    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        return elementFactory.createElement(field, pageObjectInstance);
    }
}
