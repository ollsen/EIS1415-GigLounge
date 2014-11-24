package com.eis.transteinle.gigloungepoc;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by DerOlli on 24.11.2014.
 */
public class ServerRequest {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static List<Cookie> cookies = null;

    public ServerRequest(){

    }
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            BasicCookieStore cStore = new BasicCookieStore();
            cStore.addCookie(cookies.get(0));
            httpClient.setCookieStore(cStore);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            cookies = httpClient.getCookieStore().getCookies();
            System.out.println(cookies.size());
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public JSONObject getJSON(String url, List<NameValuePair> params) {
        Params param = new Params(url, params);
        Request myTask = new Request();
        try {
            jObj = myTask.execute(param).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return jObj;
    }
    private static class Params {
        String url;
        List<NameValuePair> params;
        Params(String url, List<NameValuePair> params) {
            this.url = url;
            this.params = params;
        }
    }
    private class Request extends AsyncTask<Params, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(Params... args) {
            ServerRequest request = new ServerRequest();
            JSONObject json = request.getJSONFromUrl(args[0].url,args[0].params);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
        }
    }
}
