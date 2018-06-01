package hr.fer.android.opp.car4all;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Person;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.registerButton)
    void startRegistration() {
        startActivity(new Intent(LoginActivity.this, RegisterObligatoryActivity.class));
    }

    @BindView(R.id.username)
    EditText usernameField;

    @BindView(R.id.password)
    EditText passwordField;

    @BindView(R.id.loginError)
    TextView loginError;

    @OnClick(R.id.loginButton)
    void processLogin() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        try {
            if (!DAOProvider.getDao().isPersonFound(username, password)) {
                loginError.setVisibility(View.VISIBLE);
                return;
            }

            Person person = DAOProvider.getDao().getPersonForUsername(username);

            if (!person.isConfirmed() || person.isDeleted()){
                Toast.makeText(this, "Korisnik nije potvrđen ili je obrisan!", Toast.LENGTH_SHORT).show();
                return;
            }

            ((MyApplication) getApplication()).setPerson(person);
            Intent intent = null;

            switch (person.getType()) {
                case USER:
                    startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                    break;
                case MODERATOR:
                    startActivity(new Intent(LoginActivity.this, ModeratorHomeActivity.class));
                    break;
                case ADMINISTRATOR:
                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    break;
            }

        } catch (Exception exc) {
            Toast.makeText(LoginActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException(exc);
        }
    }

}
