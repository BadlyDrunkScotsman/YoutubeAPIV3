package app.com.YTBackPack.models;

import android.os.Parcel;
import android.os.Parcelable;

public class YoutubeCommentModel extends YoutubeDataModel implements Parcelable {

    private YoutubeDataType _DataType = YoutubeDataType.COMMENT;

    private String title = "";
    private String comment = "";
    private String publishedAt = "";
    private String thumbnail = "";
    private String video_id = "";

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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
        return null;
    }
    @Override
    public void setDescription(String description) {}

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
    public YoutubeDataType get_DataType() {
        return _DataType;
    }


/*
    @Override
    public String getplaylistId() {
        return null;
    }

    @Override
    public void setplaylistId(String value) {

    }
*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(comment);
        dest.writeString(publishedAt);
        dest.writeString(thumbnail);
        dest.writeString(video_id);
    }

    public YoutubeCommentModel() {
        super();
    }

    protected YoutubeCommentModel(Parcel in) {
        this();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.comment = in.readString();
        this.publishedAt = in.readString();
        this.thumbnail = in.readString();
        this.video_id = in.readString();

    }

    public static final Creator<YoutubeCommentModel> CREATOR = new Creator<YoutubeCommentModel>() {
        @Override
        public YoutubeCommentModel createFromParcel(Parcel in) {
            return new YoutubeCommentModel(in);
        }

        @Override
        public YoutubeCommentModel[] newArray(int size) {
            return new YoutubeCommentModel[size];
        }
    };
}
