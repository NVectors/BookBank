package com.example.bookbank.navigation;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookbank.R;
import com.example.bookbank.activities.LoginActivity;

public class AppBar extends DrawerLayout {

    private DrawerLayout drawerLayout;

    public AppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.app_bar, this, true);

        // set listener for app bar to open up the drawer widget
        drawerLayout = rootView.findViewById(R.id.drawer_layout);
        ImageView navIcon = rootView.findViewById(R.id.nav_icon);
        navIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // open drawer
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // set authenticated username in drawer view
        Log.d("debug", rootView.toString());
        addDrawerNavigationListeners(rootView);
    }

    private void addDrawerNavigationListeners(final View rootView) {
        TextView myProfileButton = rootView.findViewById(R.id.drawer_my_profile);
        myProfileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                 rootView.getContext().startActivity(new Intent(rootView.getContext() ,LoginActivity.class));
            }
        });
    }
}
