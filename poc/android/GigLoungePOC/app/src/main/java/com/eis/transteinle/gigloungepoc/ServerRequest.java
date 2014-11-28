package com.eis.transteinle.gigloungepoc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by DerOlli on 24.11.2014.
 */
public class ServerRequest {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static List<Cookie> cookies;
    Context ctx;
    static SharedPreferences pref;

    private static ProgressDialog pDialog;

    private static final String COOKIE_NAME = "cookieName";
    private static final String COOKIE_VALUE = "cookieValue";
    private static final String COOKIE_DOMAIN = "cookieDomain";

    private static final String domain = "192.168.178.55:3000";

    public ServerRequest(Context c){
        ctx = c;
    }
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            System.out.println(httpClient.getCookieStore().getCookies());
            if(cookies == null)
                cookies = new ArrayList<Cookie>();
            if(cookies.size() == 0) {
                if(pref.contains(COOKIE_NAME)){
                    //HttpCookie httpCookie = new HttpCookie(pref,pref.getString("cookies",""));
                    BasicClientCookie cookie = new BasicClientCookie(pref.getString(COOKIE_NAME,""),
                            pref.getString(COOKIE_VALUE,""));
                    cookie.setDomain(pref.getString(COOKIE_DOMAIN, ""));
                    cookie.setPath("/");
                    cookies.add(cookie);
                    Log.d("cookiePref",cookie.toString());
                    BasicCookieStore cStore = new BasicCookieStore();
                    cStore.addCookie(cookies.get(0));
                    httpClient.setCookieStore(cStore);
                }
                //Log.d("Cookie","name: "+cookies.get(0).getName());
            } else {
                BasicCookieStore cStore = new BasicCookieStore();
                cStore.addCookie(cookies.get(0));
                httpClient.setCookieStore(cStore);
            }
            HttpResponse httpResponse;
            if(params == null) {
                HttpGet httpGet = new HttpGet("http://"+domain+url);
                httpResponse = httpClient.execute(httpGet);
            } else {
                HttpPost httpPost = new HttpPost("http://"+domain+url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                httpResponse = httpClient.execute(httpPost);
            }
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            cookies = httpClient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(COOKIE_NAME,cookies.get(i).getName());
                    edit.putString(COOKIE_VALUE,cookies.get(i).getValue());
                    edit.putString(COOKIE_DOMAIN,cookies.get(i).getDomain());
                    edit.commit();
                    Log.d("pref",pref.getString(COOKIE_NAME,""));
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
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
        myTask.execute(param);
        /*try {
            jObj = myTask.execute(param).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
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
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Params... args) {
            ServerRequest request = new ServerRequest(ctx);
            JSONObject json = request.getJSONFromUrl(args[0].url, args[0].params);
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

        }
    }
}
