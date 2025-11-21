package dev.anthhyo.discord.events.commands;

import dev.anthhyo.annotations.Command;
import dev.anthhyo.annotations.CommandOption;
import dev.anthhyo.discord.DiscordManager;
import dev.anthhyo.discord.events.EventArgs;
import dev.anthhyo.discord.events.EventDefault;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(
    name = "say",
    description = "say something",
    value = {
        @CommandOption(
            type = OptionType.STRING,
            name = "message",
            description = "Say something"
        )
    }
)
public class Say extends EventDefault {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
        OptionMapping optionMessage = event.getOption("message");

        if (optionMessage == null) {
            return;
        }

        event
            .getChannel()
            .sendMessage(DiscordManager.processDiscordMentions(optionMessage.getAsString()))
            .queue();

        event
            .reply("Done")
            .setEphemeral(true)
            .queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event, EventArgs args) throws Exception {
        if (!args.has(0)) {
            return;
        }

        event.getMessage().delete().queue();

        event.getChannel().sendMessage(DiscordManager.processDiscordMentions(args.getStr(0))).queue();
    }

}
