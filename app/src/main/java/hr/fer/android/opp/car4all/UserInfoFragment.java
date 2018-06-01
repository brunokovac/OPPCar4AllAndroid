package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Review;
import hr.fer.android.opp.car4all.models.User;

public class UserInfoFragment extends Fragment {

    private User userBeingChecked;

    @BindView(R.id.userInfoProfilePicture)
    ImageView profilePicture;

    @BindView(R.id.userInfoUsernameField)
    TextView usernameField;

    @BindView(R.id.userInfoReviewsList)
    ListView reviewsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, view);

        userBeingChecked = (User) ((MyApplication) getActivity().getApplication()).getPersonBeingChecked();
        setupData();

        return view;
    }

    @BindView(R.id.userInfoReviewsTitle)
    TextView reviewsTitle;

    private void setupData(){
        Glide.with(this).load(Uri.parse(userBeingChecked.getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        usernameField.setText(userBeingChecked.getUsername());

        List<Review> reviews = userBeingChecked.getReviews();
        if (reviews.isEmpty()){
            reviewsTitle.setText(String.format("%s %s", reviewsTitle.getText().toString(), "(korisnik nikad nije recenziran)"));
        }

        ArrayAdapter<Review> adapter = new ReviewArrayAdadpter(getActivity(), reviews);
        reviewsList.setAdapter(adapter);
    }

}
