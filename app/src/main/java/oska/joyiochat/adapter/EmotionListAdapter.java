package oska.joyiochat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import oska.joyiochat.R;
import oska.joyiochat.module.EmotionModel;

/**
 * Created by theoska on 4/9/17.
 */

public class EmotionListAdapter extends RecyclerView.Adapter<EmotionListAdapter.EmotionViewHolder>{

    private Context context;
    private Activity activity;
    private ArrayList<EmotionModel> emotionModelArrayList;
    private EmotionViewHolder emotionViewHolder;
    private int selectedPosition = -1;
    public EmotionListAdapter(Activity activity, ArrayList<EmotionModel> emotionModelArrayList){
        this.activity = activity;
        this.emotionModelArrayList = emotionModelArrayList;
    }


    public class EmotionViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        AVLoadingIndicatorView loadingIndicatorView;
        FrameLayout flRoot;
        public EmotionViewHolder(View view){
            super(view);
            circleImageView = (CircleImageView) view.findViewById(R.id.civ_icon);
            flRoot = (FrameLayout) view.findViewById(R.id.fl_root);
            loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.loading_view);
        }

    }

    @Override
    public EmotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emotion,parent,false);
        emotionViewHolder = new EmotionViewHolder(itemView);
        return emotionViewHolder;
    }

    @Override
    public void onBindViewHolder(EmotionViewHolder holder, int position) {
        if(emotionModelArrayList.get(position).getIcon() != 0) {
            holder.circleImageView.setImageResource(emotionModelArrayList.get(position).getIcon());
        }


    }

    @Override
    public int getItemCount() {
        return emotionModelArrayList.size();
    }



}
