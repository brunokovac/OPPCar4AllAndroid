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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Cancellation;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

public class ModeratorHomeActivity extends AppCompatActivity {

    private Moderator moderator;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_home);

        ButterKnife.bind(this);

        moderator = (Moderator) ((MyApplication) getApplication()).getPerson();
        setupDrawerContent(navigation);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new ModeratorNewUsersFragment()).commit();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkForNewMessages();
                checkForNewCancellations();
            }
        };
        long interval = 55*1000;
        timer = new Timer();
        timer.scheduleAtFixedRate(task, interval, interval);
    }

    @BindView(R.id.moderatorNavigation)
    NavigationView navigation;

    @BindView(R.id.moderatorDrawer)
    DrawerLayout drawerLayout;

    @OnClick(R.id.moderatorMenuButton)
    void showMenu(){
        drawerLayout.openDrawer(Gravity.START);
    }

    private int messagesLastNumber = 0;

    public void checkForNewMessages(){
        List<Journey> newMessagesJourneys = DAOProvider.getDao().getNewMessageJourneysForUser(moderator);
        ((MyApplication) getApplication()).setNewMessagesJourneys(newMessagesJourneys);

        final int number = newMessagesJourneys.size();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigation.getMenu().findItem(R.id.moderatorMessagesItem).setTitle(String.format("Pregled dobivenih poruka (%d)", number));
            }
        });

        if (messagesLastNumber < number && number != 0){
            messagesLastNumber = number;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ModeratorHomeActivity.this, "Imate nove poruke!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int cancellationsLastNumber = 0;

    public void checkForNewCancellations(){
        List<Cancellation> newCancellations = moderator.getCancellations();
        ((MyApplication) getApplication()).setNewCancellations(newCancellations);

        final int number = newCancellations.size();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navigation.getMenu().findItem(R.id.moderatorCancellationsItem).setTitle(String.format("Otkazivanja putovanja ili zahtjeva (%d)", number));
            }
        });

        if (cancellationsLastNumber < number && number != 0){
            cancellationsLastNumber = number;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ModeratorHomeActivity.this, "Imate nova otkazivanja!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        checkForNewMessages();
        checkForNewCancellations();

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
            case R.id.newUsersItem:
                fragment = new ModeratorNewUsersFragment();
                break;
            case R.id.allUsersItem:
                fragment = new ModeratorAllUsersFragment();
                break;
            case R.id.reviewsItem:
                fragment = new ModeratorReviewsFragment();
                break;
            case R.id.moderatorOfferJourneyItem:
                fragment = new OfferJourneyObligatoryFragment();
                break;
            case R.id.moderatorFindJourneyItem:
                fragment = new FindJourneyFragment();
                break;
            case R.id.moderatorRateLastJourneyItem:
                rateLastJourneyAction();
                break;
            case R.id.moderatorOfferedJourneysItem:
                fragment = new OfferedJourneysFragment();
                break;
            case R.id.moderatorJourneyRequestsItem:
                fragment = new RequestsFragment();
                break;
            case R.id.moderatorAttendedJourneysItem:
                fragment = new AttendedJourneysFragment();
                break;
            case R.id.moderatorCancellationsItem:
                fragment = new CancellationsFragment();
                break;
            case R.id.moderatorMessagesItem:
                fragment = new NewMessagesJourneysFragment();
                break;
            default:
                break;
        }

        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, fragment).commit();
        }

        drawerLayout.closeDrawers();
    }

    private void rateLastJourneyAction(){
        try {
            List<Journey> allJourneys = moderator.getAttendedJourneys();
            List<Journey> journeys = new ArrayList<>();

            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            for (Journey journey : allJourneys){
                if (journey.getStartingDate().compareTo(cal.getTime()) < 0){
                    journeys.add(journey);
                    break;
                }
            }

            if (!journeys.isEmpty()) {
                ((MyApplication) getApplication()).setJourney(journeys.get(0));

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new JourneyDetailsReviewFragment()).commit();
            } else {
                Toast.makeText(this, "Nije pronađeno niti jedno putovanje!", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception exc){
            Toast.makeText(this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.moderatorMyProfileButton)
    void goToEditProfileScreen(){
        startActivity(new Intent(ModeratorHomeActivity.this, EditProfileActivity.class));
    }

    @OnClick(R.id.moderatorLogoutButton)
    void logout(){
        timer.cancel();
        startActivity(new Intent(ModeratorHomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        }
    }

}
