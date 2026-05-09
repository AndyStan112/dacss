public class ChatLogger {
    @Subscribe(threading = ThreadMode.ASYNC)
    public void onMessage(MessageEventChild event) {
        System.out.println("[LOG] " + event.getSender() + ": " + event.getMessage());
    }
}