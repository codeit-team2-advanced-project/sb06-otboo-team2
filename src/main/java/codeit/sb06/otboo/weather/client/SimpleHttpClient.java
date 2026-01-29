package codeit.sb06.otboo.weather.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SimpleHttpClient {

  public String get(String urlString, Map<String, String> headers) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(urlString).openConnection();
      conn.setRequestMethod("GET");

      // GET에서는 Content-Type 거의 의미 없고 Accept/Authorization이 중요
      if (headers != null) {
        for (var e : headers.entrySet()) {
          conn.setRequestProperty(e.getKey(), e.getValue());
        }
      }

      int status = conn.getResponseCode();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(
          (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream()
      ))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);

        if (status < 200 || status >= 300) {
          throw new IOException("HTTP GET failed: " + urlString + " status=" + status + " body=" + sb);
        }
        return sb.toString();
      }
    } finally {
      if (conn != null) conn.disconnect();
    }
  }
}