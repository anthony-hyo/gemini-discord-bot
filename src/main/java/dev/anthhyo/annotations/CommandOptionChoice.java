package dev.anthhyo.annotations;

import java.lang.annotation.*;

@Repeatable(CommandOption.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandOptionChoice {
	String name();

	String value();
}
