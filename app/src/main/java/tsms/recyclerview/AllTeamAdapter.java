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

public class AllTeamAdapter extends RecyclerView.Adapter<AllTeamAdapter.MyViewHolder> {

    private Context mContext;
    private List<Team> teamList;
    private Application app;
    private String TN;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, overflow;
        public ImageView thumbnail;
        public View v;


        public MyViewHolder(View view) {
            super(view);
            this.v = view;
            title = (TextView) view.findViewById(R.id.title_all);

            count = (TextView) view.findViewById(R.id.institution_all);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail_all);
            overflow = (TextView) view.findViewById(R.id.overflow_all);
        }
    }


    public AllTeamAdapter(Context mContext, List<Team> teamList, Application app) {
        this.mContext = mContext;
        this.teamList = teamList;
        this.app = app;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_all, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Team team = teamList.get(position);
        holder.title.setText(team.gettname());
        holder.count.setText( team.getInstitution());

        // loading album cover using Glide library
        Glide.with(mContext).load(team.getThumbnail()).override(200, 200).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Viewing " + team.gettname(), Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, ViewTeamActivity.class).putExtra("teamName", team.gettname()));

                /// /Intent intent = new Intent(view.getContext(), MarkTeamActivity.class);
                //intent.putExtra("teamName", team.gettname());
                //startActivityForResult(intent, 0);
            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyApplication) app).team = team;
                Toast.makeText(mContext, "Viewing " + team.gettname(), Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, ViewTeamActivity.class).putExtra("teamName", team.gettname()));

                /// /Intent intent = new Intent(view.getContext(), MarkTeamActivity.class);
                //intent.putExtra("teamName", team.gettname());
                //startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

}
