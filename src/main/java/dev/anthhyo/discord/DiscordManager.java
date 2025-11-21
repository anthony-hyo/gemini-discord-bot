package dev.anthhyo.discord;

import dev.anthhyo.Main;
import dev.anthhyo.annotations.Command;
import dev.anthhyo.config.Config;
import dev.anthhyo.discord.data.CommandData;
import dev.anthhyo.interfaces.IButtonInteractionEvent;
import dev.anthhyo.interfaces.IEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordManager {

	public static final HashMap<String, Supplier<IButtonInteractionEvent>> actions = new HashMap<>();
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(DiscordManager.class);
	private static final HashMap<String, CommandData> commands = new HashMap<>();
	private static DiscordManager singleton;
	private net.dv8tion.jda.api.JDA JDA;

	public DiscordManager() throws Exception {
		singleton = this;

		log.info("> Commands");
		setupCommands();

		log.info("> JDA");
		setupJDA();
	}

	public static DiscordManager getSingleton() {
		return singleton;
	}

	public static HashMap<String, CommandData> getCommands() {
		return commands;
	}

	private void setupJDA() {
		JDABuilder builder = JDABuilder.createDefault(Config.singleton().discord().token());

		// enable member chunking for all guilds
		// builder.setChunkingFilter(ChunkingFilter.ALL);

		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.playing(":)"));
		builder.addEventListeners(new DiscordListener());

		this.JDA = builder.build();
	}

	@SuppressWarnings("unchecked")
	private void setupCommands() {
		Reflections reflections = new Reflections("dev.anthhyo.discord.events.commands");

		Set<Class<? extends IEvent>> commands = reflections.getSubTypesOf(IEvent.class);

		commands.forEach(command -> {
			log.info("command {}", command.getName());

			if (!command.isAnnotationPresent(Command.class)) {
				return;
			}

			Command commandAnnotation = command.getDeclaredAnnotation(Command.class);

			getCommands().put(
				commandAnnotation.name(),
				new CommandData(
					commandAnnotation,
					(Class<IEvent>) command
				)
			);
		});
	}

	public JDA getJDA() {
		return JDA;
	}

	public static final Pattern COMBINED_PATTERN = Pattern.compile("@(everyone|here)|<@&[0-9]+>");

	public static String processDiscordMentions(String input) {
		Matcher matcher = COMBINED_PATTERN.matcher(input);
		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			matcher.appendReplacement(result, "");
		}
		matcher.appendTail(result);
		return result.toString();
	}

}
