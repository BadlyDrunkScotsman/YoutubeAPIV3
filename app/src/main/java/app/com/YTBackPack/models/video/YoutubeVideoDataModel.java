package app.com.YTBackPack.models.video;

import android.os.Parcel;
import android.os.Parcelable;

import app.com.YTBackPack.models.YoutubeDataType;

public class YoutubeVideoDataModel extends FYoutubeVideoDataModel implements Parcelable {

    @Override
    public String getID() {
        return super._Video_ID;
    }

    @Override
    public void setID(String video_id) {
        super._Video_ID = video_id;
    }

    @Override
    public String getTitle() {
        return super._Title;
    }

    @Override
    public void setTitle(String title) {
        super._Title = title;
    }

    @Override
    public String getDescription() {
        return super._Description;
    }

    @Override
    public void setDescription(String description) {
        super._Description = description;
    }

    @Override
    public String getPublishedAt() {
        return super._PublishedAt;
    }

    @Override
    public void setPublishedAt(String publishedAt) {
        super._PublishedAt = publishedAt;
    }

    @Override
    public String getThumbnail() {
        return _Thumbnail;
    }

    @Override
    public void setThumbnail(String thumbnail) {
        super._Thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super._Title);
        dest.writeString(super._Description);
        dest.writeString(super._PublishedAt);
        dest.writeString(super._Thumbnail);
        dest.writeString(super._Video_ID);
    }

    @Override
    public YoutubeDataType get_DataType() {
        return super._DataType;
    }

    public YoutubeVideoDataModel() {
        super();
    }

    protected YoutubeVideoDataModel(Parcel in) {
        this();
        readFromParcel(in);
    }


    public void readFromParcel(Parcel in) {
        this._Title = in.readString();
        this._Description = in.readString();
        this._PublishedAt = in.readString();
        this._Thumbnail = in.readString();
        this._Video_ID = in.readString();

    }

    public static final Creator<YoutubeVideoDataModel> CREATOR = new Creator<YoutubeVideoDataModel>() {
        @Override
        public YoutubeVideoDataModel createFromParcel(Parcel in) {
            return new YoutubeVideoDataModel(in);
        }

        @Override
        public YoutubeVideoDataModel[] newArray(int size) {
            return new YoutubeVideoDataModel[size];
        }
    };
}
