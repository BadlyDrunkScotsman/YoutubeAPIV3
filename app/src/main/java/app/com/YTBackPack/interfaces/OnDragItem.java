package app.com.YTBackPack.interfaces;

import android.graphics.drawable.Drawable;
import android.view.View;

import app.com.YTBackPack.models.YoutubeDataModel;

public interface OnDragItem {
    void onDrag(View v, YoutubeDataModel item, Drawable drawable);
}
