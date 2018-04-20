package com.waller.alan.familyorganizer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
public class EventAdapter extends ArrayAdapter<Event> {
    private final String TAG = "Event Adapter";
    public EventAdapter(Context context, int resource, List<Event> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_event, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView descTextView = (TextView) convertView.findViewById(R.id.descTextView);
        CalendarView calView = (CalendarView) convertView.findViewById(R.id.calView);

        Event event = getItem(position);

        nameTextView.setVisibility(View.VISIBLE);
        nameTextView.setText(event.getName());
        //todo: fix bug with date not being properly displayed
        descTextView.setText(event.getDescription());
        Log.d(TAG, Long.toString(event.getStartDate()));
        calView.setDate(event.getStartDate());

        return convertView;
    }


}
