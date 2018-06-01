package hr.fer.android.opp.car4all;

import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.ImageUtil;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.ProxyBitmap;
import hr.fer.android.opp.car4all.models.User;

public class RegisterProfilePictureActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    @BindView(R.id.registerProfilePictureImage)
    ImageView image;

    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_picture);

        ButterKnife.bind(this);

        user = ((MyApplication) getApplication()).getPerson();
    }

    @OnClick(R.id.chooseprofilePictureButton)
    void chooseProfilePicture(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Odaberite sliku");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(image);

            user.setProfilePicture(uri.toString());
        }
    }

    @OnClick(R.id.obligatory2)
    void goToObligatoryScreen(){
        onBackPressed();
    }

    @OnClick(R.id.optional2)
    void goToOptionalScreen(){
        if (user.getProfilePicture() != null){
            startActivity(new Intent(RegisterProfilePictureActivity.this, RegisterOptionalActivity.class));
        }else{
            Toast.makeText(RegisterProfilePictureActivity.this, "Obavezno je odabrati sliku profila!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.end2)
    void goToEndScreen(){
        if (user.getProfilePicture() != null){
            startActivity(new Intent(RegisterProfilePictureActivity.this, RegisterEndActivity.class));
        }else{
            Toast.makeText(RegisterProfilePictureActivity.this, "Obavezno je odabrati sliku profila!", Toast.LENGTH_SHORT).show();
        }
    }

}
