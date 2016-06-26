package oska.joyiochat.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import oska.joyiochat.R;
import oska.joyiochat.adapter.FavPeopleAdapter;
import oska.joyiochat.module.FavPeopleItem;

/**
 * Created by TheOSka on 21/6/2016.
 */

public class ChatRoomFragment extends Fragment implements
        FavPeopleAdapter.OnFavPeopleItemClickListener {

    @Bind(R.id.recycler_fav_people)
    RecyclerView rvFavPeople;

    private GridLayoutManager gridLayoutManager;

    private FavPeopleAdapter mFavPeopleAdapter;
    List<FavPeopleItem> favPeopleItemArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        ButterKnife.bind(this, view);
        initItemList();
        initRecyclerView();
        return view;
    }

    private void initItemList() {
        String[] favName = {"IU", "Leonardo", "Tony Stark", "Marry"};
        Integer[] favIcon = {R.drawable.fav_people_icon,
                R.drawable.fav_people_icon2,
                R.drawable.fav_people_icon3,
                R.drawable.fav_people_icon
        };
        favPeopleItemArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            FavPeopleItem mFavPeopleItem = new FavPeopleItem();
            mFavPeopleItem.setFavName(favName[i]);
            mFavPeopleItem.setFavProfilePic(favIcon[i]);
            favPeopleItemArrayList.add(i, mFavPeopleItem);
        }
    }

    private void initRecyclerView() {
        gridLayoutManager = new GridLayoutManager(getContext(),2);
        rvFavPeople.setHasFixedSize(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvFavPeople.setLayoutManager(gridLayoutManager);

        mFavPeopleAdapter = new FavPeopleAdapter(this.getContext(), favPeopleItemArrayList);
        mFavPeopleAdapter.setOnFavPeopleItemClickListener(this);
        rvFavPeople.setAdapter(mFavPeopleAdapter);
    }

    @Override
    public void onImageClick(View v, int position) {
        Snackbar.make(v, "Clicked!" + favPeopleItemArrayList.get(position).getFavName(), Snackbar.LENGTH_SHORT).show();
    }
}