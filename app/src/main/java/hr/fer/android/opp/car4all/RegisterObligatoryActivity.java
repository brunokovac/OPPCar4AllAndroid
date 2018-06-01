package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAO;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.User;

public class RegisterObligatoryActivity extends AppCompatActivity {

    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_obligatory);

        ButterKnife.bind(this);
    }

    @BindView(R.id.usernameField)
    EditText usernameField;

    @BindView(R.id.passwordField)
    EditText passwordField;

    @BindView(R.id.nameField)
    EditText nameField;

    @BindView(R.id.surnameField)
    EditText surnameField;

    @BindView(R.id.addressField)
    EditText addressField;

    @BindView(R.id.oibField)
    EditText oibField;

    @BindView(R.id.emailField)
    EditText emailField;

    private boolean isDataCorrect() {
        boolean correct = true;

        Drawable d = getResources().getDrawable(R.drawable.error);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String username = usernameField.getText().toString().trim();
        if (username.length() < 5) {
            usernameField.setError("Korisničko ime mora imati barem 5 znakova!", d);
            correct = false;
        } else if (usernameExists(username)) {
            usernameField.setError("Korisničko ime već postoji!", d);
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
        } else {
            if (oibExists(oib)) {
                oibField.setError("Osoba s navedenim OIB-om već postoji!", d);
                correct = false;
            }

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

    private Person processData() {
        user = new Person();

        user.setConfirmed(false);
        user.setUsername(usernameField.getText().toString().trim());
        user.setPassword(passwordField.getText().toString().trim());
        user.setName(nameField.getText().toString().trim());
        user.setSurname(surnameField.getText().toString().trim());
        user.setAddress(addressField.getText().toString().trim());
        user.setOIB(oibField.getText().toString().trim());
        user.setEmail(emailField.getText().toString().trim());
        user.setType(PersonType.USER);

        return user;
    }

    private boolean usernameExists(String username) {
        try {
            return DAOProvider.getDao().usernameExists(username);
        } catch (DAOException e) {
            Toast.makeText(RegisterObligatoryActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private boolean oibExists(String oib) {
        try {
            return DAOProvider.getDao().oibExists(oib);
        } catch (DAOException e) {
            Toast.makeText(RegisterObligatoryActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @OnClick(R.id.profilePicture1)
    void goToProfilePictureScreen(){
        if (isDataCorrect()) {
            processData();

            startActivity(new Intent(RegisterObligatoryActivity.this, RegisterProfilePictureActivity.class));
            ((MyApplication) getApplication()).setPerson(user);
        }
    }

}
