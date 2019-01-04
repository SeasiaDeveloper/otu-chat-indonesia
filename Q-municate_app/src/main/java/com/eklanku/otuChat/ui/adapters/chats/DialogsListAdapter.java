package com.eklanku.otuChat.ui.adapters.chats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCTypes;
import com.eklanku.otuChat.ui.activities.call.CallActivity;
import com.eklanku.otuChat.ui.activities.chats.BaseDialogActivity;
import com.eklanku.otuChat.ui.activities.chats.NewMessageActivity;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.others.PreviewImageActivity;
import com.eklanku.otuChat.ui.activities.profile.UserProfileActivity;
import com.eklanku.otuChat.ui.adapters.base.BaseListAdapter;
import com.eklanku.otuChat.ui.views.TouchImageView;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.DateUtils;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;

import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.utils.ToastUtils;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.utils.StringUtils;


public class DialogsListAdapter extends BaseListAdapter<DialogWrapper> {

    private static final String TAG = DialogsListAdapter.class.getSimpleName();

    ViewHolder viewHolder;
    BaseActivity baseActivity;
    private DataManager dataManager;

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList) {
        super(baseActivity, objectsList);
        this.baseActivity = baseActivity;
    }

    @Override
    public void setNewData(List<DialogWrapper> newData) {
        objectsList.clear();
        objectsList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DialogWrapper dialogWrapper = getItem(position);
        ConnectycubeChatDialog currentDialog = dialogWrapper.getChatDialog();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog, null);

            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = (RoundedImageView) convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.lastMessageTextView = (TextView) convertView.findViewById(R.id.last_message_textview);
            viewHolder.lastMessageTime = (TextView) convertView.findViewById(R.id.last_message_time);
            viewHolder.unreadMessagesTextView = (TextView) convertView.findViewById(
                    R.id.unread_messages_textview);

            convertView.setTag(viewHolder);

            viewHolder.avatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QMUser opponentUser;
                    if (dialogWrapper.getOpponentUser() != null) {
                        opponentUser = dialogWrapper.getOpponentUser();
                        //Toast.makeText(baseActivity, "ADA", Toast.LENGTH_SHORT).show();
                        viewImage(opponentUser.getAvatar(), opponentUser.getFullName(), opponentUser.getId().toString(), opponentUser);
                    } else {
                        Toast.makeText(baseActivity, "Tidak bisa menampilkan profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (ConnectycubeDialogType.PRIVATE.equals(currentDialog.getType())) {
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
    }

    public void viewImage(String imageUrl, String nama, String id, QMUser user) {
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.activity_preview_image_friends);
        builder.setCancelable(true);

        TouchImageView img = builder.findViewById(R.id.image_touchimageview);
        ImageButton btn_chat = builder.findViewById(R.id.ib_chat);
        ImageButton btn_call = builder.findViewById(R.id.ib_call);
        ImageButton btn_vcall = builder.findViewById(R.id.ib_vcall);
        ImageButton btn_info = builder.findViewById(R.id.ib_info);

        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(baseActivity)
                    .load(imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            ToastUtils.shortToast(R.string.preview_image_error);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(img);
        }else{
            img.setImageResource(R.drawable.placeholder_user);
        }

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(user);
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser(RTCTypes.ConferenceType.CONFERENCE_TYPE_AUDIO, user);
            }
        });

        btn_vcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser(RTCTypes.ConferenceType.CONFERENCE_TYPE_VIDEO, user);
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.start(context, Integer.parseInt(id));
            }
        });

        builder.show();
    }

    public void sendMessage(QMUser user){
        dataManager = DataManager.getInstance();
        user = QMUserService.getInstance().getUserCache().get((long)user.getId());
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            ConnectycubeChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            PrivateDialogActivity.startWithClearTop(context, user, chatDialog);
        } else {
            QBCreatePrivateChatCommand.start(context, user);
        }
    }
    private void callToUser(RTCTypes.ConferenceType conferenceType, QMUser user) {
        if (!isChatInitializedAndUserLoggedIn()) {
            ToastUtils.longToast(R.string.call_chat_service_is_initializing);
            return;
        }
        List<ConnectycubeUser> connectycubeUserList = new ArrayList<>(1);
        connectycubeUserList.add(UserFriendUtils.createConnectycubeUser(user));
        CallActivity.start((Activity) context, connectycubeUserList, conferenceType, null);
    }

    protected boolean isAppInitialized() {
        return AppSession.getSession().isSessionExist();
    }

    public boolean isChatInitializedAndUserLoggedIn() {
        return isAppInitialized() && ConnectycubeChatService.getInstance().isLoggedIn();
    }
}