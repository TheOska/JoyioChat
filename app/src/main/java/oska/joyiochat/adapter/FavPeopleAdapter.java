package oska.joyiochat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



import butterknife.BindView;
import butterknife.ButterKnife;
import oska.joyiochat.R;
import oska.joyiochat.module.FavPeopleItem;


/**
 * Created by froger_mcs on 05.11.14.
 * <p/>
 * FavPeopleAdapter: An adapter for the ChatRoom  which put  in the part of recyclerview in the ChatRoomFragment ,
 * Model use:  FavPeopleItem
 */
public class FavPeopleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;


    private List<FavPeopleItem> favPeopleItems = new ArrayList<>();
    private Context context;
    private OnFavPeopleItemClickListener onFavPeopleItemClickListener;


    public FavPeopleAdapter(Context context, List<FavPeopleItem> favPeopleItems) {
        this.favPeopleItems = favPeopleItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_fav_people, parent, false);

            FavViewHolder favViewHolder = new FavViewHolder(view);
                setupClickableViews(view, favViewHolder);
            return favViewHolder;
        }
        return null;
    }

    private void setupClickableViews(final View view, final FavViewHolder favViewHolder) {
        favViewHolder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavPeopleItemClickListener.onImageClick(view, favViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((FavViewHolder) viewHolder).bindView(favPeopleItems.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return favPeopleItems.size();
    }


    public void setOnFavPeopleItemClickListener(OnFavPeopleItemClickListener onFavPeopleItemClickListener) {
        this.onFavPeopleItemClickListener = onFavPeopleItemClickListener;
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView favImage;
        TextView favName;

        FavPeopleItem favPeopleItem;

        public FavViewHolder(View view) {
            super(view);
//            ButterKnife.bind(this, view);
            favImage = (ImageView) view.findViewById(R.id.fav_image);
            favName = (TextView) view.findViewById(R.id.fav_name);
        }

        public void bindView(FavPeopleItem favPeopleItem) {
            this.favPeopleItem = favPeopleItem;
            favImage.setImageResource(favPeopleItem.getFavProfilePic());
            favName.setText(favPeopleItem.getFavName());
        }

        public FavPeopleItem getFavPeopleItem() {
            return favPeopleItem;
        }
    }


    public interface OnFavPeopleItemClickListener {
        void onImageClick(View v, int position);
    }
}
