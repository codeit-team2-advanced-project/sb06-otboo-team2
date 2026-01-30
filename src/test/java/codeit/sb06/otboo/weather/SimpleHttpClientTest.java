package codeit.sb06.otboo.weather;

import static org.assertj.core.api.Assertions.*;

import codeit.sb06.otboo.weather.client.SimpleHttpClient;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleHttpClientTest {

  private static HttpServer server;
  private static int port;
  private static final AtomicReference<String> lastHeaderValue = new AtomicReference<>();

  private SimpleHttpClient client;

  @BeforeAll
  static void startServer() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    port = server.getAddress().getPort();

    server.createContext("/ok", exchange -> {
      lastHeaderValue.set(exchange.getRequestHeaders().getFirst("X-Test"));
      byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });

    server.createContext("/error", exchange -> {
      byte[] body = "bad".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(400, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });

    server.start();
  }

  @AfterAll
  static void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  @BeforeEach
  void setUp() {
    client = new SimpleHttpClient();
    lastHeaderValue.set(null);
  }

  @Test
  void get_성공시_본문과_헤더를_정상_처리한다() throws IOException {
    // given
    String url = "http://localhost:" + port + "/ok";

    // when
    String body = client.get(url, Map.of("X-Test", "yes"));

    // then
    assertThat(body).isEqualTo("ok");
    assertThat(lastHeaderValue.get()).isEqualTo("yes");
  }

  @Test
  void get_실패시_에러본문을_포함한_IOException을_던진다() {
    // given
    String url = "http://localhost:" + port + "/error";

    // when / then
    assertThatThrownBy(() -> client.get(url, Map.of()))
        .isInstanceOf(IOException.class)
        .hasMessageContaining("status=400")
        .hasMessageContaining("body=bad");
  }
}
