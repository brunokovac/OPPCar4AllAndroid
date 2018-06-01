package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class EditProfileActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    private User user;

    @BindView(R.id.editProfilePictureImage)
    ImageView profilePicture;

    @BindView(R.id.editFullName)
    TextView fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        user = (User) ((MyApplication) getApplication()).getPerson();
        setupData();
    }

    @BindView(R.id.editOldPasswordField)
    EditText oldPasswordField;

    @BindView(R.id.editNewPasswordField)
    EditText newPasswordField;

    @BindView(R.id.editAddressField)
    EditText addressField;

    @BindView(R.id.editEmailField)
    EditText emailField;

    @BindView(R.id.editWebpageField)
    EditText webpageField;

    @BindView(R.id.editFacebookField)
    EditText facebookField;

    @BindView(R.id.editLinkedInField)
    EditText linkedInField;

    private void setupData(){
        previousPicture = user.getProfilePicture();

        Glide.with(this).load(Uri.parse(previousPicture))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);

        fullName.setText(String.format("%s %s (%s)", user.getName(), user.getSurname(), user.getUsername()));

        addressField.setText(user.getAddress());
        emailField.setText(user.getEmail());
        webpageField.setText(user.getWebpage());
        facebookField.setText(user.getFacebook());
        linkedInField.setText(user.getLinkedIn());
    }

    @OnClick(R.id.editChangePictureButton)
    void changeProfilePicture(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Odaberite novu sliku");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(profilePicture);

            user.setProfilePicture(uri.toString());
        }
    }

    @OnClick(R.id.editSaveChanges)
    void saveChanges(){
        if (!isDataCorrect()){
            return;
        }

        if (!newPasswordField.getText().toString().trim().isEmpty()){
            user.setPassword(newPasswordField.getText().toString().trim());
        }
        user.setAddress(addressField.getText().toString().trim());
        user.setEmail(emailField.getText().toString().trim());

        if (!webpageField.getText().toString().trim().isEmpty()) {
            user.setWebpage(webpageField.getText().toString().trim());
        }
        if (!facebookField.getText().toString().trim().isEmpty()){
            user.setFacebook(facebookField.getText().toString().trim());
        }
        if (!linkedInField.getText().toString().trim().isEmpty()){
            user.setLinkedIn(linkedInField.getText().toString().trim());
        }

        try {
            DAOProvider.getDao().editPerson(user);
        } catch (Exception e) {
            Toast.makeText(EditProfileActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }

        if (user instanceof Moderator) {
            startActivity(new Intent(EditProfileActivity.this, ModeratorHomeActivity.class));
        }else{
            startActivity(new Intent(EditProfileActivity.this, UserHomeActivity.class));
        }
    }

    private boolean isDataCorrect() {
        boolean correct = true;

        Drawable d = getResources().getDrawable(R.drawable.error);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String newPassword = newPasswordField.getText().toString().trim();
        String oldPassword = oldPasswordField.getText().toString().trim();
        if (!newPassword.isEmpty()) {
            if (!user.getPassword().equals(oldPassword)) {
                newPasswordField.setError("Stara lozinka nije ispravna!", d);
                correct = false;
            } else {
                if (newPassword.length() < 5) {
                    newPasswordField.setError("Lozinka mora imati barem 5 znakova!", d);
                    correct = false;
                }
            }
        }

        String address = addressField.getText().toString().trim();
        if (address.isEmpty()) {
            addressField.setError("Adresa mora biti upisana!", d);
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

    private String previousPicture;

    @OnClick(R.id.editDiscardChanges)
    void discardChanges(){
        user.setProfilePicture(previousPicture);
        onBackPressed();
    }

}
