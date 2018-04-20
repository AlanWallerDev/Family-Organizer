package com.waller.alan.familyorganizer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EditableContactAdapter extends ArrayAdapter<Contact> {

    public EditableContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_contact_editable, parent, false);
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
