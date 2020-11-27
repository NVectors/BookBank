package com.example.bookbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.models.Notification;

import java.util.ArrayList;

public class NotificationsAdapter extends ArrayAdapter {
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // custom array adapter for formatting each item in our list
        // inflate our custom layout (R.layout.gear_list_view) instead of the default view
        // LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View view = inflater.inflate(R.layout.list_item, null);

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification_block, parent,false);
        }

        /** Get the position of notification in the ArrayList<Notification> */
        Notification notification = notifications.get(position);

        /** Get references to the objects in the layout */
        ImageView notificationImage = view.findViewById(R.id.notification_bell_image);
        TextView notificationText = view.findViewById(R.id.notification_text);

        /** Set references to the user object data */
        notificationImage.setImageResource(R.drawable.notification_bell);
        notificationText.setText(notification.getMessage());


        return view;
    }
}
