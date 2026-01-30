package codeit.sb06.otboo.weather.client;

import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SimpleHttpClient {

  private final RestClient restClient;

  public SimpleHttpClient(RestClient.Builder builder) {
    this.restClient = builder.build();
  }

  public String get(String urlString, Map<String, String> headers) throws IOException {
    try {
      return restClient.get()
          .uri(urlString)
          .headers(httpHeaders -> {
            if (headers != null) {
              headers.forEach(httpHeaders::set);
            }
          })
          .retrieve()
          .body(String.class);
    } catch (RestClientResponseException e) {
      String body = e.getResponseBodyAsString();
      throw new IOException(
          "HTTP GET failed: " + urlString + " status=" + e.getStatusCode().value() + " body=" + body,
          e
      );
    }
  }
}
