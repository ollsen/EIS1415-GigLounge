package com.eis.transteinle.gigloungeprototype.other;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eis.transteinle.gigloungeprototype.R;

import java.util.List;

/**
 * Created by DerOlli on 08.01.15.
 */
public class CustomListAdapter extends ArrayAdapter<CustomListItem> {

    private Context context;
    private boolean useList = true;

    public CustomListAdapter(Context context, List<CustomListItem> items) {
        super(context, 0, items);
    }

    private class ViewHolder {
        TextView titleText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CustomListItem item = getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        //LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bandlist, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.firstLine);
        TextView tvHome = (TextView) convertView.findViewById(R.id.secondLine);
        // Populate the data into the template view using the data object
        Log.d("adapter",item.getItemTitle());
        tvName.setText(item.getItemTitle());
        tvHome.setText(item.getSecondTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
