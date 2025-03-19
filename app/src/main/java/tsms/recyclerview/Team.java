package tsms.recyclerview;

/**
 * Created by shams on 9/30/2017.
 */

//mustafa changed "vote" to "mark" in here, and the setMark (since every judge only sees the mark that they themselves
    //set, would be used in the markTeamActivity
public class Team {
    private String tname;
    private String mentor;
    private String institution;
    private String event;
    private int mark;
    private String cat;
    private int thumbnail;

    public Team(String tname, String mentor, String institution, String event, int votes, int covers){
        this.tname = tname;
        this.mentor = mentor;
        this.institution = institution;
        this.event = event;
        this.mark = votes;
        this.thumbnail = covers;
    }

    public Team(String tname, String mentor, String institution, String event, int vote, String cat, int thumbnail){
        this.tname = tname;
        this.mentor = mentor;
        this.institution = institution;
        this.event = event;
        this.mark = vote;
        this.cat = cat;
        this.thumbnail = thumbnail;
    }

    public String gettname() {
        return tname;
    }

    public String getMentor() {
        return mentor;
    }

    public String getInstitution() {
        return institution;
    }

    public String getEvent() {
        return event;
    }

    public int getMark() {
        return mark;
    }

    public void setTname(String tname){
        this.tname = tname;
    }
    public void setMentor(String mentor){
        this.mentor = mentor;
    }
    public void setInstitution(String institution){
        this.institution = institution;
    }
    public void setEvent(String event){
        this.event = event;
    }

    public void setMark(int mark){
        this.mark = mark;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean compareTo(Team team){
        if (tname == team.tname){
            return true;
        }
        return false;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
}
