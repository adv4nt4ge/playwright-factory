package io.github.adv4nt4ge.common.page.factory;

import java.lang.reflect.Field;

public interface FieldDecorator {
    Object decorate(Field field, Object pageObjectInstance);
}
