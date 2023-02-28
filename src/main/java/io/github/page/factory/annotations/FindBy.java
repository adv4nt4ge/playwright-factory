package io.github.page.factory.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindBy {
    String testId() default "";

    String altText() default "";

    String label() default "";

    String placeholder() default "";

    String text() default "";

    String title() default "";

    String locator() default "";
}
