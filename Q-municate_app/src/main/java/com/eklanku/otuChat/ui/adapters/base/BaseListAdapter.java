package com.eklanku.otuChat.ui.adapters.base;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.utils.ValidationUtils;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_core.models.AppSession;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {


    protected LayoutInflater layoutInflater;
    protected Context context;
    protected List<T> objectsList;
    protected Resources resources;

    protected ConnectycubeUser currentUser;

    public BaseListAdapter(Context context, List<T> objectsList) {
        this.context = context;
        this.objectsList = objectsList;
        this.layoutInflater = LayoutInflater.from(context);
        resources = context.getResources();
        currentUser = AppSession.getSession().getUser();
    }

    @Override
    public int getCount() {
        return objectsList.size();
    }

    @Override
    public T getItem(int position) {
        return objectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setNewData(List<T> newData) {
        objectsList = newData;
        notifyDataSetChanged();
    }

    public void addNewData(ArrayList<T> newData) {
        objectsList.addAll(newData);
        notifyDataSetChanged();
    }

    public void addNewItem(T item) {
        objectsList.add(0, item);
        notifyDataSetChanged();
    }

    protected void displayAvatarImage(String uri, ImageView imageView) {
        if(ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_user);
        }else{
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        }
    }

    protected void displayGroupPhotoImage(String uri, ImageView imageView) {
        if(ValidationUtils.isNull(uri)) {
            imageView.setImageResource(R.drawable.placeholder_group);
        }else{
            ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtils.UIL_GROUP_AVATAR_DISPLAY_OPTIONS);
        }

    }

}