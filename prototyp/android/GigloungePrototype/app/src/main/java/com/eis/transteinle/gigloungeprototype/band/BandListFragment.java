package com.eis.transteinle.gigloungeprototype.band;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.eis.transteinle.gigloungeprototype.MainActivity;
import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;
import com.eis.transteinle.gigloungeprototype.dummy.DummyContent;
import com.eis.transteinle.gigloungeprototype.other.CustomListAdapter;
import com.eis.transteinle.gigloungeprototype.other.CustomListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class BandListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static final String ARG_SECTION_NUMBER = "section_number";

    ServerRequest sr;
    DlTask mDlTask;

    private View mProgressView;
    private View mBandlistView;

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

    // TODO: Rename and change types of parameters
    public static BandListFragment newInstance(int position) {
        BandListFragment fragment = new BandListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BandListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_band, container, false);

        List<CustomListItem> arrayOfBands = new ArrayList<CustomListItem>();
        // Create the adapter to convert the array to views
        adapter = new CustomListAdapter(getActivity(), arrayOfBands);

        // Attach the adapter to a ListView
        mListView = (ListView) view.findViewById(android.R.id.list);


        mBandlistView = mListView;
        mProgressView = view.findViewById(R.id.list_band_progress);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomListItem item = (CustomListItem)mListView.getItemAtPosition(position);
                OnBandSelected(item.getId());
            }
        });

        attemptDl();

        return view;
    }

    public void OnBandSelected(String param) {
        // Capture the Userprofile Fragment from the activity layout
        BandFragment fragment = BandFragment.newInstance(param);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
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

            mBandlistView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBandlistView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBandlistView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mBandlistView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DlTask extends AsyncTask<String, String,JSONObject> {

        private File image;

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(getActivity());
            JSONObject json = sr.getJSON("/bands/");



            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    /*String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    user.setEmail(json.getString("email"));
                    user.setFirstName(json.getString("firstName"));
                    user.setLastName(json.getString("lastName"));
                    //String city = json.getString("city");
                    tvName.setText(user.getFirstName()+" "+user.getLastName());
                    if(json.has("city"))
                        tvCity.setText(json.getString("city"));
                    else
                        tvCity.setText("unknown");*/
                    JSONArray jsonArray = json.getJSONArray("bands");
                    Log.v("Users","Count: "+jsonArray.length());
                    //list = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject u = jsonArray.getJSONObject(i);
                        adapter.add(new CustomListItem(u.getString("_id"),u.getString("name"), u.getString("city")));
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
