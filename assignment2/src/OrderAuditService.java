public class OrderAuditService {
    @Subscribe
    public void handleOrder(OrderPlacedEvent event) {
        System.out.println("[AUDIT] Order #" + event.getOrderId() + " placed by " + event.getCustomer() + " amount=" + event.getAmount());
    }
}