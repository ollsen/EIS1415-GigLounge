package com.eis.transteinle.gigloungeprototype.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User user;

    private OnFragmentInteractionListener mListener;

    ServerRequest sr;
    DlTask mDlTask;
    DlAvatarTask mDlAvatarTask;

    static SharedPreferences pref;

    private TextView tvName;
    private TextView tvCity;

    private Button btnEdit;

    private View mProgressView;
    private View mProfileView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Bundle bundle = getArguments();
        //String usern =
        user = new User();
        user.setId(bundle.getString(ARG_PARAM1));
        btnEdit = (Button)view.findViewById(R.id.btnEditUser);
        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
        if(pref.getString("id","").equals(user.getId())) {
            btnEdit.setVisibility(Button.VISIBLE);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = EditUserFragment.newInstance(user.getId());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }


        tvName = (TextView)view.findViewById(R.id.tvUsername);
        tvCity = (TextView)view.findViewById(R.id.tvCity);
        mProfileView = view.findViewById(R.id.userprofile_view);
        mProgressView = view.findViewById(R.id.progressBar);

        attemptDl(user.getId());

        return view;
    }

    public void attemptDl(String param) {
        if (mDlTask != null) {
            return;
        }

        showProgress(true);
        mDlTask = new DlTask();
        mDlTask.execute(param);
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

            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mDlAvatarTask = new DlAvatarTask();
            mDlAvatarTask.execute(user.getId());
            //showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mDlTask = null;
            showProgress(false);
        }
    }

    private class DlAvatarTask extends AsyncTask<String, String,File> {
        @Override
        protected void onPostExecute(File image) {
            super.onPostExecute(image);
            if (image != null) {
                ImageView myImage = (ImageView) getActivity().findViewById(R.id.avatar);
                Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                myImage.setImageBitmap(myBitmap);
            }
            showProgress(false);
        }

        @Override
        protected File doInBackground(String... params) {
            Log.d("param",params[0]);
            File image = sr.getMedia("/users/"+params[0]+"/avatar", null);
            return image;
        }
        @Override
        protected void onCancelled() {
            mDlAvatarTask = null;
            showProgress(false);
        }
    }
}
