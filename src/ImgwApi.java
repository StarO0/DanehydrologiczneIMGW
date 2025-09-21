import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImgwApi extends JFrame {
    private static final String HYDRO_URL = "https://danepubliczne.imgw.pl/api/data/hydro/";

    public JSONArray fetchHydroData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(HYDRO_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error HTTP: " + response.statusCode());
        }

        return new JSONArray(response.body());
    }

    public static boolean hasValidVoivodeship(JSONObject station) {
        if (!station.has("wojewodztwo") || station.isNull("wojewodztwo")) {
            return false;
        }
        return !"-".equals(station.getString("wojewodztwo"));
    }
}
