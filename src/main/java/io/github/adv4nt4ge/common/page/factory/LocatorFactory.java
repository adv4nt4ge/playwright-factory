package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import io.github.adv4nt4ge.common.page.factory.annotations.Under;

import java.lang.reflect.Field;

public class LocatorFactory {
    Page page;

    public LocatorFactory(Page page) {
        this.page = page;
    }

    public Locator createLocator(Field field, Object pageObjectInstance) {
        Class<?> clazz = field.getDeclaringClass();
        FindBy findBy = field.getAnnotation(FindBy.class);
        Under underAnnotation = field.getAnnotation(Under.class);

        if (underAnnotation == null) {
            return buildLocatorFromFindBy(findBy);
        }

        Locator locator = getParentLocator(clazz, underAnnotation, pageObjectInstance);
        return buildParentChildLocator(findBy, locator);

    }

    protected Locator getParentLocator(Class<?> clazz, Under underAnnotation, Object pageObjectInstance) {
        Locator locator;
        try {
            Field depField = clazz.getField(underAnnotation.value());
            locator = (Locator) depField.get(pageObjectInstance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getCause());
        }

        return locator;
    }

    protected Locator buildLocatorFromFindBy(FindBy findBy) {
        if (!"".equals(findBy.testId())) {
            return page.getByTestId(findBy.testId());
        }

        if (!"".equals(findBy.altText())) {
            return page.getByAltText(findBy.altText());
        }

        if (!"".equals(findBy.label())) {
            return page.getByLabel(findBy.label());
        }

        if (!"".equals(findBy.placeholder())) {
            return page.getByPlaceholder(findBy.placeholder());
        }

        if (!"".equals(findBy.text())) {
            return page.getByText(findBy.text());
        }

        if (!"".equals(findBy.title())) {
            return page.getByTitle(findBy.title());
        }

        if (!"".equals(findBy.locator())) {
            return page.locator(findBy.locator());
        }

        // Fall through
        return null;
    }

    protected Locator buildParentChildLocator(FindBy findBy, Locator locator) {
        if (!"".equals(findBy.testId())) {
            return locator.getByTestId(findBy.testId());
        }

        if (!"".equals(findBy.altText())) {
            return locator.getByAltText(findBy.altText());
        }

        if (!"".equals(findBy.label())) {
            return locator.getByLabel(findBy.label());
        }

        if (!"".equals(findBy.placeholder())) {
            return locator.getByPlaceholder(findBy.placeholder());
        }

        if (!"".equals(findBy.text())) {
            return locator.getByText(findBy.text());
        }

        if (!"".equals(findBy.title())) {
            return locator.getByTitle(findBy.title());
        }

        if (!"".equals(findBy.locator())) {
            return locator.locator(findBy.locator());
        }

        // Fall through
        return null;
    }
}
