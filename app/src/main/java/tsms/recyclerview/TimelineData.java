package tsms.recyclerview;

/**
 * Created by shams on 9/30/2017.
 */

public class TimelineData
{
    //private int eventId;
    private String agendaText;
   // private boolean amPm;
    private String time;

    public TimelineData()
    {
     //   this.amPm=false;
        this.agendaText="This hour will include...";
    }

    public TimelineData(String agendaText, /*boolean amPm,*/ String time){

        this.agendaText = agendaText;
     //   this.amPm = amPm;
        this.time = time;
    }

    public String getAgendaText() {
        return agendaText;
    }

 //   public boolean getAmPm() {
   //     return amPm;
   // }

    public String getTime() {
        return time;
    }

}
