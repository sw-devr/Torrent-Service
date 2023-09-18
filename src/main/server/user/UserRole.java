package main.server.user;

public enum UserRole {

    GENERAL(0),
    UPLOADER(3000);

    private final int price;

    UserRole(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
