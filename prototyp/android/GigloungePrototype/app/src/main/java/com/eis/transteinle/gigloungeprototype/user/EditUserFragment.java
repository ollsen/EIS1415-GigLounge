package com.eis.transteinle.gigloungeprototype.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eis.transteinle.gigloungeprototype.R;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User user;

    ServerRequest sr;
    DlTask mDlTask;
    PutEditTask mPutEditTask;
    UlTask mUlTask;

    private EditText etEmail, etFirstName, etLastName, etCountry, etCity, etPostcode
            ,etAddress;
    private Button btnSave, btnChooseAvatar, btnUploadAvatar;

    private View mProgressView;
    private View mEditProfileView;

    static SharedPreferences pref;
    List<NameValuePair> params;
    private String mFilePath;

    private OnFragmentInteractionListener mListener;
    private static final int PICKFILE_RESULT_CODE = 1;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EditUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditUserFragment newInstance(String param1) {
        EditUserFragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public EditUserFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        Bundle bundle = getArguments();
        user = new User();
        Log.d("param",bundle.getString(ARG_PARAM1));
        user.setId(mParam1);
        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);

        // EditText
        etEmail = (EditText)view.findViewById(R.id.email);
        etFirstName = (EditText)view.findViewById(R.id.first_name);
        etLastName = (EditText)view.findViewById(R.id.last_name);
        //etCountry = (EditText)view.findViewById(R.id.country);
        etCity = (EditText)view.findViewById(R.id.city);
        etPostcode = (EditText)view.findViewById(R.id.postcode);
        etAddress = (EditText)view.findViewById(R.id.address);

        // Buttons
        btnSave = (Button)view.findViewById(R.id.save_user_btn);
        btnChooseAvatar = (Button)view.findViewById(R.id.choose_avatar_btn);
        btnUploadAvatar = (Button)view.findViewById(R.id.upload_avatar_btn);

        // Views
        mEditProfileView = view.findViewById(R.id.edit_user_form);
        mProgressView = view.findViewById(R.id.edit_user_progress);

        btnChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 96);
                intent.putExtra("outputY", 96);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent,PICKFILE_RESULT_CODE);
            }
        });

        btnUploadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUl();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptEditUser();
            }
        });

        attemptDl(user.getId());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri galleryUri = data.getData();
                    ImageView myImage = (ImageView) getActivity().findViewById(R.id.avatar);
                    myImage.setImageURI(galleryUri);
                    //mFilePath = galleryUri.getPath();
                    mFilePath = getRealPathFromURI(getActivity(), galleryUri);
                    btnUploadAvatar.setEnabled(true);
                    Log.d("FILEPATH", mFilePath);
                }
                break;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String wholeId = DocumentsContract.getDocumentId(contentUri);
            // Split at colon, use second item in the array
            String id = wholeId.split(":")[1];
            String[] proj = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , proj, sel, new String[]{id}, null);

            int column_index = cursor.getColumnIndex(proj[0]);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void attemptEditUser() {

        user.setEmail(etEmail.getText().toString());
        user.setFirstName(etFirstName.getText().toString());
        user.setLastName(etLastName.getText().toString());
        user.setCountry(etCountry.getText().toString());
        user.setCity(etCity.getText().toString());
        user.setPostcode(etPostcode.getText().toString());
        //user.setAddress(etAddress.getText().toString());

        Log.d("User",user.getCity());

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", user.getEmail()));
        params.add(new BasicNameValuePair("firstName", user.getFirstName()));
        params.add(new BasicNameValuePair("lastName", user.getLastName()));
        params.add(new BasicNameValuePair("country", user.getCountry()));
        params.add(new BasicNameValuePair("city", user.getCity()));
        params.add(new BasicNameValuePair("postcode", user.getPostcode()));
        //params.add(new BasicNameValuePair("address", user.getAddress()));
        showProgress(true);
        mPutEditTask = new PutEditTask();
        mPutEditTask.execute(params);
    }

    private void attemptDl(String param) {
        if (mDlTask != null) {
            return;
        }

        showProgress(true);
        mDlTask = new DlTask();
        mDlTask.execute(param);
    }

    private void attemptUl() {
        if (mUlTask != null) {
            return;
        }


        if(mFilePath != null)
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("image", mFilePath));
            showProgress(true);
            mUlTask = new UlTask();
            mUlTask.execute(params);
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

            mEditProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditProfileView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEditProfileView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DlTask extends AsyncTask<String, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(getActivity());
            Log.d("param", params[0]);
            JSONObject json = sr.getJSONFromUrl("/users/"+params[0], null);

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
                    if(json.has("country"))
                        user.setCountry(json.getString("country"));
                    if(json.has("city"))
                        user.setCity(json.getString("city"));
                    if(json.has("postcode"))
                        user.setCountry(json.getString("postcode"));
                    //if(json.has("address"))
                    //    user.setCity(json.getString("address"));

                    etEmail.setText(user.getEmail());
                    etFirstName.setText(user.getFirstName());
                    etLastName.setText(user.getLastName());
                    //etCountry.setText(user.getCountry());
                    etCity.setText(user.getCity());
                    etPostcode.setText(user.getPostcode());
                    //etAddress.setText(user.getAddress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mDlTask = null;
            showProgress(false);
        }
    }

    private class PutEditTask extends  AsyncTask<List<NameValuePair>, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            JSONObject json = sr.putJSON("/users/"+user.getId(), params[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    if(json.get("message").equals("updated")) {
                        Fragment fragment = UserFragment.newInstance(user.getId());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "error updating user profile", Toast.LENGTH_SHORT);
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
            mDlTask = null;
            showProgress(false);
        }
    }

    private class UlTask extends AsyncTask<List<NameValuePair>, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(List<NameValuePair>... params) {
            JSONObject json = sr.postMedia("/users/"+user.getId()+"/avatar", params[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {

                    String jsonstr = json.toString();
                    Log.v("Login", "response: " + jsonstr);
                    if(json.get("message").equals("avatar added")) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "avatar uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "error uploading an avatar", Toast.LENGTH_SHORT);
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
            mDlTask = null;
            showProgress(false);
        }
    }
}
