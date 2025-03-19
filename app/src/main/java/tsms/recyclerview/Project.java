package tsms.recyclerview;

/**
 * Created by shams on 10/16/2017.
 */

public class Project {
    private String uni, cat, pname, p_abstract;

    public Project(String uni, String cat, String pname, String p_abstract) {
        this.uni = uni;
        this.cat = cat;
        this.pname = pname;
        this.p_abstract = p_abstract;
    }

    public String getUni() {
        return uni;
    }

    public void setUni(String uni) {
        this.uni = uni;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getP_abstract() {
        return p_abstract;
    }

    public void setP_abstract(String p_abstract) {
        this.p_abstract = p_abstract;
    }
}
