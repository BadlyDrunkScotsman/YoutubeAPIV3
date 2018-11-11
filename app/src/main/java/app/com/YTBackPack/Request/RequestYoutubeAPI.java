package app.com.YTBackPack.Request;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.com.YTBackPack.interfaces.OnError;
import app.com.YTBackPack.interfaces.OnSuccess;


public final class RequestYoutubeAPI extends AsyncTask<Void, String, String> {

    private String _ToRequest;
    private OnSuccess _Success;
    private OnError _OnError;

/*
    public RequestYoutubeAPI(String LinkToRequest, OnSuccess success){
        this._ToRequest = LinkToRequest;
        this._Success = success;
    }
    */

    public RequestYoutubeAPI(String LinkToRequest, OnSuccess success, OnError onError){
        this._ToRequest = LinkToRequest;
        this._Success = success;
        this._OnError = onError;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(_ToRequest);
        Log.i("URL", _ToRequest);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _OnError.execute();
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        _OnError.execute();
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                _Success.execute(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
