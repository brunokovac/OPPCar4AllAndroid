package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.User;

public class RegisterOptionalActivity extends AppCompatActivity {

    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_optional);

        ButterKnife.bind(this);

        user = (Person) ((MyApplication) getApplication()).getPerson();
    }

    @BindView(R.id.webpageField)
    EditText webpageField;

    @BindView(R.id.facebookField)
    EditText facebookField;

    @BindView(R.id.linkedInField)
    EditText linkedInField;

    @OnClick(R.id.end3)
    void goToEndScreen() {
        String webpage = webpageField.getText().toString().trim();
        if (!webpage.isEmpty()) {
            user.setWebpage(webpage);
        }

        String facebook = facebookField.getText().toString().trim();
        if (!facebook.isEmpty()) {
            user.setFacebook(facebook);
        }

        String linkedIn = linkedInField.getText().toString().trim();
        if (!linkedIn.isEmpty()) {
            user.setLinkedIn(linkedIn);
        }

        startActivity(new Intent(RegisterOptionalActivity.this, RegisterEndActivity.class));
    }

    @Override
    public void onBackPressed() {
    }
}
