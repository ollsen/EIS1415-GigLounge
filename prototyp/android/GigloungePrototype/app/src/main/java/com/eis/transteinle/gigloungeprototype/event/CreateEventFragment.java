package com.eis.transteinle.gigloungeprototype.event;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ServerRequest sr;

    private EditText etEventName,etLocation, etCity, etPostcode ,etAddress;
    private Spinner  spCountry;

    private Button btnSave, btnStart, btnEnd;

    HashMap<String, String> myMap;

    private View mProgressView;
    private View mCreateEventView;

    private String selectedCountry = null;

    private SendTask mSendTask;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateEventFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        // EditText
        etEventName = (EditText)view.findViewById(R.id.event_name);
        etLocation = (EditText)view.findViewById(R.id.event_location);
        etAddress = (EditText)view.findViewById(R.id.event_address);
        etCity = (EditText)view.findViewById(R.id.event_city);
        etPostcode = (EditText)view.findViewById(R.id.event_postcode);
        // Spinner
        spCountry = (Spinner)view.findViewById(R.id.spinner_country);

        // Buttons
        btnSave = (Button)view.findViewById(R.id.save_event_btn);
        btnStart = (Button)view.findViewById(R.id.start_btn);
        btnEnd = (Button)view.findViewById(R.id.end_btn);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int startYear = c.get(Calendar.YEAR);
                int startMonth = c.get(Calendar.MONTH);
                int startDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        new mDateSetListener(), startYear, startMonth, startDay);
                dialog.show();
            }
        });

        String[] countriesNames = getResources().getStringArray(R.array.country_array);
        String[] countriesCodes = getResources().getStringArray(R.array.country_array_code);

        myMap = new HashMap<String, String>();

        for (int i = 0; i < countriesNames.length; i++) {
            myMap.put(countriesNames[i], countriesCodes[i]);
        }

        // Views
        mCreateEventView = view.findViewById(R.id.create_event_view);
        mProgressView = view.findViewById(R.id.create_event_progress);

        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = myMap.get(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreateEvent();
            }
        });

        return view;
    }

    private void attemptCreateEvent(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", etEventName.getText().toString()));
        params.add(new BasicNameValuePair("location", etLocation.getText().toString()));
        params.add(new BasicNameValuePair("address", etAddress.getText().toString()));
        params.add(new BasicNameValuePair("city", etCity.getText().toString()));
        params.add(new BasicNameValuePair("postcode", etPostcode.getText().toString()));
        params.add(new BasicNameValuePair("country", selectedCountry));

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

            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateEventView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class SendTask extends AsyncTask<List<NameValuePair>, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            sr = new ServerRequest(getActivity());
            JSONObject json = sr.postJSON("/events", params[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    if(json.get("message").equals("Event created")) {
                        if(mParam1.equals("GIG")) {
                            Fragment fragment = CreateGigFragment.newInstance(json.get("id").toString(),"");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .commit();
                        }
                        /*Fragment fragment = UserFragment.newInstance();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();*/
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Event created", Toast.LENGTH_SHORT);
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


    private class mDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        }
    }
}
