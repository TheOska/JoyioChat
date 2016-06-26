package oska.joyiochat.activity;

/**
 * Created by TheOSka on 20/6/2016.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import oska.joyiochat.R;
import oska.joyiochat.adapter.ViewPagerAdapter;
import oska.joyiochat.utils.Utils;

public class HomePageActivity  extends BaseDrawerActivity{


    //Declaring All The Variables Needed
//    @Bind(R.id.tool_bar)
//    Toolbar toolbar;

    @Bind(R.id.tabs_layout)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private boolean pendingOpenAppAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //BindView Function complete in BaseActivity
        if (savedInstanceState == null) {
            pendingOpenAppAnimation = true;
        }
        initViewPager();
//        initToolbar();
        initTabs();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }

    private void initToolbar() {setSupportActionBar(toolbar);}

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
        if (pendingOpenAppAnimation) {
            pendingOpenAppAnimation = false;
            startOpenAppAnimation();
        }
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

    private void startOpenAppAnimation() {
//        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getComehereLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getComehereLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .start();
    }


}
