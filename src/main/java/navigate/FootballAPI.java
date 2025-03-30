package navigate;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FootballAPI {
    private static final String API_URL = "https://api.football-data.org/v4/matches";
    private static final String API_KEY = "23e4f86548474a3cb8836e7be5f669f9";

    public static List<String[]> getNews() {
        List<String[]> newsList = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Auth-Token", API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Ошибка API: " + responseCode);
                return newsList;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (!jsonResponse.has("matches")) {
                System.err.println("Ответ API не содержит 'matches'.");
                return newsList;
            }

            JSONArray matches = jsonResponse.getJSONArray("matches");

            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);

                JSONObject homeTeam = match.optJSONObject("homeTeam");
                JSONObject awayTeam = match.optJSONObject("awayTeam");
                JSONObject competition = match.optJSONObject("competition");

                String homeTeamName = (homeTeam != null) ? homeTeam.optString("name", "Неизвестно") : "Неизвестно";
                String awayTeamName = (awayTeam != null) ? awayTeam.optString("name", "Неизвестно") : "Неизвестно";
                String competitionName = (competition != null) ? competition.optString("name", "Неизвестный турнир") : "Неизвестный турнир";
                String date = match.optString("utcDate", "Дата неизвестна");

                String title = "⚽ Матч: " + homeTeamName + " vs " + awayTeamName;
                String details = "Турнир: " + competitionName + "\nДата: " + date;

                newsList.add(new String[]{title, "", details, date, ""});
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении данных: " + e.getMessage());
        }
        return newsList;
    }
}
