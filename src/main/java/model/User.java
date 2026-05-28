package model;

import java.time.OffsetDateTime;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private OffsetDateTime registrationDate;

    public User() {}

    public User(String name, String email, String passwordHash, OffsetDateTime registrationDate) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public User(int id, String name, String email, String passwordHash, OffsetDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = registrationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
        return String.format("User{id=%d, name='%s', email='%s'}", id, name, email);
    }
}