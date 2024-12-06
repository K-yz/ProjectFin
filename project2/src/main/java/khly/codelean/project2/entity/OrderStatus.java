package khly.codelean.project2.entity;

public enum OrderStatus {
    PENDING("Chưa xác nhận"),
    CONFIRMED("Xác nhận");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

