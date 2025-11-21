package dev.anthhyo.config.data.discord;

import java.util.List;

public record DiscordData(String token, String prefix, List<String> administrators) {

}
