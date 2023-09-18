package main.server.payment;

import main.server.user.UserRole;

public class RequestPurchaseAuthorityDto {

    private long userId;
    private UserRole role;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
