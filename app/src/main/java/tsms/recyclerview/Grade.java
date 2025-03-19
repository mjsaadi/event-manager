package tsms.recyclerview;

/**
 * Created by shams on 12/6/2017.
 */

public class Grade  {
    private int id, score, round,total;
    private String judge, name, team;

    public Grade(int id, int score, int round, int total, String judge, String name, String team) {
        this.id = id;
        this.score = score;
        this.round = round;
        this.total = total;
        this.judge = judge;
        this.name = name;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
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

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }



    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
