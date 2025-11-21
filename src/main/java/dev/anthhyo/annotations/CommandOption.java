package dev.anthhyo.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.*;

@Repeatable(Command.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandOption {
    OptionType type() default OptionType.STRING;

    String name() default "none";

    String description() default "none";

    boolean required() default true;

    CommandOptionChoice[] value() default {};
}
