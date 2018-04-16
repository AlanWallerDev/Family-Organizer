package com.waller.alan.familyorganizer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_contact, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView emailTextView = (TextView) convertView.findViewById(R.id.emailTextView);

        Contact contact = getItem(position);

        nameTextView.setVisibility(View.VISIBLE);
        nameTextView.setText(contact.getDisplayName());

        emailTextView.setText(contact.getEmail());

        return convertView;
    }
}
