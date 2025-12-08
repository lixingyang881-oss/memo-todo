import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import com.google.gson.Gson;

public class MemoHandler implements HttpHandler {
    private final MemoStore store = new MemoStore();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // 允许跨域（否则你的前端 fetch 会被浏览器拦截）
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if (method.equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (path.equals("/memos") && method.equals("GET")) {
            handleList(exchange);
        } else if (path.equals("/memos") && method.equals("POST")) {
            handleCreate(exchange);
        } else if (path.startsWith("/memos/") && method.equals("PUT")) {
            String id = path.substring("/memos/".length());
            handleUpdate(exchange, id);
        } else if (path.startsWith("/memos/") && method.equals("DELETE")) {
            String id = path.substring("/memos/".length());
            handleDelete(exchange, id);
        } else {
            sendJson(exchange, 404, "{\"error\":\"not found\"}");
        }
    }

    private void handleList(HttpExchange ex) throws IOException {
        sendJson(ex, 200, gson.toJson(store.loadAll()));
    }

    private void handleCreate(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes());
        Memo m = gson.fromJson(body, Memo.class);
        m.setId(UUID.randomUUID().toString());
        m.setCreated(new Date().toString());
        store.add(m);
        sendJson(ex, 200, gson.toJson(m));
    }

    private void handleUpdate(HttpExchange ex, String id) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes());
        Memo m = gson.fromJson(body, Memo.class);
        m.setId(id);
        store.update(id, m);
        sendJson(ex, 200, gson.toJson(m));
    }

    private void handleDelete(HttpExchange ex, String id) throws IOException {
        store.delete(id);
        sendJson(ex, 200, "{\"ok\":true}");
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] resp = json.getBytes();
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, resp.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(resp);
        }
    }
}
