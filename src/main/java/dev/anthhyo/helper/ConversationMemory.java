package dev.anthhyo.helper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConversationMemory {

	private static final Map<String, Deque<String>> channelHistory = new ConcurrentHashMap<>();

	private static final int MAX_HISTORY = 10;

	public static void addMessage(String channelId, String message) {
		channelHistory.computeIfAbsent(channelId, _ -> new ArrayDeque<>());
		
		Deque<String> history = channelHistory.get(channelId);

		if (history.size() >= MAX_HISTORY) {
			history.pollFirst();
		}

		history.addLast(message);
	}

	public static String getHistory(String channelId) {
		Deque<String> history = channelHistory.get(channelId);
		return history == null || history.isEmpty() ? "" : String.join("\n", history);
	}

}
