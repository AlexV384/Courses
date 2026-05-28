package model;

public class Course {
    private int id;
    private int teacherId;
    private String name;
    private String description;
    private String duration;

    public Course() {}

    public Course(int teacherId, String name, String description, String duration) {
        this.teacherId = teacherId;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    @Override
    public String toString() {
        return String.format("Course{id=%d, name='%s', teacherId=%d}", id, name, teacherId);
    }
}