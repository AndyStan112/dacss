import java.util.Random;

import static java.lang.Thread.sleep;

public class ChatDemoApp {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();

        ChatLoggerChild logger = new ChatLoggerChild();
        ChatNotifier notifier = new ChatNotifier();
        ChatStatistics statistics = new ChatStatistics();

        eventBus.register(logger);
        eventBus.register(notifier);
        eventBus.register(statistics);

        int numberOfUsers = 5;

        for (int u = 0; u < numberOfUsers; u++) {
            int userId = u;

            new Thread(() -> {
                int i = 0;
                Random rand = new Random();

                try {
                    while (true) {
                        sleep(rand.nextInt(5000));
                        eventBus.post(new MessageEventChild(
                                "User" + userId,
                                "Message " + i + " from User" + userId
                        ));
                        i++;
                    }
                } catch (InterruptedException e) {
                }
            }).start();
        }
    }
}