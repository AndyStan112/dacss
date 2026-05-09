public class OrderDemoApp {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();

        OrderAuditService auditService = new OrderAuditService();
        InventoryService inventoryService = new InventoryService();
        RevenueService revenueService = new RevenueService();

        eventBus.register(auditService);
        eventBus.register(inventoryService);
        eventBus.register(revenueService);

        eventBus.post(new OrderPlacedEvent(1001, "Andy", 250.0));
        eventBus.post(new OrderPlacedEvent(1002, "Izy", 180.5));
        eventBus.post(new OrderPlacedEvent(1003, "Alex", 99.99));

        eventBus.shutdown();
    }
}