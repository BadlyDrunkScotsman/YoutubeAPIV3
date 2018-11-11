package app.com.YTBackPack.models.playlist;

import android.os.Parcelable;

import app.com.YTBackPack.models.YoutubeDataModel;
import app.com.YTBackPack.models.YoutubeDataType;

public abstract class FYoutubePlaylistDataModel extends YoutubeDataModel implements Parcelable {
    protected YoutubeDataType _DataType = YoutubeDataType.PLAYLIST;
    protected String _Title = "";
    protected String _Description = "";
    protected String _PublishedAt = "";
    protected String _Thumbnail = "";
    protected String _PlaylistId = "";
}
