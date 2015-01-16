package com.eis.transteinle.gigloungeprototype.event;


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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.band.BandFragment;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;
import com.eis.transteinle.gigloungeprototype.other.CustomListAdapter;
import com.eis.transteinle.gigloungeprototype.other.CustomListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ServerRequest sr;
    DlTask mDlTask;

    private Button mCreateEventBtn;

    private View mProgressView;
    private View mEventListView;

    CustomListAdapter adapter;



    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventListFragment newInstance(String param1, String param2) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EventListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        List<CustomListItem> arrayOfBands = new ArrayList<CustomListItem>();
        // Create the adapter to convert the array to views
        adapter = new CustomListAdapter(getActivity(), arrayOfBands);

        // Attach the adapter to a ListView
        mListView = (ListView) view.findViewById(android.R.id.list);

        mCreateEventBtn = (Button)view.findViewById(R.id.create_event_btn);

        mCreateEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CreateEventFragment.newInstance("GIG","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        mEventListView = mListView;
        mProgressView = view.findViewById(R.id.list_event_progress);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListItem item = (CustomListItem)mListView.getItemAtPosition(position);
                OnEventSelected(item.getId());
            }
        });

        attemptDl();

        return view;
    }

    public void OnEventSelected(String param) {
        // Capture the Userprofile Fragment from the activity layout
        Fragment fragment = CreateGigFragment.newInstance(param,"");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void attemptDl() {
        if (mDlTask != null) {
            return;
        }

        showProgress(true);
        mDlTask = new DlTask();
        mDlTask.execute();
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

            mEventListView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEventListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEventListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEventListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DlTask extends AsyncTask<String, String,JSONObject> {

        private File image;

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(getActivity());
            JSONObject json = sr.getJSON("/events");



            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    JSONArray jsonArray = json.getJSONArray("event");
                    Log.v("Users", "Count: " + jsonArray.length());
                    //list = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject u = jsonArray.getJSONObject(i);
                        adapter.add(new CustomListItem(u.getString("_id"),u.getString("name"), ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mListView.setAdapter(adapter);


                adapter.notifyDataSetChanged();
            }
            //mDlAvatarTask = new DlAvatarTask();
            //mDlAvatarTask.execute(user.getId());
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mDlTask = null;
            showProgress(false);
        }
    }

}
