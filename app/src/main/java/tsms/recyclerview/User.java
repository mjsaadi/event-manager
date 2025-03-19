package tsms.recyclerview;

/**
 * Created by shams on 9/16/2017.
 */

public class User {

    private String id;
    private String loginCode, event;

    public User(String id, String loginCode, String event)
    {
        this.id = id;
        this.loginCode = loginCode;
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
