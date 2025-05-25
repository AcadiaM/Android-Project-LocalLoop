/**
 * Abstract User class, extended by all types of users: Admin, Participant and Organizer.
 */
public abstract class User {

    private String type;
    private String username;
    private String password;
    private String email;


    public User(String type, String username, String password, String email) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getType() {
        return this.type;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }
}