package com.eklanku.otuChat.ui.activities.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import com.eklanku.otuChat.R;;

public class contact_us extends AppCompatActivity {

    @Bind(R.id.version)
    TextView version;

    @Bind(R.id.about_enjoy_it)
    TextView aboutEnjoyIT;

    @Bind(R.id.about_app_name)
    TextView appname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);


    }
}
