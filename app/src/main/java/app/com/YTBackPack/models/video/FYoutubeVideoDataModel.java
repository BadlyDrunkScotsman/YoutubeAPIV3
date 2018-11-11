package app.com.YTBackPack.models.video;

import app.com.YTBackPack.models.YoutubeDataModel;
import app.com.YTBackPack.models.YoutubeDataType;

public abstract class FYoutubeVideoDataModel extends YoutubeDataModel {

    protected YoutubeDataType _DataType = YoutubeDataType.VIDEO;
    protected String _Video_ID;
    protected String _Title;
    protected String _Description;
    protected String _PublishedAt;
    protected String _Thumbnail;
}
