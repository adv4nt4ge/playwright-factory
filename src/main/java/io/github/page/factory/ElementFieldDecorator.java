package io.github.page.factory;

import com.microsoft.playwright.Page;

import java.lang.reflect.Field;

public class ElementFieldDecorator implements FieldDecorator {
    private final ElementFactory elementFactory;

    public ElementFieldDecorator(Page page) {
        this.elementFactory = new ElementFactory(page);
    }

    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        return elementFactory.createElement(field, pageObjectInstance);
    }
}
