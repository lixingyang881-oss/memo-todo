import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MemoStore {
    private static final String DB_FILE = "memos.json";
    private final Gson gson = new Gson();

    public synchronized List<Memo> loadAll() {
        try {
            if (!Files.exists(Paths.get(DB_FILE))) {
                return new ArrayList<>();
            }
            String json = Files.readString(Paths.get(DB_FILE));
            if (json.isBlank()) return new ArrayList<>();
            return gson.fromJson(json, new TypeToken<List<Memo>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public synchronized void saveAll(List<Memo> memos) {
        try {
            String json = gson.toJson(memos);
            Files.writeString(Paths.get(DB_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void add(Memo m) {
        List<Memo> all = loadAll();
        all.add(m);
        saveAll(all);
    }

    public synchronized void update(String id, Memo updated) {
        List<Memo> all = loadAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) {
                all.set(i, updated);
            }
        }
        saveAll(all);
    }

    public synchronized void delete(String id) {
        List<Memo> all = loadAll();
        all.removeIf(m -> m.getId().equals(id));
        saveAll(all);
    }
}
