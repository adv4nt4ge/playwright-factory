package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import io.github.adv4nt4ge.common.page.factory.annotations.Frame;
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
        Frame frameAnnotation = clazz.getAnnotation(Frame.class);

        if (frameAnnotation != null && underAnnotation == null) {
            FrameLocator frameLocator = page.frameLocator(frameAnnotation.frame());
            return buildFrameChildLocator(findBy, frameLocator);
        }

        if (underAnnotation == null) {
            return buildLocatorFromFindBy(findBy);
        }

        Locator parentLocator = getParentLocator(clazz, underAnnotation, pageObjectInstance);
        return buildParentChildLocator(findBy, parentLocator);

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

    protected Locator buildParentChildLocator(FindBy findBy, Locator parentLocator) {
        if (!"".equals(findBy.testId())) {
            return parentLocator.getByTestId(findBy.testId());
        }

        if (!"".equals(findBy.altText())) {
            return parentLocator.getByAltText(findBy.altText());
        }

        if (!"".equals(findBy.label())) {
            return parentLocator.getByLabel(findBy.label());
        }

        if (!"".equals(findBy.placeholder())) {
            return parentLocator.getByPlaceholder(findBy.placeholder());
        }

        if (!"".equals(findBy.text())) {
            return parentLocator.getByText(findBy.text());
        }

        if (!"".equals(findBy.title())) {
            return parentLocator.getByTitle(findBy.title());
        }

        if (!"".equals(findBy.locator())) {
            return parentLocator.locator(findBy.locator());
        }

        // Fall through
        return null;
    }

    protected Locator buildFrameChildLocator(FindBy findBy, FrameLocator frameLocator) {
        if (!"".equals(findBy.testId())) {
            return frameLocator.getByTestId(findBy.testId());
        }

        if (!"".equals(findBy.altText())) {
            return frameLocator.getByAltText(findBy.altText());
        }

        if (!"".equals(findBy.label())) {
            return frameLocator.getByLabel(findBy.label());
        }

        if (!"".equals(findBy.placeholder())) {
            return frameLocator.getByPlaceholder(findBy.placeholder());
        }

        if (!"".equals(findBy.text())) {
            return frameLocator.getByText(findBy.text());
        }

        if (!"".equals(findBy.title())) {
            return frameLocator.getByTitle(findBy.title());
        }

        if (!"".equals(findBy.locator())) {
            return frameLocator.locator(findBy.locator());
        }

        // Fall through
        return null;
    }
}
