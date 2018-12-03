package com.dontletbehind.domain.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representation of a Task to be done.
 */
public class TaskEntity implements Parcelable {

    private Integer id;
    private String title;
    private String description;
    private Long timer;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    public TaskEntity(){}

    public TaskEntity(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        timer = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(timer);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskEntity> CREATOR = new Parcelable.Creator<TaskEntity>() {
        @Override
        public TaskEntity createFromParcel(Parcel in) {
            return new TaskEntity(in);
        }

        @Override
        public TaskEntity[] newArray(int size) {
            return new TaskEntity[size];
        }
    };
}
