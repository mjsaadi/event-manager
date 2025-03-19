package tsms.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shams on 12/15/2017.
 */

public class WinnerActivityMarksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    Context context;
    List<MarkingCriteria> markingCriteriaList;


    private static class MarksViewHolder extends RecyclerView.ViewHolder {



        TextView name = itemView.findViewById(R.id.fieldname);

        ProgressBar pb = itemView.findViewById(R.id.pbInn);
        TextView count = itemView.findViewById(R.id.fieldscore);



        MarksViewHolder(View itemView) {
            super(itemView);

        }

    }

    @NonNull
    private List<Grade> items = Collections.emptyList();

    public WinnerActivityMarksAdapter(@NonNull List<Grade> items, Activity act, Context context) {
        markingCriteriaList = new ArrayList<>();
        this.activity = act;

        this.items = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


                View itemView = inflater.inflate(R.layout.marks_cv, parent, false);

                return new MarksViewHolder(itemView);

        }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


                Grade grade =  items.get(position);
                MarksViewHolder holder = (MarksViewHolder) viewHolder;


                holder.name.setText(grade.getName());
                holder.pb.setProgress(grade.getScore());
                holder.pb.setMax(grade.getTotal());
                holder.count.setText(Integer.toString(grade.getScore()));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }



}