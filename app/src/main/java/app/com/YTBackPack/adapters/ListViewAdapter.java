package app.com.YTBackPack.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.YTBackPack.R;
import app.com.YTBackPack.adapters.drag.ImageDragShadowBuilder;
import app.com.YTBackPack.interfaces.OnBottomReachedListener;
import app.com.YTBackPack.interfaces.OnItemClickListener;
import app.com.YTBackPack.models.YoutubeDataModel;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.YoutubePostHolder> {

    private ArrayList<YoutubeDataModel> dataSet;
    private Context mContext = null;

    private LayoutVideoType _layoutVideoType;

    private OnBottomReachedListener onBottomReachedListener;
    private final OnItemClickListener listener;

    //boolean hasChannelThumbnail

    public ListViewAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, LayoutVideoType LayoutPerVideo, OnItemClickListener listener, OnBottomReachedListener onBottomReachedListener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.listener = listener;
        this.onBottomReachedListener = onBottomReachedListener;
        this._layoutVideoType = LayoutPerVideo;
    }

    public void addItems(ArrayList<YoutubeDataModel> dataSets) {
        dataSet.addAll(dataSets);
        super.notifyDataSetChanged();
    }

    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);

        switch (this._layoutVideoType){
            case BIG:
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);
                break;
            }
            case BIG2:
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_big,parent,false);
                break;
            }
            case SMALL:
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_small,parent,false);
                break;
            }
        }

        return new YoutubePostHolder(view);
    }

    @Override
    public void onBindViewHolder(YoutubePostHolder holder, int position) {
        //set the views here
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDes = holder.textViewDes;
        TextView textViewDate = holder.textViewDate;
        ImageView ImageThumb = holder.ImageThumb;

        YoutubeDataModel object = dataSet.get(position);

        textViewTitle.setText(object.getTitle());
        textViewDes.setText(object.getDescription());
        textViewDate.setText(object.getPublishedAt());
        holder.bind(dataSet.get(position), listener);

        Picasso.with(mContext).load(object.getThumbnail()).into(ImageThumb);

        if (position == dataSet.size() - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class YoutubePostHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewDes;
        TextView textViewDate;
        ImageView ImageThumb;

        YoutubePostHolder(View itemView) {
            super(itemView);

            itemView.setTag(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.textViewDes = (TextView) itemView.findViewById(R.id.textViewDes);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            this.ImageThumb = (ImageView) itemView.findViewById(R.id.ImageThumb);

        }

        void bind(final YoutubeDataModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData.Item Item = new ClipData.Item(v.getTag().toString());

                    ClipData dragData = new ClipData(
                            v.getTag().toString(),
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                            Item);

                    View.DragShadowBuilder myShadow = ImageDragShadowBuilder.fromResource(ImageThumb.getDrawable());

                    v.startDrag(dragData,  // the data to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local data
                            0          // flags (not currently used, set to 0)
                    );
                    return false;
                }
            });
        }
    }
}
