package dev.anthhyo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    String name() default "none";

    String description() default "none";

    boolean databaseOpen() default false;

    boolean databaseTransaction() default false;

    boolean slashCommandInteraction() default true;

    boolean prefixCommandInteraction() default true;

    CommandOption[] value() default {};
}
