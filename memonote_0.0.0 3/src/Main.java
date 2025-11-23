import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/memos", new MemoHandler());
        server.setExecutor(null);
        System.out.println("✅ Memo server running at http://localhost:8080/memos");
        server.start();
    }
}
