import java.util.concurrent.atomic.DoubleAdder;

public class RevenueService {
    private final DoubleAdder totalRevenue = new DoubleAdder();

    @Subscribe(threading = ThreadMode.ASYNC)
    public void handleOrder(OrderPlacedEvent event) {
        totalRevenue.add(event.getAmount());
        System.out.println("[REVENUE] Total revenue: " + totalRevenue.sum());
    }
}