package comp3911.cwk2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 证明 F4（敏感数据泄露）：登录后返回的页面包含高度敏感字段，且没有数据最小化。
 * 当前页面展示的敏感字段：出生日期、诊断（病情）、GP 标识符，以及可与身份关联的姓名。
 * 注意：地址未在模板中输出，故本测试不再断言地址。
 */
public class SensitiveDataDisclosureTest {

    private static Server server;
    private static int port;

    @BeforeAll
    static void startServer() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // ephemeral port
        server.addConnector(connector);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(AppServlet.class, "/*");
        server.setHandler(handler);
        server.start();
        port = connector.getLocalPort();
        System.out.println("Test Jetty started on port " + port);
    }

    @AfterAll
    static void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @DisplayName("Unauthenticated request must NOT leak patient data")
    void unauthenticatedAccessProtected() throws Exception {
        // 仅提交姓氏，不提供用户名/密码，模拟未认证访问
        String body = "surname=Johnson";
        String html = post(body);

        // 应出现未登录提示
        assertTrue(html.contains("The login credentials you supplied are not valid"),
                "Expect invalid login message");

        // 不应出现敏感数据表格标题
        assertFalse(html.contains("Patient Details"), "Must not show details table");

        // 不应出现具体患者敏感字段（诊断、出生日期、GP ID 等）
        assertFalse(html.contains("Liver cancer"), "Diagnosis must not be leaked");
        assertFalse(html.contains("1951-11-27"), "Date of birth must not be leaked");
        assertFalse(html.contains("10"), "GP identifier must not be leaked");
    }

    @Test
    @DisplayName("GET with query parameters must not leak patient data")
    void getQueryDoesNotLeak() throws Exception {
        String html = get("?surname=Johnson");
        assertTrue(html.contains("Patient Records System"), "Expect login page header");
        assertFalse(html.contains("Patient Details"), "Must not show details table");
        assertFalse(html.contains("Liver cancer"), "Diagnosis must not appear");
        assertFalse(html.contains("1951-11-27"), "DOB must not appear");
    }

    @Test
    @DisplayName("Path guessing /Johnson must not leak data")
    void pathGuessDoesNotLeak() throws Exception {
        String html = get("/Johnson");
        assertTrue(html.contains("Patient Records System"), "Expect login page header");
        assertFalse(html.contains("Patient Details"), "Must not show details table");
        assertFalse(html.contains("Liver cancer"), "Diagnosis must not appear");
    }

    private String get(String pathAndQuery) throws IOException {
        URL url = new URL("http://localhost:" + port + pathAndQuery);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r;
        while ((r = is.read(buf)) != -1) {
            baos.write(buf, 0, r);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private String post(String form) throws IOException {
        URL url = new URL("http://localhost:" + port + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        byte[] out = form.getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(out.length));
        try (OutputStream os = conn.getOutputStream()) {
            os.write(out);
        }
        InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r;
        while ((r = is.read(buf)) != -1) {
            baos.write(buf, 0, r);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }
}
