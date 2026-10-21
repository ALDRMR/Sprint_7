package Model;

public class Courier {
    private String id;
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
}
