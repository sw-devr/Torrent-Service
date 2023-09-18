package main.server.payment;

public class RequestChargePointDto {

    private long userId;
    private int addingPoints;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getAddingPoints() {
        return addingPoints;
    }

    public void setAddingPoints(int addingPoints) {
        this.addingPoints = addingPoints;
    }
}
