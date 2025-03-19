package tsms.recyclerview;

/**
 * Created by ishratkhan on 24/02/16.
 */
public class DataModal {
    private String name;
    private String time;
    private int id;

    public DataModal(int id, String time, String name) {
        this.id = id;
        this.time = time;
        this.name = name;
    }



    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

