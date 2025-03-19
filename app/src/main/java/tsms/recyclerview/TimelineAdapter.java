package tsms.recyclerview;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shams on 9/30/2017.
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private Context mContext;
    private List<TimelineData> timelineDataList;
    private Application app;
    private String TN;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView agenda, amPm, time;


        public MyViewHolder(View view) {
            super(view);
            agenda = (TextView) view.findViewById(R.id.agenda_text);

            amPm = (TextView) view.findViewById(R.id.amPm_text);
            time = (TextView) view.findViewById(R.id.time_text);
           }
    }


    public TimelineAdapter(Context mContext, List<TimelineData> timelineDataList, Application app) {
        this.mContext = mContext;
        this.timelineDataList = timelineDataList;
        this.app = app;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeslice, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TimelineData timelineData = timelineDataList.get(position);
        holder.agenda.setText(timelineData.getAgendaText());
        //holder.amPm.setText(timelineData.getAmPm());
        holder.time.setText(timelineData.getTime());
    }

    @Override
    public int getItemCount() {
        return timelineDataList.size();
    }

}
