package com.eklanku.otuChat.ui.activities.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.R;;

public class EditProfilActivity extends BaseLoggableActivity {


    @Override
    protected int getContentResId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, EditProfilActivity.class);
        context.startActivity(intent);
    }
}
