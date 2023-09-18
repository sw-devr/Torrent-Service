package main.server.user;

import java.util.Objects;

import static main.server.user.UserConstants.INIT_POINTS;

public class User {

    private long id;
    private String email;
    private String password;
    private UserRole role;
    private long points;

    public static User init(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setPoints(INIT_POINTS);
        user.setRole(UserRole.GENERAL);

        return user;
    }

    public boolean isEnoughPoints(int filePrice) {

        return this.points >= filePrice;
    }

    public void payPoints(int points) {

        if(!isEnoughPoints(points)) {
            throw new IllegalArgumentException("현재 보유 포인트가 구매하려는 포인트보다 적습니다.");
        }
        this.points -= points;
    }

    public void receivePoints(int points) {

        this.points += points;
    }

    public int purchaseAuthority(UserRole role) {

        if(this.role.getPrice() >= role.getPrice()) {
            throw new IllegalStateException("이미 가입된 상품입니다.");
        }
        int rolePrice = role.getPrice();
        if(!isEnoughPoints(rolePrice)) {
            throw new IllegalArgumentException("현재 보유 포인트가 구매하려는 포인트보다 적습니다.");
        }
        payPoints(rolePrice);
        this.role = role;

        return rolePrice;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", points=" + points +
                '}';
    }

    public boolean isCorrectPassword(String password) {

        return this.password.equals(password);
    }
}
