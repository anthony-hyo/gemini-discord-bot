package dev.anthhyo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.anthhyo.config.data.DatabaseData;
import dev.anthhyo.config.data.discord.DiscordData;
import dev.anthhyo.config.data.gemini.GeminiData;

import java.io.File;

public record Config(DatabaseData database, GeminiData gemini, DiscordData discord) {

	private static final Config data;

	static {
		try {
			data = new ObjectMapper(new YAMLFactory())
				.readValue(
					new File(String.format("%s%sconf%s%s", (new File(".").getCanonicalPath()), File.separatorChar, File.separatorChar, "config.yml")),
					Config.class
				);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static Config singleton() {
		return data;
	}

}


