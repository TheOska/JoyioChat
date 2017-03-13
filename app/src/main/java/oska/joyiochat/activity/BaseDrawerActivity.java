package oska.joyiochat.activity;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import oska.joyiochat.R;
import oska.joyiochat.utils.CircleTransformation;

/**
 * Created by Miroslaw Stanek on 15.07.15.
 */
public class BaseDrawerActivity extends BaseActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
    @BindString(R.string.user_profile_photo)
    String profilePhoto;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;

    @Override
    public void setContentView(int layoutResID) {
        //set the
        Log.v("flow","before super inject");
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        Log.v("flow","after super inject");
        // view group in the navigation drawer
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        //Instantiates a layout XML file into its corresponding View objects.
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
        setupHeader();
    }

    // get from baseActivity
    // give responsibility to the toolbar
    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    // the nav bar header

    /*
     * vNavigation.getHeaderView(0);
     * Gets the header view at the specified position.
     *
     *  <int> The position at which to get the view from.
     * return he header view the specified position or null if the position does not exist in this
     * NavigationView.
     */
    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

    /*
    *startingLocation
    *<p>Computes the coordinates of this view on the screen. The argument
    * must be an array of two integers. After the method returns, the array
    * contains the x and y location in that order.</p>
    *
    * @param location an array of two integers in which to hold the coordinates
    */
    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int[] startingLocation = new int[2];
//                v.getLocationOnScreen(startingLocation);
//                // obtained LocationOnScreen in startingLocation array
//                startingLocation[0] += v.getWidth() / 2;
//                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
//                /*
//                * 	overridePendingTransition(int enterAnim, int exitAnim)
//                *
//                *   Call immediately after one of the flavors of startActivity(Intent) or finish()
//                *   to specify an explicit transition animation to perform next.
//                * */
//                overridePendingTransition(0, 0);
//            }
//        }, 200);
    }

}
