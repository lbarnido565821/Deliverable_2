package model;

public class User {
    public String idOrEmail;
    public String password;
    public String name;
    public String role; // GUEST, ADMIN, FRONT_DESK, HOUSEKEEPING
    public int rewardPoints;

    public User(String idOrEmail, String password, String name, String role) {
        this.idOrEmail = idOrEmail;
        this.password = password;
        this.name = name;
        this.role = role;
        this.rewardPoints = 0;
    }
}
