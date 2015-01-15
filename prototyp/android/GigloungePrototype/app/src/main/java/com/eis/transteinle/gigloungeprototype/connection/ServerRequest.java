package com.eis.transteinle.gigloungeprototype.connection;

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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    private DefaultHttpClient httpClient;

    private static ProgressDialog pDialog;

    private static final String COOKIE_NAME = "cookieName";
    private static final String COOKIE_VALUE = "cookieValue";
    private static final String COOKIE_DOMAIN = "cookieDomain";

    private static String domain = "h2192129.stratoserver.net:3000";

    public ServerRequest(Context ctx){
        this.ctx = ctx;
        pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        domain = pref.getString("URL","");
        httpClient = new DefaultHttpClient();
        setCookies();
    }
    public void setCookies() {
        cookies = new ArrayList<Cookie>();
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
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public JSONObject getJSONFromUrl(String path, List<NameValuePair> params){
        try {
            //pref = ctx.getSharedPreferences("AppPref", Context.MODE_PRIVATE);
            /*if(cookies.size() == 0) {

                //Log.d("Cookie","name: "+cookies.get(0).getName());
            } else {
                BasicCookieStore cStore = new BasicCookieStore();
                cStore.addCookie(cookies.get(0));
                httpClient.setCookieStore(cStore);
            }*/
            HttpResponse httpResponse;
            if(params == null) {
                HttpGet httpGet = new HttpGet("http://"+domain+path);
                httpResponse = httpClient.execute(httpGet);
            } else {
                HttpPost httpPost = new HttpPost("http://"+domain+path);
                httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
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

    public JSONObject putJSON (String path, List<NameValuePair> params) {
        try {
            HttpPut httpPut = new HttpPut("http://"+domain+path);
            httpPut.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
            HttpResponse httpResponse = httpClient.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
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
            Log.e("JSON1", json);
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

    public JSONObject postMedia(String path, List<NameValuePair> params) {
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            File file = new File(params.get(0).getValue());
            Long fileSize = file.length();
            Log.d("filesize", fileSize.toString());
            FileBody fb = new FileBody(file);
            entity.addPart("avatar",fb);

            HttpPost httpPost = new HttpPost("http://"+domain+path);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
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
}
