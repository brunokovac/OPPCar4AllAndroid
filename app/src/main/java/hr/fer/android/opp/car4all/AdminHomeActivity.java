package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.Administrator;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ButterKnife.bind(this);

        Administrator admin = (Administrator) ((MyApplication) getApplication()).getPerson();
        setupDrawerContent(navigation);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new AdminActiveUsersFragment()).commit();
    }

    @BindView(R.id.adminNavigation)
    NavigationView navigation;

    @BindView(R.id.adminDrawer)
    DrawerLayout drawerLayout;

    @OnClick(R.id.adminMenuButton)
    void showMenu(){
        drawerLayout.openDrawer(Gravity.START);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        switch(menuItem.getItemId()) {
            case R.id.adminCreatingModeratorsItem:
                fragment = new RegisterModeratorFragment();
                break;
            case R.id.adminChangingTypeItem:
                fragment = new AdminActiveUsersFragment();
                break;
            case R.id.adminNewUsersItem:
                fragment = new ModeratorNewUsersFragment();
                break;
            case R.id.adminAllUsersItem:
                fragment = new ModeratorAllUsersFragment();
                break;
            case R.id.adminReviewsItem:
                fragment = new ModeratorReviewsFragment();
                break;
            default:
                break;
        }

        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, fragment).commit();
        }

        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.adminLogoutButton)
    void logout(){
        startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        }
    }

}
