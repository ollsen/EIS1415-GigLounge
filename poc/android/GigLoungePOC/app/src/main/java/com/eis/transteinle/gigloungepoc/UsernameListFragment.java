package com.eis.transteinle.gigloungepoc;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.Toast;


import com.eis.transteinle.gigloungepoc.dummy.DummyContent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnUserSelectedListener}
 * interface.
 */
public class UsernameListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    List<NameValuePair> params;
    static SharedPreferences pref;
    Dialog reset;
    ServerRequest sr;
    View view;
    ArrayList<String> list = new ArrayList<String>();
    Context context;
    ArrayAdapter<String> adapter;

    private static ProgressDialog pDialog;




    private OnUserSelectedListener mListener;

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
    public static UsernameListFragment newInstance(int sectionNumber) {
        UsernameListFragment fragment = new UsernameListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsernameListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item, container, false);
        pref = this.getActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        context = view.getContext();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", pref.getString("username","")));
        params.add(new BasicNameValuePair("password", pref.getString("password","")));
        new DownloadJSON().execute(params);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);




        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnUserSelected((String) mListView.getItemAtPosition(position));
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mListener = (OnUserSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnUserelectedListener");
        }
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
            mListener.OnUserSelected((String) mListView.getItemAtPosition(position));
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
    public interface OnUserSelectedListener {
        // TODO: Update argument type and name
        public void OnUserSelected(String param);
    }

    private class DownloadJSON extends AsyncTask<List<NameValuePair>, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            sr = new ServerRequest(context);

            JSONObject json = sr.getJSONFromUrl("http://h2192129.stratoserver.net/users/mlist", null);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(view.getContext());
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
                    JSONArray jsonArray = json.getJSONArray("users");
                    Log.v("Users","Count: "+jsonArray.length());
                    list = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject u = jsonArray.getJSONObject(i);
                        list.add(u.getString("username"));
                    }
                    Integer size = new Integer(list.size());
                    Log.d("list", size.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, list);

                mListView.setAdapter(adapter);


                adapter.notifyDataSetChanged();





            }
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

}
