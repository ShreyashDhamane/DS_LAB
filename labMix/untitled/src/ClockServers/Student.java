package ClockServers;

public class Student {
    private String username;
    private String password;

    public Student(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public boolean authenticateUser(String username, String password) {

        return this.username.equals(username) && this.password.equals(password);
    }
}
