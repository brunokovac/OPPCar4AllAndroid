package hr.fer.android.opp.car4all;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Cancellation;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.Review;
import hr.fer.android.opp.car4all.models.User;

public class ReviewUserFragment extends Fragment {

    private User user;
    private User userBeingChecked;

    @BindView(R.id.reviewUserProfilePicture)
    ImageView profilePicture;

    @BindView(R.id.reviewUserUsernameField)
    TextView usernameField;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_user, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        userBeingChecked = (User) ((MyApplication) getActivity().getApplication()).getPersonBeingChecked();

        Glide.with(this).load(Uri.parse(userBeingChecked.getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        usernameField.setText(userBeingChecked.getUsername());

        return view;
    }

    @BindView(R.id.reviewUserRatingBar)
    RatingBar ratingBar;

    @BindView(R.id.reviewUserCommentField)
    EditText commentField;

    @BindView(R.id.reviewUserErrorMessage)
    TextView errorMessage;

    @OnClick(R.id.reviewUserSaveButton)
    void saveReview(){
        errorMessage.setVisibility(View.GONE);

        if (ratingBar.getRating() == 0){
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        int grade = (int) ratingBar.getRating();
        String comment = commentField.getText().toString().trim();
        if (comment.isEmpty()){
            comment = "-";
        }

        Review review = new Review();
        review.setUser(userBeingChecked);
        review.setReviewer(user);
        review.setGrade(grade);
        review.setComment(comment);
        review.setResultOfCancellation(resultOfCancellation);

        try{
            userBeingChecked.addReview(review);

            Cancellation cancellation = ((MyApplication)getActivity().getApplication()).getCancellationBeingChecked();
            if (cancellation != null){
                DAOProvider.getDao().checkCancellation(cancellation);

                if (user instanceof Moderator) {
                    ((ModeratorHomeActivity) getActivity()).checkForNewCancellations();
                }else{
                    ((UserHomeActivity) getActivity()).checkForNewCancellations();
                }
            }

            getActivity().onBackPressed();
        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean resultOfCancellation;

    public void setResultOfCancellation(boolean resultOfCancellation) {
        this.resultOfCancellation = resultOfCancellation;
    }

    public boolean isResultOfCancellation() {
        return resultOfCancellation;
    }

}
