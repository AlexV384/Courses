package model;

import java.time.OffsetDateTime;

public class Teacher {
    private int teacherId;
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private OffsetDateTime registrationDate;

    public Teacher() {}

    public Teacher(int userId, String name, String email, String passwordHash, OffsetDateTime registrationDate) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public OffsetDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(OffsetDateTime registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public String toString() {
        return String.format("Teacher{id=%d, name='%s', email='%s'}", teacherId, name, email);
    }
}