package app.com.YTBackPack.models.playlist;

import android.os.Parcel;
import android.os.Parcelable;

import app.com.YTBackPack.models.YoutubeDataType;

public class YoutubePlaylistDataModel extends FYoutubePlaylistDataModel implements Parcelable{

    @Override
    public String getID() {
        return super._PlaylistId;
    }

    @Override
    public void setID(String playlistId) {
        super._PlaylistId = playlistId;
    }

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
        return super._Thumbnail;
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
        dest.writeString(super._PublishedAt);
        dest.writeString(super._Thumbnail);
        dest.writeString(super._PlaylistId);
    }

    public YoutubePlaylistDataModel() {
        super();
    }


    protected YoutubePlaylistDataModel(Parcel in) {
        this();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this._Title = in.readString();
        this._PublishedAt = in.readString();
        this._Thumbnail = in.readString();
        this._PlaylistId = in.readString();
    }

    public static final Parcelable.Creator<YoutubePlaylistDataModel> CREATOR = new Parcelable.Creator<YoutubePlaylistDataModel>() {
        @Override
        public YoutubePlaylistDataModel createFromParcel(Parcel in) {
            return new YoutubePlaylistDataModel(in);
        }

        @Override
        public YoutubePlaylistDataModel[] newArray(int size) {
            return new YoutubePlaylistDataModel[size];
        }
    };

    @Override
    public YoutubeDataType get_DataType() {
        return _DataType;
    }
}
