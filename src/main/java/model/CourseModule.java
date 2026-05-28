package model;

public class CourseModule {
    private int id;
    private int courseId;
    private String name;
    private String description;

    public CourseModule() {}

    public CourseModule(int courseId, String name, String description) {
        this.courseId = courseId;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("CourseModule{id=%d, name='%s', courseId=%d}", id, name, courseId);
    }
}
