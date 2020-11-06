package com.example.bookbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.activities.SearchUsernameActivity;
import com.example.bookbank.models.User;

import java.util.ArrayList;

public class SearchUsernameAdapter extends ArrayAdapter {

    private ArrayList<User> userList;
    private Context context;

    public SearchUsernameAdapter(@NonNull Context context, @NonNull ArrayList<User> userList) {
        super(context, 0, userList);
        this.userList = userList;
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
            view = LayoutInflater.from(context).inflate(R.layout.searched_user_block, parent,false);
        }

        /** Get the position of book in the ArrayList<User> */
        User user = userList.get(position);

        /** Get references to the objects in the layout */
        TextView userName = view.findViewById(R.id.searched_user_name);
        TextView userPhone = view.findViewById(R.id.searched_user_phone);
        TextView userAddr = view.findViewById(R.id.searched_user_addr);

        /** Set references to the user object data */
        userName.setText(user.getFullname());
        userPhone.setText(user.getPhoneNumber());
        userAddr.setText(user.getAddress());

        return view;

    }
}
