package main.server.user;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseUserDto)) return false;
        ResponseUserDto that = (ResponseUserDto) o;
        return userId == that.userId && points == that.points && Objects.equals(email, that.email) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, points, role);
    }
}
