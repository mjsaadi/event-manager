package tsms.recyclerview;


import io.socket.client.Socket;

public class constants {
    public static final String CHAT_SERVER_URL = "http://52.27.201.136:4001";
    public final static String FRAG_A="fragment_a";
    public final static String FRAG_B="fragment_b";
    public final static String FRAG_C="fragment_c";
    public static String event;
    private static String Team;
    public static Socket mSocket;

    public static String newMessage ="updatechat";
    public static String socketConnection = "socket.connection";
    public static String login = "login";
    public static String addUser = "add user";
    public static String connectionFailure = "failedToConnect";



    public static String getTeam() {
        return Team;
    }

    public static void setTeam(String team) {
        Team = team;
    }
}
