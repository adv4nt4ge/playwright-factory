package io.github.adv4nt4ge.common.page.factory;

import java.lang.reflect.Field;

/**
 * This interface defines a method for decorating fields in a class.
 */
public interface FieldDecorator {
    /**
     * This method decorates a field on a specific page object instance.
     *
     * @param field              The field to decorate
     * @param pageObjectInstance The instance of the page object on which the field resides
     * @return The decorated object for the specified field
     */
    Object decorate(Field field, Object pageObjectInstance);
}
