package app.com.YTBackPack.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.YTBackPack.DetailsActivity;
import app.com.YTBackPack.Encoding.EncodingUtil;
import app.com.YTBackPack.R;
import app.com.YTBackPack.Request.RequestYoutubeAPI;
import app.com.YTBackPack.adapters.LayoutVideoType;
import app.com.YTBackPack.adapters.ListViewAdapter;
import app.com.YTBackPack.interfaces.OnError;
import app.com.YTBackPack.models.YoutubeDataModel;
import app.com.YTBackPack.models.video.YoutubeVideoDataModel;

import static app.com.YTBackPack.Keys.GOOGLE_YOUTUBE_API_KEY;
/**
 * A simple {@link Fragment} subclass.
 */
public final class VideoFragment extends Fragment {

    private ProgressBar _Progress;
    private ProgressBar _nextPageloadingPanel;

    private static final String TAG = "VideoFragment";

    private static String SearchKey = "";
    private static boolean WindowMode = false;

    private static String _NextPageToken = "";

    private LinearLayout DropBackGround;
    private ImageView PlayShadow;
    private ImageView ShareShadow;
    private ImageView DownloadShadow;
    private ImageView SeeLaterShadow;


    private View view;
    private RecyclerView mList_videos = null;
    private ListViewAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        String SearchKey = args.getCharSequence("SearchKey").toString();
        boolean WindowMode = args.getBoolean("WindowMode");

        VideoFragment.WindowMode = WindowMode;
        VideoFragment.SearchKey = SearchKey;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_with_recycleview, container, false);
        mList_videos = (RecyclerView) view.findViewById(R.id.mList_values);
        _Progress = (ProgressBar) view.findViewById(R.id.loadingPanel);
        _nextPageloadingPanel = (ProgressBar) view.findViewById(R.id.nextPageloadingPanel);
        DropBackGround = (LinearLayout) view.findViewById(R.id.DropBackGround);
        PlayShadow = (ImageView) view.findViewById(R.id.PlayShadow);
        ShareShadow = (ImageView) view.findViewById(R.id.ShareShadow);
        DownloadShadow = (ImageView) view.findViewById(R.id.DownloadShadow);
        SeeLaterShadow = (ImageView) view.findViewById(R.id.SeeLaterShadow);

        loadFirstPage();
        return view;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (!WindowMode) {
            adapter = new ListViewAdapter(getActivity(), mListData, LayoutVideoType.BIG, item -> {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(YoutubeVideoDataModel.class.toString(), item);
                startActivity(intent);
            }, bottom -> getNextPage());
        }else {
            adapter = new ListViewAdapter(getActivity(), mListData, LayoutVideoType.SMALL, item -> {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(YoutubeVideoDataModel.class.toString(), item);
                startActivity(intent);
            }, bottom -> getNextPage());
        }
        mList_videos.setAdapter(adapter);
    }

    private void loadFirstPage(){
        new RequestYoutubeAPI(GET_SEARCH_URL(), json -> {
            Log.i("response", json.toString());
            mListData.addAll(parseVideoListFromResponse(json));

            _Progress.setVisibility(View.GONE);

            initList(mListData);
        }, new OnError() {
            @Override
            public void execute() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _Progress.setVisibility(View.GONE);
                        Log.e(TAG,"Error while requesting :"+GET_SEARCH_URL());
                        Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.content),
                                "Can't get first page", Snackbar.LENGTH_LONG);

                        mySnackbar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadFirstPage();
                            }
                        });
                        mySnackbar.show();
                    }
                });
            }
        }).execute();
    }

    private void getNextPage(){
        if (!_NextPageToken.isEmpty()) {

            _nextPageloadingPanel.setVisibility(View.VISIBLE);

            new RequestYoutubeAPI(GET_NEXT_PAGE_URL(), jsonObject -> {
                Log.i("response", jsonObject.toString());
                ArrayList<YoutubeDataModel> data = parseVideoListFromResponse(jsonObject);
                mListData.addAll(data);

                _nextPageloadingPanel.setVisibility(View.GONE);

                addDataToAdapter(data);
            },new OnError() {
                @Override
                public void execute() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _Progress.setVisibility(View.GONE);
                            Log.e(TAG, "Error while requesting :" + GET_SEARCH_URL());
                            Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.content),
                                    "Can't get next page", Snackbar.LENGTH_LONG);

                            mySnackbar.setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNextPage();
                                }
                            });
                            mySnackbar.show();
                        }
                    });
                }
            }).execute();
        }
    }

    private void addDataToAdapter(ArrayList<YoutubeDataModel> mListData){
        adapter.addItems(mListData);
    }

    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                if (jsonObject.has("nextPageToken"))
                    _NextPageToken = jsonObject.getString("nextPageToken");
                else
                    _NextPageToken = "";


                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeVideoDataModel youtubeObject = new YoutubeVideoDataModel();

                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setID(video_id);
                                mList.add(youtubeObject);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }

    private static String GET_SEARCH_URL(){
            if (SearchKey.isEmpty()) {
                return "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=zucc&type=video&key=" + GOOGLE_YOUTUBE_API_KEY + "";

            } else
                return "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&q=" + EncodingUtil.encodeURIComponent(SearchKey) + "&type=video&key=" + GOOGLE_YOUTUBE_API_KEY + "";
    }

    private static String GET_NEXT_PAGE_URL(){
        if (SearchKey.isEmpty()) {
            return "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&pageToken="+_NextPageToken+"&q=zucc&type=video&key=" + GOOGLE_YOUTUBE_API_KEY + "";
        } else
            return "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&pageToken="+_NextPageToken+"&q=" + EncodingUtil.encodeURIComponent(SearchKey) + "&type=video&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    }

}
