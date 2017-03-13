package oska.joyiochat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;


import butterknife.BindView;
import oska.joyiochat.R;
import oska.joyiochat.utils.Utils;


public class MainActivity extends BaseDrawerActivity{

    private static final int ANIM_DURATION_TOOLBAR = 300;

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;

    @BindView(R.id.content)
    CoordinatorLayout clContent;


    private boolean pendingOpenAppAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("flow", "before set ContentView");
        setContentView(R.layout.activity_main);
        Log.v("flow", "after set ContentView");
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

    }

}