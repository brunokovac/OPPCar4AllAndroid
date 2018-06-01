package hr.fer.android.opp.car4all;

import android.content.DialogInterface;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.User;

public class RegisterModeratorFragment extends Fragment {

    private Administrator admin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_moderator, container, false);
        ButterKnife.bind(this, view);

        admin = (Administrator) ((MyApplication) getActivity().getApplication()).getPerson();

        return view;
    }

    @BindView(R.id.registerModeratorUsernameField)
    EditText usernameField;

    @BindView(R.id.registerModeratorPasswordField)
    EditText passwordField;

    @BindView(R.id.registerModeratorNameField)
    EditText nameField;

    @BindView(R.id.registerModeratorSurnameField)
    EditText surnameField;

    @BindView(R.id.registerModeratorAddressField)
    EditText addressField;

    @BindView(R.id.registerModeratorOibField)
    EditText oibField;

    @BindView(R.id.registerModeratorEmailField)
    EditText emailField;

    private boolean isDataCorrect() {
        boolean correct = true;

        Drawable d = getResources().getDrawable(R.drawable.error);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String username = usernameField.getText().toString().trim();
        if (username.length() < 5) {
            usernameField.setError("Korisničko ime mora imati barem 5 znakova!", d);
            correct = false;
        }

        String password = passwordField.getText().toString().trim();
        if (password.length() < 5) {
            passwordField.setError("Lozinka mora imati barem 5 znakova!", d);
            correct = false;
        }

        String name = nameField.getText().toString().trim();
        if (name.isEmpty()) {
            nameField.setError("Ime mora biti upisano!", d);
            correct = false;
        }

        String surname = surnameField.getText().toString().trim();
        if (surname.isEmpty()) {
            surnameField.setError("Prezime mora biti upisano!", d);
            correct = false;
        }

        String address = addressField.getText().toString().trim();
        if (address.isEmpty()) {
            addressField.setError("Adresa mora biti upisana!", d);
            correct = false;
        }

        String oib = oibField.getText().toString().trim();
        if (oib.length() != 11) {
            oibField.setError("OIB se mora sastojati od točno 11 znamenki!", d);
            correct = false;
        }

        String email = emailField.getText().toString().trim();
        if (email.isEmpty()) {
            emailField.setError("E-mail adresa mora biti upisana!", d);
            correct = false;
        } else {
            if (!email.contains("@")) {
                emailField.setError("Neispravan format e-mail adrese!", d);
                correct = false;
            }
        }

        return correct;
    }

    private Moderator moderator;

    private Person processData() {
        moderator = new Moderator();

        moderator.setConfirmed(true);
        moderator.setDeleted(false);
        moderator.setType(PersonType.MODERATOR);

        moderator.setUsername(usernameField.getText().toString().trim());
        moderator.setPassword(passwordField.getText().toString().trim());
        moderator.setName(nameField.getText().toString().trim());
        moderator.setSurname(surnameField.getText().toString().trim());
        moderator.setAddress(addressField.getText().toString().trim());
        moderator.setOIB(oibField.getText().toString().trim());
        moderator.setEmail(emailField.getText().toString().trim());
        moderator.setProfilePicture("");

        return moderator;
    }

    private boolean usernameExists(String username) {
        try {
            return DAOProvider.getDao().usernameExists(username);
        } catch (DAOException e) {
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @OnClick(R.id.registerModeratorAddButton)
    void addNewModerator(){
        try{
            if (isDataCorrect()){
                processData();

                if (usernameExists(moderator.getUsername())){
                    showAlreadyExistsDialog();
                }else{
                    DAOProvider.getDao().addPerson(moderator);
                    getActivity().onBackPressed();
                }
            }
        }catch (Exception exc){
            Log.d("greska1", exc.getMessage());
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlreadyExistsDialog(){
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(getActivity())
                        .setTitle("Korisnik već postoji!")
                        .setMessage(String.format("%s%n%n%s", "Korisnik sa zadanim korisničkim imenom i/ili OIB-om već postoji.",
                                "Želite li mu promijeniti ovlasti na moderatorske?"))
                        .setPositiveButton("PROMIJENI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    User oldUser = (User) DAOProvider.getDao().getPersonForUsername(moderator.getUsername());
                                    admin.changeAuthority(oldUser, PersonType.MODERATOR);
                                    Toast.makeText(getActivity(), "Ovlasti su promijenjene!", Toast.LENGTH_SHORT).show();

                                }catch (Exception exc){
                                    Log.d("greska2", exc.getMessage());
                                    Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                                }

                                dialog.cancel();
                                getActivity().onBackPressed();
                            }
                        })
                        .setNegativeButton("ODUSTANI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                getActivity().onBackPressed();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.show();
    }

}
