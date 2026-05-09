public class ChatNotifier {
    @Subscribe(threading = ThreadMode.ASYNC)
    public void onMessage(MessageEvent event) {
        System.out.println("[NOTIFY] New message from " + event.getSender());
    }
}