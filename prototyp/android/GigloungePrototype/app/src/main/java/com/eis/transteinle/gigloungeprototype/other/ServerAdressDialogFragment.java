package com.eis.transteinle.gigloungeprototype.other;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.eis.transteinle.gigloungeprototype.R;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class ServerAdressDialogFragment extends DialogFragment {

    static SharedPreferences pref;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setHint("127.0.0.1:3000");
        builder.setView(input);
        builder.setMessage("Server Adress")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pref = getActivity().getSharedPreferences("AppPref", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("URL",input.getText().toString());
                        edit.commit();
                    }
                })
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
        return builder.create();
    }
}
