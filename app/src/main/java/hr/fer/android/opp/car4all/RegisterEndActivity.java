package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

public class RegisterEndActivity extends AppCompatActivity {

    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_end);

        ButterKnife.bind(this);

        user = (Person) ((MyApplication) getApplication()).getPerson();
    }

    @OnClick(R.id.finalize)
    void finalizeRegistration() {
        try {
            DAOProvider.getDao().addPerson(user);
        } catch (Exception e) {
            Toast.makeText(RegisterEndActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(RegisterEndActivity.this, LoginActivity.class));
    }

    @OnClick(R.id.quitRegistration)
    void quitRegistration(){
        startActivity(new Intent(RegisterEndActivity.this, LoginActivity.class));
    }

    @Override
    public void onBackPressed() {
    }
}
