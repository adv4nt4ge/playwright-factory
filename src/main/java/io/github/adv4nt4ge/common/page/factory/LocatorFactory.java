package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import io.github.adv4nt4ge.common.page.factory.annotations.Frame;
import io.github.adv4nt4ge.common.page.factory.annotations.Parent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory class for creating Locator objects based on provided annotations.
 */
public class LocatorFactory {
    Page page;
    FindBy findBy;
    Parent parentAnnotation;
    Frame frameAnnotation;
    Class<?> clazz;

    /**
     * Constructs a LocatorFactory with the specified Page.
     *
     * @param page the Page object for which the LocatorFactory is to be created
     */
    public LocatorFactory(Page page) {
        this.page = page;
    }

    /**
     * Retrieves annotations from the provided Field and sets the corresponding class properties.
     *
     * @param field the Field from which to retrieve annotations
     */
    public void getAnnotation(Field field) {
        this.clazz = field.getDeclaringClass();
        this.findBy = field.getAnnotation(FindBy.class);
        this.parentAnnotation = field.getAnnotation(Parent.class);
        this.frameAnnotation = clazz.getAnnotation(Frame.class);
    }

    /**
     * Creates a Locator based on the provided Field and page object instance.
     *
     * @param field              the Field for which to create the Locator
     * @param pageObjectInstance the instance of the page object for which to create the Locator
     * @return the created Locator
     */
    public Locator createLocator(Field field, Object pageObjectInstance) {
        getAnnotation(field);

        if (frameAnnotation != null && parentAnnotation == null) {
            FrameLocator frameLocator = page.frameLocator(frameAnnotation.frame());
            return buildLocator(findBy, frameLocator::getByTestId, frameLocator::getByAltText,
                    frameLocator::getByLabel, frameLocator::getByPlaceholder, frameLocator::getByText,
                    frameLocator::getByTitle, frameLocator::locator);
        }

        if (parentAnnotation == null) {
            return buildLocator(findBy, page::getByTestId, page::getByAltText,
                    page::getByLabel, page::getByPlaceholder, page::getByText,
                    page::getByTitle, page::locator);
        }

        Locator parentLocator = getParentLocator(clazz, parentAnnotation, pageObjectInstance);
        return buildLocator(findBy, parentLocator::getByTestId, parentLocator::getByAltText,
                parentLocator::getByLabel, parentLocator::getByPlaceholder, parentLocator::getByText,
                parentLocator::getByTitle, parentLocator::locator);
    }

    /**
     * This method is responsible for creating a list of {@code ElementHandle} instances based on a specified field and
     * page object instance. The method first retrieves the annotation of the field. If there's no parent annotation,
     * it builds a locator using various getBy methods defined in the page class and performs a query selector to return all
     * elements matching the readable locator string.
     * <p>
     * If there's a parent annotation, it retrieves the parent locator, builds a locator using it, and performs a query
     * selector to return all elements matching the readable locator string.
     *
     * @param field              The field from which to create the locator list.
     * @param pageObjectInstance The object instance of the page where the elements will be located.
     * @return A list of {@code ElementHandle} instances representing the located elements on the page.
     */
    public List<ElementHandle> createLocatorList(Field field, Object pageObjectInstance) {
        Locator element = createLocator(field, pageObjectInstance);
        String readableLocator = getReadableLocator(element);
        return page.querySelectorAll(readableLocator);
    }

    /**
     * Retrieves the parent Locator of the specified Class and Under annotation from the provided page object instance.
     *
     * @param clazz              the Class for which to retrieve the parent Locator
     * @param parentAnnotation   the Under annotation for which to retrieve the parent Locator
     * @param pageObjectInstance the instance of the page object from which to retrieve the parent Locator
     * @return the parent Locator
     */
    protected Locator getParentLocator(Class<?> clazz, Parent parentAnnotation, Object pageObjectInstance) {
        Locator locator;
        try {
            Field depField = clazz.getField(parentAnnotation.value());
            locator = (Locator) depField.get(pageObjectInstance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getCause());
        }

        return locator;
    }

    /**
     * Builds a Locator based on the provided FindBy annotation and set of Functions.
     * <p> <a href="https://playwright.dev/java/docs/locators">Learn more about locators</a>.
     *
     * @param findBy           the FindBy annotation based on which to build the Locator
     * @param getByTestId      the Function to retrieve the Locator by locating an element based on its data-testid attribute.
     * @param getByAltText     the Function to retrieve the Locator by locating an element, usually an image, by its text alternative.
     * @param getByLabel       the Function to retrieve the Locator by locating a form control by associated label's text.
     * @param getByPlaceholder the Function to retrieve the Locator by locating an input by placeholder.
     * @param getByText        the Function to get the locator for searching by the content of the text.
     * @param getByTitle       the Function to retrieve the Locator by locating an element by its title attribute.
     * @param locator          the Function is used to create a locator that takes a selector that describes how to find an element on the page.
     *                         Playwright supports both CSS and XPath selectors.
     * @return the built Locator
     */
    protected Locator buildLocator(FindBy findBy, Function<String, Locator> getByTestId, Function<String, Locator> getByAltText,
                                   Function<String, Locator> getByLabel, Function<String, Locator> getByPlaceholder,
                                   Function<String, Locator> getByText, Function<String, Locator> getByTitle,
                                   Function<String, Locator> locator) {

        Map<String, Function<String, Locator>> findByMap = Map.of(
                "testId", getByTestId,
                "altText", getByAltText,
                "label", getByLabel,
                "placeholder", getByPlaceholder,
                "text", getByText,
                "title", getByTitle,
                "locator", locator);

        for (Map.Entry<String, Function<String, Locator>> entry : findByMap.entrySet()) {
            String value = getByReflection(findBy, entry.getKey());
            if (value != null && !"".equals(value)) {
                return entry.getValue().apply(value);
            }
        }
        return null;
    }

    /**
     * Retrieves a value from the provided FindBy annotation using the specified method name.
     *
     * @param findBy the FindBy annotation from which
     *               to retrieve the value
     * @param method the name of the method to use to retrieve the value
     * @return the retrieved value
     */
    private String getByReflection(FindBy findBy, String method) {
        try {
            return (String) findBy.getClass().getMethod(method).invoke(findBy);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * This method transforms a given Playwright Locator object into a readable String format.
     *
     * @param locator the Locator object to be transformed.
     * @return a String representation of the locator, excluding the "Locator@" part from the toString representation.
     * If the locator is null, it will return "Locator@".
     */
    private String getReadableLocator(Locator locator) {
        return locator.toString().replace("Locator@", "");
    }
}
