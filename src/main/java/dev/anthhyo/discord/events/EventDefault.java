package dev.anthhyo.discord.events;

import dev.anthhyo.exceptions.NotImplementedException;
import dev.anthhyo.interfaces.IEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EventDefault implements IEvent {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event, EventArgs args) throws Exception {
		throw new NotImplementedException();
	}

}
