package hr.fer.android.opp.car4all.models;

import java.io.Serializable;

public class Review implements Serializable {

    private int reviewID;

    private User user;
    private User reviewer;

    private int grade;
    private String comment;

    // 0 = nije obrisano | -x = nije obrisano, ali je pregledao x
    private int deletedBy =0;
    private boolean resultOfCancellation;

    public Review() {
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isResultOfCancellation() {
        return resultOfCancellation;
    }

    public void setResultOfCancellation(boolean resultOfCancellation) {
        this.resultOfCancellation = resultOfCancellation;
    }

    public int getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(int deletedBy) {
        this.deletedBy = deletedBy;
    }

}
