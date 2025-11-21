package dev.anthhyo.interfaces;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButtonInteractionEvent {
	void execute(ButtonInteractionEvent event);
}
