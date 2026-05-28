package model;

public class Test {
    private int id;
    private int lessonId;
    private String name;

    public Test() {}

    public Test(int lessonId, String name) {
        this.lessonId = lessonId;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return String.format("Test{id=%d, name='%s', lessonId=%d}", id, name, lessonId);
    }
}