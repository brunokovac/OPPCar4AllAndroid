package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

public class ModeratorCheckUserFragment extends Fragment {

    private Moderator moderator;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator_check_user, container, false);
        ButterKnife.bind(this, view);

        Person person = ((MyApplication) getActivity().getApplication()).getPerson();
        moderator = person instanceof Administrator ? ((Administrator) person).getModerator() : (Moderator) person;
        user = (User) ((MyApplication) getActivity().getApplication()).getPersonBeingChecked();
        setupData();

        return view;
    }

    @BindView(R.id.moderatorCheckUserProfilePictureImage)
    ImageView profilePicture;

    @BindView(R.id.moderatorCheckUserFullName)
    TextView fullName;

    @BindView(R.id.moderatorCheckUserUsernameField)
    TextView usernameField;

    @BindView(R.id.moderatorCheckUserAddressField)
    TextView addressField;

    @BindView(R.id.moderatorCheckUserEmailField)
    TextView emailField;

    @BindView(R.id.moderatorCheckUserWebpageField)
    TextView webpageField;

    @BindView(R.id.moderatorCheckUserFacebookField)
    TextView facebookField;

    @BindView(R.id.moderatorCheckUserLinkedInField)
    TextView linkedInField;

    private void setupData(){
        Glide.with(this).load(Uri.parse(user.getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);

        fullName.setText(String.format("%s %s (%s)", user.getName(), user.getSurname(), user.getOIB()));

        usernameField.setText(user.getUsername());
        addressField.setText(user.getAddress());
        emailField.setText(user.getEmail());
        webpageField.setText(user.getWebpage());
        facebookField.setText(user.getFacebook());
        linkedInField.setText(user.getLinkedIn());
    }

    @OnClick(R.id.moderatorCheckUserAcceptButton)
    void acceptUser(){
        try {
            moderator.acceptUser(user);
            sendEmail(user.getEmail(), true);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            Person person = ((MyApplication) getActivity().getApplication()).getPerson();
            if (person instanceof Administrator) {
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new ModeratorNewUsersFragment()).commit();
            }else{
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new ModeratorNewUsersFragment()).commit();
            }

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.moderatorCheckUserDeclineButton)
    void declineUser(){
        try{
           moderator.declineUser(user);
            sendEmail(user.getEmail(), false);

           FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

           Person person = ((MyApplication) getActivity().getApplication()).getPerson();
           if (person instanceof Administrator) {
               fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new ModeratorNewUsersFragment()).commit();
           }else{
               fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new ModeratorNewUsersFragment()).commit();
           }

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(String recipientEmail, boolean accepted){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",recipientEmail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Car4all - Registracija");

        String message = String.format("Poštovani,%n%nVaš zahtjev za pristupom u Car4All zajednicu je %s" +
                "%n%nCar4All moderator", accepted ? "prihvaćen" : "odbijen");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(intent, "Send report"));
    }

}
