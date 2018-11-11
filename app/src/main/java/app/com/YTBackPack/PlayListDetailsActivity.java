package app.com.YTBackPack;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import app.com.YTBackPack.Request.RequestYoutubeAPI;
import app.com.YTBackPack.adapters.CommentAdapter;
import app.com.YTBackPack.adapters.LayoutVideoType;
import app.com.YTBackPack.adapters.ListViewAdapter;
import app.com.YTBackPack.interfaces.OnItemClickListener;
import app.com.YTBackPack.models.YoutubeCommentModel;
import app.com.YTBackPack.models.YoutubeDataModel;
import app.com.YTBackPack.models.playlist.YoutubePlaylistDataModel;
import app.com.YTBackPack.models.video.YoutubeVideoDataModel;
import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

import static app.com.YTBackPack.Keys.GOOGLE_YOUTUBE_API_KEY;

public class PlayListDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
        public static String TAG = "DetailsActivity";

        private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
        private YoutubePlaylistDataModel youtubePlaylistDataModel = null;
        TextView textViewName;
        TextView textViewDes;
        TextView textViewDate;
        private YouTubePlayer mYoutubePlayer = null;
        private ArrayList<YoutubeCommentModel> mListCommentData = new ArrayList<>();

        private ImageView _WiewColapseButton;
        private ImageView _WiewColapsePlaylistButton;

        private LinearLayout _DescriptionLayout;
        private LinearLayout _PlayListLayout;

        private RecyclerView mList_comments = null;
        private RecyclerView mList_videos = null;
        private RecyclerView mList_Playlist_videos = null;

        private int rotationAngle = 0;
        private int rotationAngleOfPlayList = 0;

        private boolean _IsDescriptionCollapsed = true;
        private boolean _IsPlayListCollapsed = false;

        private YoutubeDataModel currentVideo;

        private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();
        private ArrayList<YoutubeDataModel> mList_Playlist_data = new ArrayList<>();

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mYoutubePlayer.setFullscreen(true);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                mYoutubePlayer.setFullscreen(false);
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play_list_details);
            youtubePlaylistDataModel = getIntent().getParcelableExtra(YoutubeVideoDataModel.class.toString());
            Log.i(TAG, youtubePlaylistDataModel.getDescription());

            YouTubePlayerView mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
            mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API_KEY, this);

            textViewName = (TextView) findViewById(R.id.textViewName);
            textViewDes = (TextView) findViewById(R.id.textViewDes);
            textViewDate = (TextView) findViewById(R.id.textViewDate);
            _WiewColapseButton = (ImageView) findViewById(R.id.WiewColapseButton);
            _DescriptionLayout = (LinearLayout) findViewById(R.id.DescriptionLayout);
            _WiewColapsePlaylistButton = (ImageView) findViewById(R.id.wiewColapseButtonPlaylist);
            _PlayListLayout = (LinearLayout) findViewById(R.id.mList_Playlist_videosLayout);


            textViewName.setText(youtubePlaylistDataModel.getTitle());
            textViewDes.setText(youtubePlaylistDataModel.getDescription());
            textViewDate.setText(youtubePlaylistDataModel.getPublishedAt());

            mList_comments = (RecyclerView) findViewById(R.id.mList_comments);
            mList_videos = (RecyclerView) findViewById(R.id.mList_videos);
            mList_Playlist_videos = (RecyclerView) findViewById(R.id.mList_Playlist_videos);

            new RequestYoutubeAPI(GET_PLAYLIST_ITEMS(), jsonObject -> {
                Log.i("response", jsonObject.toString());
                mList_Playlist_data.addAll(parsePlayListFromResponse(jsonObject));
                initPlaylist(mList_Playlist_data);

                if (!mList_Playlist_data.isEmpty())
                    new RequestYoutubeAPI(GET_SEARCH_URL_relatedToVideo(mList_Playlist_data.get(0).getID()), jsonSubObject -> {

                        currentVideo = mList_Playlist_data.get(0);

                        Log.i("response", jsonSubObject.toString());
                        mListData.addAll(parseVideoListFromResponse(jsonSubObject));
                        initList(mListData);
                    },null).execute();

            },null).execute();


            if (!checkPermissionForReadExternalStorage()) {
                try {
                    requestPermissionForReadExternalStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            _WiewColapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (_IsDescriptionCollapsed){
                        _DescriptionLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                        _IsDescriptionCollapsed = false;

                        ObjectAnimator anim = ObjectAnimator.ofFloat(_WiewColapseButton, "rotation",rotationAngle, rotationAngle + 180);
                        anim.setDuration(500);
                        anim.start();

                        rotationAngle += 180;
                        rotationAngle = rotationAngle%360;

                    }else {
                        _DescriptionLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0, 0));
                        _IsDescriptionCollapsed = true;

                        ObjectAnimator anim = ObjectAnimator.ofFloat(_WiewColapseButton, "rotation",rotationAngle, rotationAngle - 180);
                        anim.setDuration(500);
                        anim.start();

                        rotationAngle -= 180;
                        rotationAngle = rotationAngle%360;
                    }
                }
            });


            _WiewColapsePlaylistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_IsPlayListCollapsed){

                        LinearLayout.LayoutParams NLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

                        NLayout.setMargins(10,0,5,2);

                        _PlayListLayout.setLayoutParams(NLayout);
                        _IsPlayListCollapsed = false;

                        ObjectAnimator anim = ObjectAnimator.ofFloat(_WiewColapsePlaylistButton, "rotation",rotationAngle, rotationAngle + 180);
                        anim.setDuration(500);
                        anim.start();

                        rotationAngleOfPlayList += 180;
                        rotationAngleOfPlayList = rotationAngleOfPlayList%360;

                    }else {
                        _PlayListLayout.setLayoutParams(new LinearLayout.LayoutParams(0,0, 0));
                        _IsPlayListCollapsed = true;

                        ObjectAnimator anim = ObjectAnimator.ofFloat(_WiewColapsePlaylistButton, "rotation",rotationAngle, rotationAngle - 180);
                        anim.setDuration(500);
                        anim.start();

                        rotationAngleOfPlayList -= 180;
                        rotationAngleOfPlayList = rotationAngleOfPlayList%360;
                    }
                }
            });

        }

        private void initList(ArrayList<YoutubeDataModel> mListData) {
            try {
                mList_videos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                ListViewAdapter adapter = new ListViewAdapter(getApplicationContext(), mListData, LayoutVideoType.SMALL, new OnItemClickListener() {
                    @Override
                    public void onItemClick(YoutubeDataModel item) {
                        Intent intent = new Intent(getApplicationContext(), app.com.YTBackPack.DetailsActivity.class);
                        intent.putExtra(YoutubeVideoDataModel.class.toString(), item);
                        startActivity(intent);
                    }
                }, bottom ->
                        new RequestYoutubeAPI("https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId=" +
                                currentVideo.getID() +
                                "&key=" + GOOGLE_YOUTUBE_API_KEY,
                                jsonObject -> {
                                    Log.i("response", jsonObject.toString());
                                    mListCommentData = parseComments(jsonObject);
                                    initCommentList(mListCommentData);
                                }, null).execute());

                mList_videos.setNestedScrollingEnabled(false);
                mList_videos.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void initPlaylist(ArrayList<YoutubeDataModel> Data){
            try {
                mList_Playlist_videos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                ListViewAdapter adapter = new ListViewAdapter(getApplicationContext(), Data, LayoutVideoType.SMALL, new OnItemClickListener() {
                    @Override
                    public void onItemClick(YoutubeDataModel item) {
                        //ToDo start the clicked video
                        currentVideo = item;
                        mYoutubePlayer.cueVideo(item.getID());
                        new RequestYoutubeAPI(GET_SEARCH_URL_relatedToVideo(item.getID()), jsonObject -> {
                            Log.i("response", jsonObject.toString());

                            mListData.clear();

                            mListData.addAll(parseVideoListFromResponse(jsonObject));
                            initList(mListData);
                        },null).execute();
                    }
                }, bottom -> {
                    //ToDo start load next page
                });

                mList_Playlist_videos.setNestedScrollingEnabled(false);
                mList_Playlist_videos.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
            youTubePlayer.setPlaybackEventListener(playbackEventListener);
            if (!wasRestored) {
                youTubePlayer.cuePlaylist(youtubePlaylistDataModel.getID());
            }
            mYoutubePlayer = youTubePlayer;
        }

        private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onStopped() {
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        };

        private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        };

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }

        public void share_btn_pressed(View view) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String link = ("https://www.youtube.com/watch?v=" + currentVideo.getID());
            // this is the text that will be shared
            sendIntent.putExtra(Intent.EXTRA_TEXT, link);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, currentVideo.getTitle()
                    + "Share");

            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "share"));
        }

        public void downloadVideo(View view) {
            //get the download URL
            String youtubeLink = ("https://www.youtube.com/watch?v=" + youtubePlaylistDataModel.getID());
            YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {
                @Override
                public void onUrisAvailable(String videoID, String videoTitle, SparseArray<YtFile> ytFiles) {
                    if (ytFiles != null) {
                        int itag = 22;
                        //This is the download URL
                        String downloadURL = ytFiles.get(itag).getUrl();
                        Log.e("download URL :", downloadURL);

                        //now download it like a file
                        new app.com.YTBackPack.PlayListDetailsActivity.RequestDownloadVideoStream().execute(downloadURL, videoTitle);
                    }

                }
            };
            ytEx.execute(youtubeLink);
        }

        private ProgressDialog pDialog;

        private class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(app.com.YTBackPack.PlayListDetailsActivity.this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                InputStream is = null;
                URL u = null;
                int len1 = 0;
                int temp_progress = 0;
                int progress = 0;
                try {
                    u = new URL(params[0]);
                    is = u.openStream();
                    URLConnection huc = (URLConnection) u.openConnection();
                    huc.connect();
                    int size = huc.getContentLength();

                    if (huc != null) {
                        String file_name = params[1] + ".mp4";
                        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoutubeVideos";
                        File f = new File(storagePath);
                        if (!f.exists()) {
                            f.mkdir();
                        }

                        FileOutputStream fos = new FileOutputStream(f + "/" + file_name);
                        byte[] buffer = new byte[1024];
                        int total = 0;
                        if (is != null) {
                            while ((len1 = is.read(buffer)) != -1) {
                                total += len1;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                progress = (int) ((total * 100) / size);
                                if (progress >= 0) {
                                    temp_progress = progress;
                                    publishProgress("" + progress);
                                } else
                                    publishProgress("" + temp_progress + 1);

                                fos.write(buffer, 0, len1);
                            }
                        }

                        if (fos != null) {
                            publishProgress("" + 100);
                            fos.close();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                pDialog.setProgress(Integer.parseInt(values[0]));
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }

        public void initCommentList(ArrayList<YoutubeCommentModel> mListData) {
            mList_comments.setLayoutManager(new LinearLayoutManager(this));
            CommentAdapter mAdapter = new CommentAdapter(this, mListData);
            mList_comments.setNestedScrollingEnabled(false);
            mList_comments.setAdapter(mAdapter);
        }

        public ArrayList<YoutubeCommentModel> parseComments(JSONObject jsonObject) {
            ArrayList<YoutubeCommentModel> mList = new ArrayList<>();

            if (jsonObject.has("items")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);

                        YoutubeCommentModel youtubeObject = new YoutubeCommentModel();
                        JSONObject jsonTopLevelComment = json.getJSONObject("snippet").getJSONObject("topLevelComment");
                        JSONObject jsonSnippet = jsonTopLevelComment.getJSONObject("snippet");

                        String title = jsonSnippet.getString("authorDisplayName");
                        String thumbnail = jsonSnippet.getString("authorProfileImageUrl");
                        String comment = jsonSnippet.getString("textDisplay");

                        youtubeObject.setTitle(title);
                        youtubeObject.setComment(comment);
                        youtubeObject.setThumbnail(thumbnail);
                        mList.add(youtubeObject);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return mList;

        }

        public void requestPermissionForReadExternalStorage() throws Exception {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            READ_STORAGE_PERMISSION_REQUEST_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        public boolean checkPermissionForReadExternalStorage() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
            }
            return false;
        }

        public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
            ArrayList<YoutubeDataModel> mList = new ArrayList<>();

            if (jsonObject.has("items")) {
                try {
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
                                    String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");

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


    public ArrayList<YoutubeDataModel> parsePlayListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {

                JSONArray jsonArray = jsonObject.getJSONArray("items");

                Log.i(TAG, String.valueOf(jsonArray.length()));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                        if (json.has("kind")) {
                            if (json.getString("kind").equals("youtube#playlistItem")) {

                                YoutubePlaylistDataModel youtubeObject = new YoutubePlaylistDataModel();

                                JSONObject jsonSnippet = json.getJSONObject("snippet");

                                if (jsonSnippet.has("resourceId")){
                                    JSONObject jsonResourceID = jsonSnippet.getJSONObject("resourceId");
                                    youtubeObject.setID(jsonResourceID.getString("videoId"));
                                }

                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                mList.add(youtubeObject);
                            }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }

        private String GET_SEARCH_URL_relatedToVideo(String VideoID) {
            return "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=25&relatedToVideoId=" + VideoID + "&key="+GOOGLE_YOUTUBE_API_KEY;
        }

        private String GET_PLAYLIST_ITEMS(){
            Log.e(TAG,"--------------------------------------------------------"+youtubePlaylistDataModel.getID());
            return "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+youtubePlaylistDataModel.getID()+"&key="+GOOGLE_YOUTUBE_API_KEY;
        }

        private String GET_PLAYLIST_ITEMS_NEXTPAGE(){
            Log.e(TAG,"--------------------------------------------------------"+youtubePlaylistDataModel.getID());
            return "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+youtubePlaylistDataModel.getID()+"&key="+GOOGLE_YOUTUBE_API_KEY;
        }
    }


//https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId="+PlaylistId+"&key="+Key;