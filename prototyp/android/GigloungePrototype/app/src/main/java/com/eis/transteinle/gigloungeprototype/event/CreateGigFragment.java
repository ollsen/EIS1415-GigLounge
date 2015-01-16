package com.eis.transteinle.gigloungeprototype.event;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGigFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout mMembers;

    private LinearLayout mMemberItem;
    private View mRootView;

    ServerRequest sr;

    private Button mAddMemberBtn;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateGigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGigFragment newInstance(String param1, String param2) {
        CreateGigFragment fragment = new CreateGigFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateGigFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_create_gig, container, false);

        mMembers = (LinearLayout)mRootView.findViewById(R.id.members);
        mMemberItem = (LinearLayout)inflater.inflate(R.layout.item_add_member, null);

        mAddMemberBtn = (Button)mRootView.findViewById(R.id.add_member_btn);
        mAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mMembers.addView(mMemberItem);
            }
        });



        return mRootView;
    }

    private class DlTask extends AsyncTask<String, String,JSONObject> {

        private File image;

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(getActivity());
            JSONObject json = sr.getJSON("/users/"+params[0]);



            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            /*
            if (json != null) {
                try {

                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    user.setEmail(json.getString("email"));
                    user.setFirstName(json.getString("firstName"));
                    user.setLastName(json.getString("lastName"));
                    //String city = json.getString("city");
                    tvName.setText(user.getFirstName()+" "+user.getLastName());
                    if(json.has("city"))
                        tvCity.setText(json.getString("city"));
                    else
                        tvCity.setText("unknown");
                    if(json.has("avatar")) {
                        mDlAvatarTask = new DlAvatarTask();
                        mDlAvatarTask.execute(user.getId());
                    } else
                        showProgress(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
        }

        @Override
        protected void onCancelled() {
            //mDlTask = null;
            //showProgress(false);
        }
    }

}
