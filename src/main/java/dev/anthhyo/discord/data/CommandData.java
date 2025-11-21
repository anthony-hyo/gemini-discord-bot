package dev.anthhyo.discord.data;

import dev.anthhyo.annotations.Command;
import dev.anthhyo.database.Database;
import dev.anthhyo.discord.events.EventArgs;
import dev.anthhyo.interfaces.IEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.StaleModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public record CommandData(Command annotation, Class<IEvent> command) {

	private static final Logger log = LoggerFactory.getLogger(CommandData.class);

	public IEvent eventInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return command().getDeclaredConstructor().newInstance();
	}

	public void run(net.dv8tion.jda.api.events.Event JDAEvent, @Nullable EventArgs args) {
		boolean shouldConnect = annotation().databaseOpen();
		boolean shouldTransaction = annotation().databaseTransaction();

		try {
			if (shouldConnect) {
				Database.open();
			}

			if (shouldTransaction) {
				Database.openTransaction();
			}

			if (JDAEvent instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
				log.info("slash {} used by {} with args {}", annotation().name(), slashCommandInteractionEvent.getUser().getId(), slashCommandInteractionEvent.getOptions());

				eventInstance().onSlashCommandInteraction(slashCommandInteractionEvent);
			} else if (JDAEvent instanceof MessageReceivedEvent messageReceivedEvent) {
				assert args != null;
				log.info("command {} used by {} with args {}", annotation().name(), messageReceivedEvent.getAuthor().getId(), args);

				eventInstance().onMessageReceived(messageReceivedEvent, args);
			}

			if (shouldTransaction) {
				Database.commit();
			}
		} catch (StaleModelException ex) {
			log.error("[E1] error occurred in ", ex);

			if (shouldTransaction) {
				Database.rollback();
			}
		} catch (DBException ex) {
			log.error("[E2] error occurred in ", ex);
		} catch (Exception ex) {
			log.error("[E3] error occurred in ", ex);
		} finally {
			if (shouldConnect) {
				Database.close();
			}
		}
	}

}
