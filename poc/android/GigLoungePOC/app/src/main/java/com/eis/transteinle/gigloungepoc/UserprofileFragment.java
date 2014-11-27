package com.eis.transteinle.gigloungepoc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserprofileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserprofileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserprofileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static ProgressDialog pDialog;


    ServerRequest sr;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView userTv;
    private TextView fNameTv;
    private TextView lNameTv;
    private TextView emailTv;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UserprofileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserprofileFragment newInstance(String param1) {
        UserprofileFragment fragment = new UserprofileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public UserprofileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userprofile, container, false);

        Bundle bundle = getArguments();
        String usern = bundle.getString(ARG_PARAM1);


        userTv = (TextView)view.findViewById(R.id.usern);
        fNameTv = (TextView)view.findViewById(R.id.fName);
        lNameTv = (TextView)view.findViewById(R.id.lName);
        emailTv = (TextView)view.findViewById(R.id.uEmail);

        new DownloadJSON().execute(usern);

        //sr = new ServerRequest(this.getActivity());
        //JSONObject json = sr.getJSON("http://h2192129.stratoserver.net/users/mlist/"+usern, null);
        //List<String> list = new ArrayList<String>();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onFragmentInteraction(Uri uri);
    }

    private class DownloadJSON extends AsyncTask<String, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(getActivity());
            //JSONObject json = sr.getJSON("http://h2192129.stratoserver.net/users/mlist", null);

            JSONObject json = sr.getJSONFromUrl("http://h2192129.stratoserver.net/users/mlist/"+params[0], null);

            return json;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {
                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    String uname = json.getString("username");
                    String email = json.getString("email");
                    String fname = json.getString("firstName");
                    String lname = json.getString("lastName");
                    userTv.setText(uname);
                    fNameTv.setText(fname);
                    lNameTv.setText(lname);
                    emailTv.setText(email);
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
