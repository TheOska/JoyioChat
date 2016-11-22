package oska.joyiochat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import oska.joyiochat.R;
import oska.joyiochat.activity.FaceTrackerActivity;
import oska.joyiochat.activity.TestRajawaliActivity;
import oska.joyiochat.adapter.ChatContactAdapter;
import oska.joyiochat.adapter.FavPeopleAdapter;
import oska.joyiochat.module.ChatContactItem;
import oska.joyiochat.module.FavPeopleItem;
import oska.joyiochat.utils.SpacesItemDecoration;

/**
 * Created by TheOSka on 21/6/2016.
 */

public class ChatRoomFragment extends Fragment implements
        FavPeopleAdapter.OnFavPeopleItemClickListener,
        ChatContactAdapter.OnChatContactItemClickListener{

    @Bind(R.id.recycler_fav_people)
    RecyclerView rvFavPeople;

    @Bind(R.id.recycler_contact_chat)
    RecyclerView rvContactChat;

    private GridLayoutManager gridLayoutManager;
    private RecyclerView.LayoutManager mLayoutManager;

    private FavPeopleAdapter mFavPeopleAdapter;
    private ChatContactAdapter mChatContactAdapter;

    List<FavPeopleItem> favPeopleItemArrayList;
    List<ChatContactItem> chatContactIteamArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        ButterKnife.bind(this, view);
        initItemList();
        initFavContactRecyclerView();
        initChatContactRecyclerView();
        return view;
    }


    private void initItemList() {
        String[] favName = {"IU", "Leonardo", "Tony Stark", "Marry"};
        Integer[] favIcon = {R.drawable.fav_people_icon,
                R.drawable.fav_people_icon2,
                R.drawable.fav_people_icon5,
                R.drawable.fav_people_icon4
        };
        favPeopleItemArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            FavPeopleItem mFavPeopleItem = new FavPeopleItem();
            mFavPeopleItem.setFavName(favName[i]);
            mFavPeopleItem.setFavProfilePic(favIcon[i]);
            favPeopleItemArrayList.add(i, mFavPeopleItem);
        }

        chatContactIteamArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ChatContactItem mChatContactItem = new ChatContactItem();
            mChatContactItem.setChatContactProfilePic(favIcon[i]);
            mChatContactItem.setContactName(favName[i]);
            chatContactIteamArrayList.add(i, mChatContactItem);
        }

    }

    private void initFavContactRecyclerView() {

        int spacingInPixels = 1;

        gridLayoutManager = new GridLayoutManager(getContext(),2);
        rvFavPeople.setHasFixedSize(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvFavPeople.setLayoutManager(gridLayoutManager);

        mFavPeopleAdapter = new FavPeopleAdapter(this.getContext(), favPeopleItemArrayList);
        mFavPeopleAdapter.setOnFavPeopleItemClickListener(this);
        rvFavPeople.setAdapter(mFavPeopleAdapter);

        rvFavPeople.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    private void initChatContactRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rvContactChat.setLayoutManager(mLayoutManager);

        mChatContactAdapter = new ChatContactAdapter(this.getContext(), chatContactIteamArrayList);
        mChatContactAdapter.setOnChatContactClickListener(this);
        rvContactChat.setAdapter(mChatContactAdapter);
    }

    @Override
    public void onImageClick(View v, int position) {
        Snackbar.make(v, "Clicked!" + favPeopleItemArrayList.get(position).getFavName(), Snackbar.LENGTH_SHORT).show();
        getContext().startActivity(new Intent(getActivity(),FaceTrackerActivity.class));

    }

    @Override
    public void onChatContactImageClick(View v, int position) {
        Snackbar.make(v, "Clicked!" + chatContactIteamArrayList.get(position).getContactName(), Snackbar.LENGTH_SHORT).show();
        getContext().startActivity(new Intent(getActivity(),TestRajawaliActivity.class));
    }
}