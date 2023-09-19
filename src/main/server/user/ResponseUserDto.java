package main.server.user;

public class ResponseUserDto {

    private long userId;
    private String email;
    private long points;
    private UserRole role;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ResponseUserDto{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", points=" + points +
                ", role=" + role +
                '}';
    }
}
