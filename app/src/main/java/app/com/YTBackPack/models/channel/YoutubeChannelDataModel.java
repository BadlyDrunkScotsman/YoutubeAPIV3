package app.com.YTBackPack.models.channel;

import android.os.Parcel;
import android.os.Parcelable;

import app.com.YTBackPack.models.YoutubeDataModel;
import app.com.YTBackPack.models.YoutubeDataType;

public class YoutubeChannelDataModel extends YoutubeDataModel implements Parcelable{

    private YoutubeDataType _DataType = YoutubeDataType.CHANNEL;

    private String title = "";
    private String description = "";
    private String publishedAt = "";
    private String thumbnail = "";
    private String video_id = "";
    private String channelTitle = "";

    @Override
    public String getID() {
        return video_id;
    }

    @Override
    public void setID(String video_id) {
        this.video_id = video_id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getPublishedAt() {
        return publishedAt;
    }

    @Override
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(publishedAt);
        dest.writeString(thumbnail);
        dest.writeString(video_id);
    }

    public YoutubeChannelDataModel() {
        super();
    }


    protected YoutubeChannelDataModel(Parcel in) {
        this();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.publishedAt = in.readString();
        this.thumbnail = in.readString();
        this.video_id = in.readString();

    }

    public static final Parcelable.Creator<YoutubeChannelDataModel> CREATOR = new Parcelable.Creator<YoutubeChannelDataModel>() {
        @Override
        public YoutubeChannelDataModel createFromParcel(Parcel in) {
            return new YoutubeChannelDataModel(in);
        }

        @Override
        public YoutubeChannelDataModel[] newArray(int size) {
            return new YoutubeChannelDataModel[size];
        }
    };

    @Override
    public YoutubeDataType get_DataType() {
        return _DataType;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }
}
