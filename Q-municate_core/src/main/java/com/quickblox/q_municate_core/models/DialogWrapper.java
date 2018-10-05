package com.quickblox.q_municate_core.models;

import android.content.Context;
import android.util.Log;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.core.helper.CollectionsUtil;
import com.quickblox.q_municate_core.R;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import java.io.Serializable;
import java.util.List;

public class DialogWrapper implements Serializable {

    private static final String TAG = DialogWrapper.class.getSimpleName();
    private ConnectycubeChatDialog chatDialog;
    private QMUser opponentUser;
    private long totalCount;
    private String lastMessage;
    private long lastMessageDate;

    public DialogWrapper(Context context, DataManager dataManager, ConnectycubeChatDialog chatDialog) {
        this.chatDialog = chatDialog;
        transform(context, dataManager);
    }

    private void transform(Context context, DataManager dataManager){
        ConnectycubeUser currentUser = AppSession.getSession().getUser();
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager().getDialogOccupantsListByDialogId(chatDialog.getDialogId());
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);

        fillOpponentUser(context, dataManager, dialogOccupantsList, currentUser);
        fillTotalCount(context, dataManager, dialogOccupantsIdsList, currentUser);
        fillLastMessage(context, dataManager, dialogOccupantsIdsList);
    }

    private void fillOpponentUser(Context context, DataManager dataManager,  List<DialogOccupant> dialogOccupantsList,  ConnectycubeUser currentUser ){
        if (ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType())) {
            opponentUser = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(currentUser), dialogOccupantsList);
            Log.v("Opponant Name", "Opponant Name: "+opponentUser.getFullName()+"---"+chatDialog.getName());
            if (opponentUser.getFullName() == null || opponentUser.getId() == null) {
                dataManager.getConnectycubeChatDialogDataManager().deleteById(chatDialog.getDialogId());
            }
        }
    }

    private void fillTotalCount(Context context, DataManager dataManager,  List<Long> dialogOccupantsIdsList,  ConnectycubeUser currentUser){
        long unreadMessages = dataManager.getMessageDataManager().getCountUnreadMessages(dialogOccupantsIdsList, currentUser.getId());
        long unreadDialogNotifications = dataManager.getDialogNotificationDataManager().getCountUnreadDialogNotifications(dialogOccupantsIdsList, currentUser.getId());
        if (unreadMessages > 0) {
            Log.i(TAG, "chat Dlg:" + chatDialog.getName() + ", unreadMessages = " + unreadMessages);
        }

        if (unreadDialogNotifications > 0) {
            Log.i(TAG, "unreadDialogNotifications = " + unreadDialogNotifications);
        }

        totalCount = unreadMessages + unreadDialogNotifications;
    }

    private void fillLastMessage(Context context, DataManager dataManager, List<Long> dialogOccupantsIdsList){
        Message message = dataManager.getMessageDataManager().getLastMessageWithTempByDialogId(dialogOccupantsIdsList);
        DialogNotification dialogNotification = dataManager.getDialogNotificationDataManager().getLastDialogNotificationByDialogId(dialogOccupantsIdsList);
        if(message != null)
            lastMessageDate = message.getCreatedDate();
        lastMessage = ChatUtils.getDialogLastMessage(context.getResources().getString(R.string.cht_notification_message), message, dialogNotification);
    }

    public ConnectycubeChatDialog getChatDialog() {
        return chatDialog;
    }

    public QMUser getOpponentUser() {
        return opponentUser;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }
}
