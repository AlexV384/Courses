package model;

public class Lesson {
    private int id;
    private int moduleId;
    private String name;
    private String description;

    public Lesson() {}

    public Lesson(int moduleId, String name, String description) {
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getModuleId() { return moduleId; }
    public void setModuleId(int moduleId) { this.moduleId = moduleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("Lesson{id=%d, name='%s', moduleId=%d}", id, name, moduleId);
    }
}