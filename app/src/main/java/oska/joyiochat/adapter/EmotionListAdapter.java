package oska.joyiochat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import oska.joyiochat.R;
import oska.joyiochat.module.EmotionModel;

/**
 * Created by theoska on 4/9/17.
 */

public class EmotionListAdapter extends RecyclerView.Adapter<EmotionListAdapter.EmotionViewHolder>{

    private Context context;
    private ArrayList<EmotionModel> emotionModelArrayList;

    public EmotionListAdapter(Context context, ArrayList<EmotionModel> emotionModelArrayList){
        this.context = context;
        this.emotionModelArrayList = emotionModelArrayList;
    }
    public class EmotionViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        public EmotionViewHolder(View view){
            super(view);
            circleImageView = (CircleImageView) view.findViewById(R.id.civ_icon);
        }
    }

    @Override
    public EmotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emotion,parent,false);
        return new EmotionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmotionViewHolder holder, int position) {
        if(emotionModelArrayList.get(position).getIcon() != 0)
            holder.circleImageView.setImageResource(emotionModelArrayList.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return emotionModelArrayList.size();
    }

}
