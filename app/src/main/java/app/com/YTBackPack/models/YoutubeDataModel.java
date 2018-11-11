package app.com.YTBackPack.models;

import android.os.Parcelable;

public abstract class YoutubeDataModel implements Parcelable {

    public abstract String getID();

    public abstract void setID(String video_id);

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract String getPublishedAt();

    public abstract void setPublishedAt(String publishedAt);

    public abstract String getThumbnail();

    public abstract void setThumbnail(String thumbnail);

    public abstract YoutubeDataType get_DataType();

}
