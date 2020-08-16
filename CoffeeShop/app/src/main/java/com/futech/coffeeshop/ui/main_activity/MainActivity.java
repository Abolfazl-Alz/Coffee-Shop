package com.futech.coffeeshop.ui.main_activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.register.RegisterData;
import com.futech.coffeeshop.service.NotificationService;
import com.futech.coffeeshop.ui.home.HomeFragment;
import com.futech.coffeeshop.ui.login.LoginActivity;
import com.futech.coffeeshop.utils.Internet;
import com.futech.coffeeshop.utils.RegisterControl;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;


@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NotificationService mService;
    private Toolbar mToolbar;
    private ConstraintLayout mAppBarMain;
    private TextView userPhone;
    private MenuItem adminMenu;
    private MenuItem orderAdminMenu;
    private TextView userFullName;

    boolean doubleBackToExitPressedOnce = false;

    private HomeFragment homeFragment;

    private final String TAG = "MainActivity";

    private int mOldLeftPadding;
    private int mOldRightPadding;
    private int mOldTopPadding;

    public static final int HOME_REQUEST = 0;
    public static final int EDIT_ACCOUNT_REQUEST = 1;
    public static final int ADMIN_PAGE_REQUEST = 2;
    public static final int ADMIN_ORDER_REQUEST = 3;
    public static final int ORDER_HISTORY_REQUEST = 4;
    public static final int CART_REQUEST = 5;

    public void setHomeFragment(boolean homeFragment) {
        isHomeFragment = homeFragment;
    }

    private boolean isHomeFragment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String languageToLoad = "fa";
        setAppLocale(languageToLoad);

//        recreate();
        setContentView(R.layout.activity_main);

        if (!checkIsLogin()) return;

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerMenu = navigationView.getHeaderView(0);

        userPhone = headerMenu.findViewById(R.id.userEmail);
        userFullName = headerMenu.findViewById(R.id.customer_name);


        mToolbar = findViewById(R.id.toolbar);
        mAppBarMain = findViewById(R.id.content_main);

        mOldTopPadding = mAppBarMain.getPaddingTop();
        mOldLeftPadding = mAppBarMain.getPaddingLeft();
        mOldRightPadding = mAppBarMain.getPaddingRight();

        setSupportActionBar(mToolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_history,
                R.id.nav_edituser, R.id.nav_admin_user, R.id.nav_admin_order, R.id.nav_cart,
                R.id.nav_history, R.id.nav_posts).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        adminMenu = navigationView.getMenu().findItem(R.id.nav_admin_user);
        orderAdminMenu = navigationView.getMenu().findItem(R.id.nav_admin_order);

        MenuCompat.setGroupDividerEnabled(navigationView.getMenu(), true);


        updateUserInformation();

        RegisterControl.UpdateData(this, new RegisterControl.RegisterRequestResultListener() {
            @Override
            public void success() {
                updateUserInformation();
            }

            @Override
            public void error(String error, int errorCode) {
            }
        });

        mService = new NotificationService();
        mService.startSystem(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            final Bundle extras = getIntent().getExtras();
            int request = extras.getInt("request");
            if (request == ADMIN_ORDER_REQUEST) {
                navController.navigate(R.id.nav_admin_order);
            }
        }
    }

    private void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    private void updateUserInformation() {
        final RegisterData registerData = RegisterControl.getRegisterData(this);

        String phoneNumber = "";
        phoneNumber += registerData.getPhoneNumber().substring(0, 4) + "-";
        phoneNumber += registerData.getPhoneNumber().substring(4, 7) + "-";
        phoneNumber += registerData.getPhoneNumber().substring(7, 11);

        userPhone.setText(phoneNumber);

        if (RegisterControl.isAdmin(this)) {
            userPhone.append(getString(R.string.sidebar_admin_label));
            adminMenu.setVisible(true);
            orderAdminMenu.setVisible(true);
        }


        if (registerData.getFullName().equals("")) {
            userFullName.setText(R.string.name_undefined);
        }else {
            userFullName.setText(registerData.getFullName());
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || !isHomeFragment || (homeFragment != null && homeFragment.isItemOpen())) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.double_click_back, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) mService.stopSystem(this);
    }

    private boolean checkIsLogin() {
        final boolean login = RegisterControl.isLogin(this);
        if (!login) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return login;
    }

    public void setToolbarColor(@ColorInt int color) {
        mToolbar.setBackgroundColor(color);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController,
                mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    //on view changing
    @Override
    public void onDestinationChanged(
            @NonNull NavController controller,
            @NonNull NavDestination destination, @Nullable Bundle arguments)
    {

        isHomeFragment = false;

        if (!Internet.isConnected(this))
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();


        setToolbarColor(getColorResource(R.color.home_background));

        mToolbar.setTitleTextColor(getColorResource(android.R.color.black));
        setTitle(R.string.app_name);

        final NavDestination label = controller.getCurrentDestination();
        if (label != null) {
            final int id = label.getId();
            if (id == R.id.nav_categories) {
                mAppBarMain.setPadding(0, 0, 0, 0);
            }else if (id == R.id.nav_home) {
                isHomeFragment = true;
            }else {
                mAppBarMain.setPadding(mOldLeftPadding, mOldTopPadding, mOldRightPadding, 0);
            }
        }
    }

    private int getColorResource(@ColorRes int colorResource) {
        try {
            return ResourcesCompat.getColor(this.getResources(), colorResource, getTheme());
        } catch (Resources.NotFoundException ex) {
            return 0x00000000;
        }
    }

    public void setToolbarIcon(@DrawableRes int ic_user) {
        mToolbar.setNavigationIcon(ic_user);
        mToolbar.setCollapseIcon(ic_user);
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }
}
