package io.github.page.factory;

import com.microsoft.playwright.Page;
import io.github.page.factory.annotations.FindBy;
import io.github.page.factory.annotations.Under;
import io.github.page.factory.exeptions.InvalidParentLocatorException;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PageFactory {
    public static <T> T create(Class<T> pageToCreate, Page page) {
        return instantiatePage(pageToCreate, page, new LocatorFieldDecorator(page));
    }

    public static <T> T create(Class<T> pageToCreate, Page page, FieldDecorator decorator) {
        return instantiatePage(pageToCreate, page, decorator);
    }

    public static void initElements(Object pageObject, Page page) {
        initElements(pageObject, new LocatorFieldDecorator(page));
    }

    public static void initElements(Object pageObject, FieldDecorator decorator) {
        initElements(decorator, pageObject);
    }

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

    private static void proxyFields(FieldDecorator decorator, Object pageObjectInstance, List<Class<?>> classes) {
        List<String> fieldNamesAlreadyProxied = new ArrayList<>();
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
                    List<String> dependencyNames = Collections.singletonList(field.getAnnotation(Under.class).value());

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

    private static boolean hasDependencies(Field field) {
        return field.isAnnotationPresent(Under.class);
    }

    private static boolean isALocator(Field field) {
        return field.isAnnotationPresent(FindBy.class);
    }

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
