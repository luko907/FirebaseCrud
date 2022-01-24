package com.lucaskoch.firebasecrud.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseRVModel implements Parcelable {
    private String courseName;
    private String coursePrice;
    private String courseSuitedFor;
    private String courseImageLink;
    private String courseLink;
    private String courseDescription;
    private String courseID;

    public CourseRVModel() {
    }


    public CourseRVModel(String courseName, String coursePrice, String courseSuitedFor, String courseImageLink, String courseLink, String courseDescription, String courseID) {
        this.courseName = courseName;
        this.coursePrice = coursePrice;
        this.courseSuitedFor = courseSuitedFor;
        this.courseImageLink = courseImageLink;
        this.courseLink = courseLink;
        this.courseDescription = courseDescription;
        this.courseID = courseID;
    }

    protected CourseRVModel(Parcel in) {
        courseName = in.readString();
        coursePrice = in.readString();
        courseSuitedFor = in.readString();
        courseImageLink = in.readString();
        courseLink = in.readString();
        courseDescription = in.readString();
        courseID = in.readString();
    }

    public static final Creator<CourseRVModel> CREATOR = new Creator<CourseRVModel>() {
        @Override
        public CourseRVModel createFromParcel(Parcel in) {
            return new CourseRVModel(in);
        }

        @Override
        public CourseRVModel[] newArray(int size) {
            return new CourseRVModel[size];
        }
    };

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(String coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseSuitedFor() {
        return courseSuitedFor;
    }

    public void setCourseSuitedFor(String courseSuitedFor) {
        this.courseSuitedFor = courseSuitedFor;
    }

    public String getCourseImageLink() {
        return courseImageLink;
    }

    public void setCourseImageLink(String courseImageLink) {
        this.courseImageLink = courseImageLink;
    }

    public String getCourseLink() {
        return courseLink;
    }

    public void setCourseLink(String courseLink) {
        this.courseLink = courseLink;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(coursePrice);
        dest.writeString(courseSuitedFor);
        dest.writeString(courseImageLink);
        dest.writeString(courseLink);
        dest.writeString(courseDescription);
        dest.writeString(courseID);
    }
}
