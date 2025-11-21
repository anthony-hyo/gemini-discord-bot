package dev.anthhyo.helper;

import dev.anthhyo.config.Config;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeminiService {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GeminiService.class);

	private static String jsonEscape(String text) {
		return text
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r");
	}
	
	public static String generate(String message) throws IOException {
		OkHttpClient client = new OkHttpClient();
		
		JSONObject jsonObject = new JSONObject()
			.element("contents", new JSONArray()
				.element(new JSONObject()
					.element("parts", new JSONArray()
						.element(new JSONObject()
							.element("text", message)
						)
					)
				)
			);

		String jsonBody = jsonObject.toString();

		Request request = new Request.Builder()
			.url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent")
			.post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
			.addHeader("Content-Type", "application/json")
			.addHeader("X-goog-api-key", Config.singleton().gemini().key())
			.build();

		Response response = client.newCall(request).execute();
		ResponseBody body = response.body();

		if (body == null) {
			log.error("[GeminiService] Body null");
			return null;
		}

		String bodyString = body.string();
		JSONObject json = JSONObject.fromObject(bodyString);

		try {
			return json.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
		} catch (Exception e) {
			log.error("[GeminiService] JSON error {} {}", bodyString, e.getMessage());
			return null;
		}
	}
	
}
