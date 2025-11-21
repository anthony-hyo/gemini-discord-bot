package dev.anthhyo.interfaces;

import dev.anthhyo.discord.events.EventArgs;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IEvent {

    void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception;

    void onMessageReceived(MessageReceivedEvent event, EventArgs args) throws Exception;

}
