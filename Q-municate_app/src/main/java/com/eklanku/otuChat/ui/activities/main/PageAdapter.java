package com.eklanku.otuChat.ui.activities.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.eklanku.otuChat.ui.fragments.CallFragment;
import com.eklanku.otuChat.ui.fragments.PaymentFragment;
import com.eklanku.otuChat.ui.fragments.chats.DialogsListFragment;
import com.eklanku.otuChat.ui.fragments.CallFragment;
import com.eklanku.otuChat.ui.fragments.PaymentFragment;
import com.eklanku.otuChat.ui.fragments.chats.DialogsListFragment;

public class PageAdapter extends FragmentPagerAdapter {


    PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //Log.d("OPPO-1", "getItem: "+position);
        switch (position) {
            case 0:
                return new DialogsListFragment();
            case 1:
                return new CallFragment();
            case 2:
                return new PaymentFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}