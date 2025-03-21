package models;
import java.sql.Timestamp; 


public class User {
    private int userId;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String password;
    private Timestamp registeredAt;

    // Constructor
    public User(int userId, String fullName, String email, String address, String phoneNumber, String password, Timestamp registeredAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.registeredAt = registeredAt;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }
}
