import java.util.concurrent.atomic.AtomicInteger;

public class ChatStatistics {
    private final AtomicInteger totalMessages = new AtomicInteger(0);

    @Subscribe(threading = ThreadMode.ASYNC)
    public void onMessage(MessageEvent event) {
        int count = totalMessages.incrementAndGet();
        System.out.println("[STATS] Total messages: " + count);
    }
}