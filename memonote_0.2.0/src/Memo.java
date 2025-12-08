public class Memo {

    private String id;
    private String title;
    private String desc;
    private String due;
    private boolean important;
    private String[] tags;
    private String created;

    // ===== 构造函数 =====
    public Memo() {}

    public Memo(String id, String title, String desc, String due,
                boolean important, String[] tags, String created) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.due = due;
        this.important = important;
        this.tags = tags;
        this.created = created;
    }

    // ===== Getter 和 Setter =====
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDue() {
        return due;
    }
    public void setDue(String due) {
        this.due = due;
    }

    public boolean isImportant() {
        return important;
    }
    public void setImportant(boolean important) {
        this.important = important;
    }

    public String[] getTags() {
        return tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
}
