package io.github.adv4nt4ge.common.page.factory;

import com.microsoft.playwright.Page;
import io.github.adv4nt4ge.common.page.factory.annotations.FindBy;
import io.github.adv4nt4ge.common.page.factory.annotations.Parent;
import io.github.adv4nt4ge.common.page.factory.exeptions.InvalidParentLocatorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to instantiate and initialize the Page Objects.
 */
public class PageFactory {
    /**
     * Creates an instance of the specified class and initializes its fields.
     *
     * @param <T>          the type of the page object
     * @param pageToCreate the class to instantiate
     * @param page         the Page object to use in the constructor
     * @return an instance of the class
     */
    public static <T> T create(Class<T> pageToCreate, Page page) {
        return instantiatePage(pageToCreate, page, new LocatorFieldDecorator(page));
    }

    /**
     * Creates an instance of the specified class and initializes its fields.
     *
     * @param <T>          the type of the page object
     * @param pageToCreate the class to instantiate
     * @param page         the Page object to use in the constructor
     * @param decorator    the FieldDecorator to use
     * @return an instance of the class
     */
    public static <T> T create(Class<T> pageToCreate, Page page, FieldDecorator decorator) {
        return instantiatePage(pageToCreate, page, decorator);
    }

    /**
     * Initializes fields in the page object.
     *
     * @param pageObject the object whose fields to initialize
     * @param page       the Page object to use in the constructor
     */
    public static void initElements(Object pageObject, Page page) {
        initElements(pageObject, new LocatorFieldDecorator(page));
    }

    /**
     * Initializes fields in the page object.
     *
     * @param pageObject the object whose fields to initialize
     * @param decorator  the FieldDecorator to use
     */
    public static void initElements(Object pageObject, FieldDecorator decorator) {
        initElements(decorator, pageObject);
    }

    /**
     * Initializes elements of the page object instance using a field decorator.
     *
     * @param decorator          the decorator to use for field initialization
     * @param pageObjectInstance the instance of the page object to initialize
     */
    private static void initElements(FieldDecorator decorator, Object pageObjectInstance) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> pageObjectClass = pageObjectInstance.getClass();

        while (pageObjectClass != Object.class) {
            classes.add(pageObjectClass);
            pageObjectClass = pageObjectClass.getSuperclass();
        }

        // Initialize in reverse order so base classes get initialized first
        // so its fields are available to child classes
        Collections.reverse(classes);
        proxyFields(decorator, pageObjectInstance, classes);
    }

    /**
     * Instantiates a page object class and initializes its fields.
     *
     * @param <T>             the type of the page object
     * @param pageObjectClass the class to instantiate
     * @param page            the Page object to use in the constructor
     * @param decorator       the FieldDecorator to use
     * @return an instance of the page object class
     */
    private static <T> T instantiatePage(Class<T> pageObjectClass, Page page, FieldDecorator decorator) {
        try {
            T pageObjectInstance;
            try {
                Constructor<T> constructor = pageObjectClass.getConstructor(Page.class);
                pageObjectInstance = constructor.newInstance(page);
            } catch (NoSuchMethodException e) {
                pageObjectInstance = pageObjectClass.getDeclaredConstructor().newInstance();
            }
            initElements(decorator, pageObjectInstance);
            return pageObjectInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Proxies fields in the given list of classes for the specified page object instance.
     *
     * @param decorator          the decorator to use for field initialization
     * @param pageObjectInstance the instance of the page object to proxy fields for
     * @param classes            the list of classes to proxy fields for
     */
    private static void proxyFields(FieldDecorator decorator, Object pageObjectInstance, List<Class<?>> classes) {
        HashSet<String> fieldNamesAlreadyProxied = new HashSet<>();
        List<Field> fieldsWithDependencies = new ArrayList<>();

        for (Class<?> pageObjectClass : classes) {
            Field[] fields = pageObjectClass.getDeclaredFields();

            for (Field field : fields) {
                if (isALocator(field)) {
                    if (hasDependencies(field)) {
                        fieldsWithDependencies.add(field);
                        continue;
                    }
                    setField(decorator, field, pageObjectInstance);
                    fieldNamesAlreadyProxied.add(field.getName());
                }
            }

            int sizeBefore;
            while (!fieldsWithDependencies.isEmpty()) {
                sizeBefore = fieldsWithDependencies.size();
                List<Field> proxiedScopedFields = new ArrayList<>();

                for (Field field : fieldsWithDependencies) {
                    List<String> dependencyNames = Collections.singletonList(field.getAnnotation(Parent.class).value());

                    if (fieldNamesAlreadyProxied.containsAll(dependencyNames)) {
                        setField(decorator, field, pageObjectInstance);
                        fieldNamesAlreadyProxied.add(field.getName());
                        proxiedScopedFields.add(field);
                    }
                }

                for (Field proxied : proxiedScopedFields) {
                    fieldsWithDependencies.remove(proxied);
                }

                if (sizeBefore == fieldsWithDependencies.size()) {
                    String fieldNames = fieldsWithDependencies.stream().map(Field::getName).collect(Collectors.joining(", "));
                    String message = String.format(
                            "\nUnable to find dependencies for the following Fields:\nPage Object: %s\nFields: %s",
                            pageObjectClass.getName(), fieldNames);

                    throw new InvalidParentLocatorException(message);
                }
            }
        }
    }

    /**
     * Checks if a field has dependencies.
     *
     * @param field the field to check
     * @return true if the field has dependencies, false otherwise
     */
    private static boolean hasDependencies(Field field) {
        return field.isAnnotationPresent(Parent.class);
    }

    /**
     * Checks if a field is a locator.
     *
     * @param field the field to check
     * @return true if the field is a locator, false otherwise
     */
    private static boolean isALocator(Field field) {
        return field.isAnnotationPresent(FindBy.class);
    }

    /**
     * Sets the field with the given decorator.
     *
     * @param decorator          the decorator to use
     * @param field              the field to set
     * @param pageObjectInstance the instance of the page object
     */
    private static void setField(FieldDecorator decorator, Field field, Object pageObjectInstance) {
        Object value = decorator.decorate(field, pageObjectInstance);
        if (value != null) {
            try {
                field.setAccessible(true);
                field.set(pageObjectInstance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
