public class OrderPlacedEvent {
    private final int orderId;
    private final String customer;
    private final double amount;

    public OrderPlacedEvent(int orderId, String customer, double amount) {
        this.orderId = orderId;
        this.customer = customer;
        this.amount = amount;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomer() {
        return customer;
    }

    public double getAmount() {
        return amount;
    }
}