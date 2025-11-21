package dev.anthhyo.discord.data;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record CommandOptionData(OptionType type, String name, String description, boolean required) {

}
