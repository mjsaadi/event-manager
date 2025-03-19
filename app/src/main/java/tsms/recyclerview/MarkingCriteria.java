package tsms.recyclerview;

/**
 * Created by shams on 12/5/2017.
 */

public class MarkingCriteria {
    private int id, total;
    private String name;

    public MarkingCriteria(int id, int total, String name) {
        this.id = id;
        this.total = total;
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
