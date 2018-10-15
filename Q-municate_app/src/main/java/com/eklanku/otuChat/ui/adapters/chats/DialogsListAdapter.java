package com.eklanku.otuChat.ui.adapters.chats;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.payment.models.DataBanner;
import com.eklanku.otuChat.ui.activities.payment.models.LoadBanner;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.base.BaseListAdapter;
import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.adapters.base.BaseListAdapter;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.utils.StringUtils;
import com.yyydjk.library.BannerLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogsListAdapter extends BaseListAdapter<DialogWrapper> {

    private static final String TAG = DialogsListAdapter.class.getSimpleName();
    public static String[] banner_promo;
    public ApiInterfacePayment mApiInterfacePayment;
    public String strApIUse = "OTU";
    ViewHolder viewHolder;
    BaseActivity baseActivity;

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList) {
        super(baseActivity, objectsList);
        this.baseActivity = baseActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder viewHolder;

        DialogWrapper dialogWrapper = getItem(position);
        QBChatDialog currentDialog = dialogWrapper.getChatDialog();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog, null);

            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = (RoundedImageView) convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.lastMessageTextView = (TextView) convertView.findViewById(R.id.last_message_textview);
            viewHolder.lastMessageTime = (TextView) convertView.findViewById(R.id.last_message_time);
            viewHolder.unreadMessagesTextView = (TextView) convertView.findViewById(
                    R.id.unread_messages_textview);
            viewHolder.banner = convertView.findViewById(R.id.bannerLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (QBDialogType.PRIVATE.equals(currentDialog.getType())) {
            QMUser opponentUser = dialogWrapper.getOpponentUser();
            if (opponentUser.getFullName() != null) {
                String username = "";
                if (opponentUser.getFullName().equals(String.valueOf(opponentUser.getId()))) {
                    // viewHolder.nameTextView.setText(currentDialog.getName());
                    username = currentDialog.getName();
                } else {
                    //viewHolder.nameTextView.setText(opponentUser.getFullName());
                    username = opponentUser.getFullName();
                }
                if (StringUtils.isNumeric(opponentUser.getFullName()) && context instanceof MainActivity) {
                    String name = ((MainActivity) context).mDbHelper.getNamebyNumber(opponentUser.getFullName());
                    if (!name.isEmpty())
                        username = name;

                }
                viewHolder.nameTextView.setText(username);
                displayAvatarImage(opponentUser.getAvatar(), viewHolder.avatarImageView);
            } else {
                viewHolder.nameTextView.setText(resources.getString(R.string.deleted_user));
            }
        } else {
            viewHolder.nameTextView.setText(currentDialog.getName());
            viewHolder.avatarImageView.setImageResource(R.drawable.placeholder_group);
            displayGroupPhotoImage(currentDialog.getPhoto(), viewHolder.avatarImageView);
        }

        long totalCount = dialogWrapper.getTotalCount();

        if (totalCount > ConstsCore.ZERO_INT_VALUE) {
            viewHolder.unreadMessagesTextView.setText(totalCount >= 100 ? resources.getString(R.string.dialog_count_unread) : Long.toString(totalCount));
            viewHolder.unreadMessagesTextView.setVisibility(View.VISIBLE);
            viewHolder.lastMessageTime.setTextColor(context.getResources().getColor(R.color.badge_unread_messages_counter));
        } else {
            viewHolder.unreadMessagesTextView.setVisibility(View.GONE);
            viewHolder.lastMessageTime.setTextColor(context.getResources().getColor(R.color.gray_color_message));
        }

        viewHolder.lastMessageTextView.setText(dialogWrapper.getLastMessage());

        DateFormat df = new SimpleDateFormat("MMddyyyy HH:mm");
        String sdt = df.format(new Date(dialogWrapper.getLastMessageDate()));

        viewHolder.lastMessageTime.setText(DateUtils.toTodayYesterdayShortMonthDate(dialogWrapper.getLastMessageDate()));

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        return convertView;
    }

    public void updateItem(DialogWrapper dlgWrapper) {
        Log.i(TAG, "updateItem = " + dlgWrapper.getChatDialog().getUnreadMessageCount());
        int position = -1;
        for (int i = 0; i < objectsList.size(); i++) {
            DialogWrapper dialogWrapper = objectsList.get(i);
            if (dialogWrapper.getChatDialog().getDialogId().equals(dlgWrapper.getChatDialog().getDialogId())) {
                position = i;
                break;
            }
        }

        if (position != -1) {
            Log.i(TAG, "find position = " + position);
            objectsList.set(position, dlgWrapper);
        } else {
            addNewItem(dlgWrapper);
        }

    }

    public void moveToFirstPosition(DialogWrapper dlgWrapper) {
        if (objectsList.size() != 0 && !objectsList.get(0).equals(dlgWrapper)) {
            objectsList.remove(dlgWrapper);
            objectsList.add(0, dlgWrapper);
            notifyDataSetChanged();
        }
    }

    public void removeItem(String dialogId) {
        Iterator<DialogWrapper> iterator = objectsList.iterator();

        while (iterator.hasNext()) {
            DialogWrapper dialogWrapper = iterator.next();
            if (dialogWrapper.getChatDialog().getDialogId().equals(dialogId)) {
                iterator.remove();
                notifyDataSetChanged();
                break;
            }
        }

    }

    private static class ViewHolder {

        public RoundedImageView avatarImageView;
        public TextView nameTextView;
        public TextView lastMessageTextView;
        public TextView unreadMessagesTextView;
        public TextView lastMessageTime;
        public BannerLayout banner;

    }

    public void loadBanner() {

        viewHolder.banner.setImageLoader(new GlideImageLoader());
        List<String> urls = new ArrayList<>();
        Call<LoadBanner> callLoadBanner = mApiInterfacePayment.getBanner(PreferenceUtil.getNumberPhone(baseActivity), strApIUse);
        callLoadBanner.enqueue(new Callback<LoadBanner>() {
            @Override
            public void onResponse(Call<LoadBanner> call, Response<LoadBanner> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("SUCCESS")) {
                        final List<DataBanner> result = response.body().getRespMessage();
                        banner_promo = new String[result.size()];
                        if (result.size() > 0) {
                            try {
                                for (int i = 0; i < result.size(); i++) {
                                    banner_promo[i] = result.get(i).getBaner_promo();
                                    urls.add(banner_promo[i]);
                                }
                            } catch (Exception e) {
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                            }
                        } else {
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                        }
                    } else {
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                    }
                    viewHolder.banner.setViewUrls(urls);
                }
            }

            @Override
            public void onFailure(Call<LoadBanner> call, Throwable t) {
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");

                viewHolder.banner.setViewUrls(urls);
            }
        });
    }
}