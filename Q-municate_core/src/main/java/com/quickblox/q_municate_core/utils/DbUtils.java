package com.quickblox.q_municate_core.utils;

import android.content.Context;
import android.util.Log;

import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatMessage;
import com.connectycube.chat.model.ConnectycubeChatDialog ;
import com.connectycube.core.helper.CollectionsUtil;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.helpers.QBRestHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbUtils {

    private static final String TAG = "DialogDbUtils";

    public static DialogOccupant saveDialogOccupantIfUserNotExists(DataManager dataManager,
                                                                   String dialogId, int userId, DialogOccupant.Status status) {
        QBRestHelper.loadAndSaveUser(userId);

        QMUser user = QMUserService.getInstance().getUserCache().get((long)userId);
        DialogOccupant dialogOccupant = ChatUtils.createDialogOccupant(dataManager, dialogId, user);
        dialogOccupant.setStatus(status);

        saveDialogOccupant(dataManager, dialogOccupant);

        return dialogOccupant;
    }

    public static void saveDialogToCache(DataManager dataManager, ConnectycubeChatDialog qbDialog) {
        dataManager.getConnectycubeChatDialogDataManager().createOrUpdate(qbDialog);
        saveDialogsOccupants(dataManager, qbDialog);
    }

    public static void saveDialogToCache(DataManager dataManager, ConnectycubeChatDialog qbDialog, boolean notify) {
        dataManager.getConnectycubeChatDialogDataManager().createOrUpdate(qbDialog, notify);
        saveDialogsOccupants(dataManager, qbDialog);
    }

    private static void saveDialogsOccupants(DataManager dataManager, ConnectycubeChatDialog qbDialog) {
        if (qbDialog.getOccupants() != null && !qbDialog.getOccupants().isEmpty()) {
            saveDialogsOccupants(dataManager, qbDialog, false);
        }
    }

    public static void saveDialogsToCacheAll(DataManager dataManager, List<ConnectycubeChatDialog > connectycubeChatDialogsList,
            ConnectycubeChatDialog  currentDialog) {
        dataManager.getConnectycubeChatDialogDataManager().createOrUpdateAll(connectycubeChatDialogsList);

        saveDialogsOccupants(dataManager, connectycubeChatDialogsList);

        saveTempMessages(dataManager, connectycubeChatDialogsList, currentDialog);
    }

    public static void saveDialogsToCacheByIds(DataManager dataManager, List<ConnectycubeChatDialog > connectycubeChatDialogsList, ConnectycubeChatDialog  currentDialog) {
        saveDialogsOccupants(dataManager, connectycubeChatDialogsList);
        saveTempMessagesUnread(dataManager, connectycubeChatDialogsList, currentDialog);

        for (ConnectycubeChatDialog qbDialog : connectycubeChatDialogsList) {
            dataManager.getConnectycubeChatDialogDataManager().createOrUpdate(qbDialog);
        }
    }

    public static void saveTempMessages(DataManager dataManager, List<ConnectycubeChatDialog > connectycubeChatDialogsList,
            ConnectycubeChatDialog  currentDialog) {
        dataManager.getMessageDataManager()
                .createOrUpdateAll(ChatUtils.createTempLocalMessagesList(dataManager, connectycubeChatDialogsList, currentDialog));
    }

    public static void saveTempMessagesUnread(DataManager dataManager, List<ConnectycubeChatDialog > connectycubeChatDialogsList,
                                              ConnectycubeChatDialog  currentDialog) {
        List<Message> messageList = ChatUtils.createTempUnreadMessagesList(dataManager, connectycubeChatDialogsList, currentDialog);
        dataManager.getMessageDataManager()
                .createOrUpdateAll(messageList);
    }

    public static void saveTempMessage(DataManager dataManager, Message message) {
        dataManager.getMessageDataManager().createOrUpdate(message);
        updateDialogModifiedDate(dataManager, message.getDialogOccupant().getDialog().getDialogId(),
                message.getCreatedDate(), true);
    }

    public static List<DialogOccupant> saveDialogsOccupants(DataManager dataManager, ConnectycubeChatDialog qbDialog, boolean onlyNewOccupant) {
        List<DialogOccupant> dialogOccupantsList = ChatUtils.createDialogOccupantsList(dataManager, qbDialog, onlyNewOccupant);
        if (!dialogOccupantsList.isEmpty()) {
            dataManager.getDialogOccupantDataManager().createOrUpdateAll(dialogOccupantsList);
        }
        return dialogOccupantsList;
    }

    public static void saveDialogOccupant(DataManager dataManager, DialogOccupant dialogOccupant) {
        dataManager.getDialogOccupantDataManager().createOrUpdate(dialogOccupant);
    }

    public static void saveDialogOccupant(DataManager dataManager, DialogOccupant dialogOccupant, boolean notify) {
        dataManager.getDialogOccupantDataManager().createOrUpdate(dialogOccupant, notify);
    }

    public static void saveDialogsOccupants(DataManager dataManager, List<ConnectycubeChatDialog> qbDialogsList) {
        for (ConnectycubeChatDialog qbDialog : qbDialogsList) {
            saveDialogsOccupants(dataManager, qbDialog, false);
        }
    }

    public static void updateStatusMessageLocal(DataManager dataManager, Message message) {
        dataManager.getMessageDataManager().update(message, false);
    }

    public static void updateStatusMessagesLocal(DataManager dataManager, List<Message> messages) {
        dataManager.getMessageDataManager().updateAll(messages);
    }

    public static void updateStatusNotificationMessageLocal(DataManager dataManager,
            DialogNotification dialogNotification) {
        Log.i(TAG, "update status msg" + dialogNotification);
        dataManager.getDialogNotificationDataManager().update(dialogNotification, false);
    }

    public static void updateStatusNotificationsLocal(DataManager dataManager,
            List<DialogNotification> dialogNotifications) {
        dataManager.getDialogNotificationDataManager().updateAll(dialogNotifications);
    }

    public static void updateStatusMessageLocal(DataManager dataManager, String messageId, State state) {
        Message message = dataManager.getMessageDataManager().getByMessageId(messageId);
        if (message != null && !state.equals(message.getState())) {
            message.setState(state);
            dataManager.getMessageDataManager().update(message);
        }
    }

    public static void saveMessagesToCache(Context context, DataManager dataManager,
            List<ConnectycubeChatMessage> qbMessagesList, String dialogId) {
        for (int i = 0; i < qbMessagesList.size(); i++) {
            ConnectycubeChatMessage qbChatMessage = qbMessagesList.get(i);


            State msgState = State.SYNC;
            if (!CollectionsUtil.isEmpty(qbChatMessage.getReadIds())){

                msgState = qbChatMessage.getReadIds().contains
                        (AppSession.getSession().getUser().getId()) ? State.READ : State.SYNC;
            }

            saveMessageOrNotificationToCache(context, dataManager, dialogId, qbChatMessage, msgState, false);
        }
        updateDialogModifiedDate(dataManager, dialogId, false);
    }

    public static void saveMessageOrNotificationToCache(Context context, DataManager dataManager,
            String dialogId, ConnectycubeChatMessage qbChatMessage, State state, boolean notify) {
        DialogOccupant dialogOccupant;
        if (qbChatMessage.getSenderId() == null) {
            dialogOccupant = dataManager.getDialogOccupantDataManager()
                    .getDialogOccupant(dialogId, AppSession.getSession().getUser().getId());
        } else {
            dialogOccupant = dataManager.getDialogOccupantDataManager()
                    .getDialogOccupant(dialogId, qbChatMessage.getSenderId());
        }

        if (dialogOccupant == null && qbChatMessage.getSenderId() != null) {
            saveDialogOccupantIfUserNotExists(dataManager, dialogId, qbChatMessage.getSenderId(),
                    DialogOccupant.Status.DELETED);
            dialogOccupant = dataManager.getDialogOccupantDataManager()
                    .getDialogOccupant(dialogId, qbChatMessage.getSenderId());
        }

        if (ChatNotificationUtils.isNotificationMessage(qbChatMessage)) {
            saveDialogNotificationToCache(context, dataManager, dialogOccupant, qbChatMessage, notify);
        } else {
            Message message = ChatUtils.createLocalMessage(qbChatMessage, dialogOccupant, state);
            if (qbChatMessage.getAttachments() != null && !qbChatMessage.getAttachments().isEmpty()) {
                ArrayList<ConnectycubeAttachment> attachmentsList = new ArrayList<ConnectycubeAttachment>(
                        qbChatMessage.getAttachments());
                Attachment attachment = ChatUtils.createLocalAttachment(attachmentsList.get(0), context, message.getMessageId().hashCode());
                message.setAttachment(attachment);
                dataManager.getAttachmentDataManager().createOrUpdate(attachment, notify);
            }

            dataManager.getMessageDataManager().createOrUpdate(message, notify);
        }
    }

    public static void updateDialogModifiedDate(DataManager dataManager, String dialogId, long modifiedDate,
            boolean notify) {
        ConnectycubeChatDialog dialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        updateDialogModifiedDate(dataManager, dialog, modifiedDate, notify);
    }

    private static void updateDialogModifiedDate(DataManager dataManager, String dialogId, boolean notify) {
        long modifiedDate = getDialogModifiedDate(dataManager, dialogId);
        updateDialogModifiedDate(dataManager, dialogId, modifiedDate, notify);
    }

    public static void updateDialogModifiedDate(DataManager dataManager, ConnectycubeChatDialog dialog, long modifiedDate,
            boolean notify) {
        if (dialog != null) {
            dialog.setLastMessageDateSent(modifiedDate);
            dataManager.getConnectycubeChatDialogDataManager().update(dialog, notify);
        }
    }

    public static long getDialogModifiedDate(DataManager dataManager, String dialogId) {
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(dialogId);
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);

        Message message = dataManager.getMessageDataManager()
                .getLastMessageByOccupantsId(dialogOccupantsIdsList);
        DialogNotification dialogNotification = dataManager.getDialogNotificationDataManager()
                .getLastDialogNotificationByDialogId(dialogOccupantsIdsList);

        return ChatUtils.getDialogMessageCreatedDate(true, message, dialogNotification);
    }

    public static void saveDialogNotificationToCache(Context context, DataManager dataManager,
            DialogOccupant dialogOccupant, ConnectycubeChatMessage qbChatMessage, boolean notify) {
        DialogNotification dialogNotification = ChatUtils.createLocalDialogNotification(context, dataManager,
                qbChatMessage, dialogOccupant);
        saveDialogNotificationToCache(dataManager, dialogNotification, notify);
    }

    private static void saveDialogNotificationToCache(DataManager dataManager,
            DialogNotification dialogNotification, boolean notify) {
        if (dialogNotification.getDialogOccupant() != null) {
            dataManager.getDialogNotificationDataManager().createOrUpdate(dialogNotification, notify);
        }
    }

    public static void deleteDialogLocal(DataManager dataManager, String dialogId) {
        dataManager.getConnectycubeChatDialogDataManager().deleteById(dialogId);
    }

    public static void updateDialogOccupants(DataManager dataManager, String dialogId,
            List<Integer> dialogOccupantIdsList, DialogOccupant.Status status) {
        List<DialogOccupant> dialogOccupantsList = ChatUtils.
                getUpdatedDialogOccupantsList(dataManager, dialogId, dialogOccupantIdsList, status);
        dataManager.getDialogOccupantDataManager().createOrUpdateAll(dialogOccupantsList);
    }

    public static void updateDialogOccupant(DataManager dataManager, String dialogId,
            int occupantId, DialogOccupant.Status status) {
        DialogOccupant dialogOccupant = ChatUtils.getUpdatedDialogOccupant(dataManager, dialogId, status,
                occupantId);
        dataManager.getDialogOccupantDataManager().update(dialogOccupant);
    }

    public static void updateDialogsOccupantsStatusesIfNeeded(DataManager dataManager, List<ConnectycubeChatDialog> qbDialogsList) {
        for (ConnectycubeChatDialog qbDialog : qbDialogsList) {
            updateDialogOccupantsStatusesIfNeeded(dataManager, qbDialog);
        }
    }

    public static void updateDialogOccupantsStatusesIfNeeded(DataManager dataManager, ConnectycubeChatDialog qbDialog) {
        List<DialogOccupant> oldDialogOccupantsList = dataManager.getDialogOccupantDataManager().getDialogOccupantsListByDialogId(qbDialog.getDialogId());
        List<DialogOccupant> updatedDialogOccupantsList = new ArrayList<>();
        List<DialogOccupant> newDialogOccupantsList = dataManager.getDialogOccupantDataManager().getActualDialogOccupantsByIds(
                qbDialog.getDialogId(), qbDialog.getOccupants());

        for (DialogOccupant oldDialogOccupant : oldDialogOccupantsList) {
            if (!newDialogOccupantsList.contains(oldDialogOccupant)) {
                oldDialogOccupant.setStatus(DialogOccupant.Status.DELETED);
                updatedDialogOccupantsList.add(oldDialogOccupant);
            }
        }

        if (!updatedDialogOccupantsList.isEmpty()) {
            dataManager.getDialogOccupantDataManager().updateAll(updatedDialogOccupantsList);
        }
    }
}