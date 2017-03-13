package oska.joyiochat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import oska.joyiochat.R;
import oska.joyiochat.adapter.ViewPagerAdapter;
import oska.joyiochat.utils.Utils;

/**
 * Created by theoska on 3/10/17.
 */

public class ChatRoomActivity extends AppCompatActivity {

    TabLayout tabLayout;


    ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs_layout);
        //BindView Function complete in BaseActivity
//        if (savedInstanceState == null) {
//            pendingOpenAppAnimation = true;
//        }
        initViewPager();
//        initToolbar();
        initTabs();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }
    private void initToolbar() {
//        setSupportActionBar(toolbar);
    }

    private void initViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void initTabs() {
        final TabLayout.Tab tab_chat = tabLayout.newTab();
        final TabLayout.Tab tab_emotion = tabLayout.newTab();

        tab_chat.setText("Chat");
        tab_emotion.setText("Emotion");

        tabLayout.addTab(tab_chat, 0);
        tabLayout.addTab(tab_emotion, 1);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.drawable.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // wait until option menu is created
//        if (pendingOpenAppAnimation) {
//            pendingOpenAppAnimation = false;
//            startOpenAppAnimation();
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }



}
