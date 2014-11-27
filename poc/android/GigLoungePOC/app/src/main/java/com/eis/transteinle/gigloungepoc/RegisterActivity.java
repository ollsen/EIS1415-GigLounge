package com.eis.transteinle.gigloungepoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends Activity {
    EditText username, email,firstname, lastname, password;
    Button login,register;
    String usernametxt, emailtxt, firstnametxt, lastnametxt, passwordtxt;
    List<NameValuePair> params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.rUsername);
        email = (EditText)findViewById(R.id.rEmail);
        firstname = (EditText)findViewById(R.id.rFname);
        lastname = (EditText)findViewById(R.id.rLname);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.registerbtn);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regactivity = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(regactivity);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernametxt = username.getText().toString();
                emailtxt = email.getText().toString();
                firstnametxt = firstname.getText().toString();
                lastnametxt = lastname.getText().toString();
                passwordtxt = password.getText().toString();
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", usernametxt));
                params.add(new BasicNameValuePair("email", emailtxt));
                params.add(new BasicNameValuePair("firstName", firstnametxt));
                params.add(new BasicNameValuePair("lastName", lastnametxt));
                params.add(new BasicNameValuePair("password", passwordtxt));
                ServerRequest sr = new ServerRequest(RegisterActivity.this);
                JSONObject json = sr.getJSON("http://h2192129.stratoserver.net:3000/msignup", params);
                if (json != null) {
                    try {
                        String jsonstr = json.getString("response");
                        Toast.makeText(getApplication(), jsonstr, Toast.LENGTH_LONG).show();
                        Log.d("Hello", "Hello " + jsonstr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
