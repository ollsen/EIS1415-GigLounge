package com.eis.transteinle.gigloungeprototype.band;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;
import com.eis.transteinle.gigloungeprototype.user.UserFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateBandFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateBandFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ServerRequest sr;

    private EditText etBandName, etCity, etPostcode
            ,etAddress;

    private Spinner spGenre, spCountry;

    private String selectedCountry;
    private String selectedGenre;

    private Button btnSave, btnChooseAvatar, btnUploadAvatar;

    private View mProgressView;
    private View mCreateBandView;

    private SendTask mSendTask;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateBandFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateBandFragment newInstance(String param1, String param2) {
        CreateBandFragment fragment = new CreateBandFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateBandFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_band, container, false);
        // EditText
        etBandName = (EditText)view.findViewById(R.id.band_name);
        etCity = (EditText)view.findViewById(R.id.city);
        etPostcode = (EditText)view.findViewById(R.id.postcode);
        // Spinner
        spCountry = (Spinner)view.findViewById(R.id.spinner_country);
        spGenre = (Spinner)view.findViewById(R.id.spinner_genre);

        // Buttons
        btnSave = (Button)view.findViewById(R.id.save_user_btn);

        // Views
        mCreateBandView = view.findViewById(R.id.create_band_view);
        mProgressView = view.findViewById(R.id.create_band_progress);

        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreateBand();
            }
        });

        return view;
    }

    private void attemptCreateBand() {


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", etBandName.getText().toString()));
        params.add(new BasicNameValuePair("city", etCity.getText().toString()));
        params.add(new BasicNameValuePair("postcode", etPostcode.getText().toString()));
        params.add(new BasicNameValuePair("country", selectedCountry));
        params.add(new BasicNameValuePair("genre", selectedGenre));
        String jsonString = "{role : 'guitarist'}";
        params.add(new BasicNameValuePair("members", jsonString));

        showProgress(true);
        mSendTask = new SendTask();
        mSendTask.execute(params);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateBandView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateBandView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateBandView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCreateBandView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class SendTask extends AsyncTask<List<NameValuePair>, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            sr = new ServerRequest(getActivity());
            JSONObject json = sr.postJSON("/bands", params[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    if(json.get("message").equals("Band created")) {
                        /*Fragment fragment = UserFragment.newInstance();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();*/
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Band created", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "error creating bands", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mSendTask = null;
            showProgress(false);
        }
    }

}
