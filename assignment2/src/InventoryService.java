public class InventoryService {
    @Subscribe(threading = ThreadMode.ASYNC)
    public void handleOrder(OrderPlacedEvent event) {
        System.out.println("[INVENTORY] Reserving stock for order #" + event.getOrderId());
    }
}