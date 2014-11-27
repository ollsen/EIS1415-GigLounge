package com.eis.transteinle.gigloungepoc;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    EditText username,password,res_email;
    Button login,cont,cont_code,cancel,cancel1,register;
    String unametxt,passwordtxt,email_res_txt,code_txt;
    List<NameValuePair> params;
    Dialog reset;
    ServerRequest sr;
    static SharedPreferences pref;

    private static ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //sr = new ServerRequest(LoginActivity.this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.loginbtn);
        register = (Button) findViewById(R.id.register);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent regactivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regactivity);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                unametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", unametxt));
                params.add(new BasicNameValuePair("password", passwordtxt));
                //ServerRequest sr = new ServerRequest(LoginActivity.this);
                //JSONObject json = sr.getJSON("http://h2192129.stratoserver.net/mlogin", params);
                new DownloadJSON().execute(params);
            }
        });

    }

    private class DownloadJSON extends AsyncTask<List<NameValuePair>, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            sr = new ServerRequest(LoginActivity.this);

            JSONObject json = sr.getJSONFromUrl("http://h2192129.stratoserver.net/mlogin", params[0]);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            Log.d("onPostJson",json.toString());
            if (json != null) {
                try {
                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    String uname = json.getString("username");
                    String email = json.getString("email");
                    String fname = json.getString("firstName");
                    String lname = json.getString("lastName");
                    //String pwtoken = json.getString("");
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("username", uname);
                    edit.putString("email", email);
                    edit.putString("firstName", fname);
                    edit.putString("lastName", lname);
                    edit.putString("cookie", sr.getCookies().toString());
                    edit.commit();
                    Intent profactivity = new Intent(LoginActivity.this, MainActivity.class);
                    finish();
                    startActivity(profactivity);
                    Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }
}



