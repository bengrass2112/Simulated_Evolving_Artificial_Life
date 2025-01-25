package gui;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Client {

	static DataOutputStream os;
	static byte[] input;

	public static int session_id;

	public static String server_url;

	public static void post(JsonObject json, String path) {

		try {

			URL url = new URL(server_url + path + "?session_id=" + session_id);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);

			os = new DataOutputStream(connection.getOutputStream());
			input = json.toString().getBytes("utf-8");
			os.write(input, 0, input.length);
			os.close();

			connection.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static JsonObject get(String path, String query) {

		try {

			URL url = new URL(server_url + path + "?session_id=" + session_id + query);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			connection.connect();

			return returnInfo(connection);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static JsonObject login(JsonObject json) {

		try {

			String server = server_url + "login";
			URL url = new URL(server);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);

			os = new DataOutputStream(connection.getOutputStream());
			input = json.toString().getBytes("utf-8");
			os.write(input, 0, input.length);
			os.close();

			connection.connect();

			return returnInfo(connection);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static JsonObject returnInfo(HttpURLConnection connection) {

		try {

			StringBuilder sb = new StringBuilder();
			if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {

				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				Gson gson = new Gson();
				JsonElement element = gson.fromJson(sb.toString(), JsonElement.class);
				br.close();
				return element.getAsJsonObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
