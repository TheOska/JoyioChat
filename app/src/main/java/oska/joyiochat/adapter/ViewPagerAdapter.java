package oska.joyiochat.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

import oska.joyiochat.R;
import oska.joyiochat.fragment.ChatRoomFragment;
import oska.joyiochat.fragment.TabFragment2;

/**
 * Created by Admin on 11-12-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new ChatRoomFragment();
        if(position == 1)
            return new TabFragment2();
        else
            return null;
    }

    @Override
    public int getCount() {
        return 2;           // As there are only 3 Tabs
    }

}
