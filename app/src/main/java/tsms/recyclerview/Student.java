package tsms.recyclerview;

/**
 * Created by shams on 10/2/2017.
 */

public class Student {
    private String name;
    private String team;

    public Student(){

    }

    public Student(String name, String team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
