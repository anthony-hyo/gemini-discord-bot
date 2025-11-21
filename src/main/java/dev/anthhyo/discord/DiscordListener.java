package dev.anthhyo.discord;

import dev.anthhyo.Main;
import dev.anthhyo.config.Config;
import dev.anthhyo.database.Database;
import dev.anthhyo.discord.data.CommandData;
import dev.anthhyo.discord.events.EventArgs;
import dev.anthhyo.helper.ConversationMemory;
import dev.anthhyo.helper.GeminiService;
import dev.anthhyo.interfaces.IButtonInteractionEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DiscordListener extends ListenerAdapter {

	private static final String TARGET_MEMBER_ID = "165584933149081600";

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(DiscordListener.class);

	private static void handlerCommand(@NotNull MessageReceivedEvent event, String contentDisplay) {
		String[] args = contentDisplay.substring(Config.singleton().discord().prefix().length()).trim().split("\\s+");

		if (args.length < 1) {
			return;
		}

		CommandData commandData = getEventInstance(args[0]);

		if (commandData == null || !commandData.annotation().prefixCommandInteraction()) {
			return;
		}

		commandData.run(event, EventArgs.parse(Arrays.copyOfRange(args, 1, args.length)));
	}

	private static CommandData getEventInstance(String command) {
		return DiscordManager.getCommands().getOrDefault(command, null);
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event) {
		DiscordManager.getCommands().values()
			.stream()
			.filter(commandData -> commandData.annotation().slashCommandInteraction())
			.forEachOrdered(commandData -> {
				CommandCreateAction commandCreateAction = event.getGuild().upsertCommand(commandData.annotation().name(), commandData.annotation().description());

				Collection<OptionData> list = new ArrayList<>();

				Arrays.stream(commandData.annotation().value())
					.forEachOrdered(discordCommandOption -> {
						OptionData optionData = new OptionData(discordCommandOption.type(), discordCommandOption.name(), discordCommandOption.description(), discordCommandOption.required());

						Arrays.stream(discordCommandOption.value())
							.forEach(commandOptionChoice -> optionData.addChoice(commandOptionChoice.name(), commandOptionChoice.value()));

						list.add(optionData);
					});

				commandCreateAction
					.addOptions(list)
					.setGuildOnly(true)
					.queue();
			});
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		CommandData commandData = getEventInstance(event.getName());

		if (commandData == null) {
			return;
		}

		commandData.run(event, null);
	}
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		User author = event.getAuthor();

		if (author.isBot()) {
			return;
		}

		String contentDisplay = event.getMessage().getContentDisplay();

		if (contentDisplay.startsWith(Config.singleton().discord().prefix())) {
			handlerCommand(event, contentDisplay);
		}

		ConversationMemory.addMessage(event.getChannel().getId(), event.getJDA().getSelfUser() + ": " + contentDisplay);
		
		aiReply(event, author, contentDisplay);
	}

	private static void aiReply(@NotNull MessageReceivedEvent event, User author, String contentDisplay) {
		try {
			String aiResponse = GeminiService.generate(Main.prompt(
				ConversationMemory.getHistory(event.getChannel().getId()),
				author.getName(),
				contentDisplay
			));

			if (aiResponse == null) {
				log.error("[DiscordListener] aiResponse is null");
				return;
			}
			
			ConversationMemory.addMessage(event.getChannel().getId(), "You: " + aiResponse);

			aiResponse = aiResponse.replace("{username}", author.getAsMention());

			event.getMessage().reply(aiResponse).queue();
		} catch (Exception e) {
			log.error("[DiscordListener] Error generating the message: {}", e.getMessage());
		}
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if (DiscordManager.actions.containsKey(event.getComponentId())) {
			try {
				Database.open();

				Supplier<IButtonInteractionEvent> action = DiscordManager.actions.get(event.getComponentId());

				action
					.get()
					.execute(event);
			} catch (Exception ex) {
				log.error("error during button interaction", ex);
			} finally {
				Database.close();
			}
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member member = event.getMember();

		if (member.getId().equals(TARGET_MEMBER_ID)) {
			Guild guild = event.getGuild();
			guild.ban(member, 0, TimeUnit.DAYS).queue();
			System.out.println("Permanently banned member with ID: " + member.getId() + " on join.");
		}
	}

	@Override
	public void onReady(ReadyEvent event) {
		event.getJDA().getGuilds().forEach(guild -> {
			guild.loadMembers().onSuccess(members -> {
				members.forEach(member -> {
					if (member.getId().equals(TARGET_MEMBER_ID)) {
						guild.ban(member, 0, TimeUnit.DAYS).queue();
						System.out.println("Permanently banned member with ID: " + member.getId() + " on bot start.");
					}
				});
			});
		});
	}

}
