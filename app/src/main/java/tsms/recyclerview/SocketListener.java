package tsms.recyclerview;

/**
 * Created by shams on 10/23/2017.
 */

public interface SocketListener {
    void onSocketConnected();
    void onSocketDisconnected();
    void onNewMessageReceived(String username, String message);
}

