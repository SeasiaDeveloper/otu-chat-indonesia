package com.quickblox.q_municate_core.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.connectycube.auth.session.ConnectycubeSettings;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeChatMessage;
import com.connectycube.core.helper.CollectionsUtil;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.models.NotificationType;
import com.quickblox.q_municate_core.models.ParcelableQBDialog;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.qb.helpers.QBRestHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.ui.chatmessage.adapter.utils.LocationUtils;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatUtils {
    private static final String TAG = ChatUtils.class.getSimpleName();

    public static final String OCCUPANT_IDS_DIVIDER = ",";

    public static String getAttachUrlFromMessage(Collection<ConnectycubeAttachment> attachmentsCollection) {
        if (!CollectionsUtil.isEmpty(attachmentsCollection)) {
            ArrayList<ConnectycubeAttachment> attachmentsList = new ArrayList<>(attachmentsCollection);
            return attachmentsList.get(0).getUrl();
        }
        return ConstsCore.EMPTY_STRING;
    }

    public static String getAttachUrlIfExists(ConnectycubeChatMessage chatMessage) {
        String attachURL = ConstsCore.EMPTY_STRING;
        Collection<ConnectycubeAttachment> attachmentCollection = chatMessage.getAttachments();
        if (!CollectionsUtil.isEmpty(attachmentCollection)) {
            attachURL = getAttachUrlFromMessage(attachmentCollection);
        }
        return attachURL;
    }

    public static String getSelectedFriendsFullNamesFromMap(List<QMUser> usersList) {
        if (usersList.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (QMUser user : usersList) {
            stringBuilder.append(user.getFullName()).append(OCCUPANT_IDS_DIVIDER).append(" ");
        }

        return stringBuilder.toString().substring(ConstsCore.ZERO_INT_VALUE, stringBuilder.length() - 2);
    }

    public static ArrayList<Integer> createOccupantsIdsFromPrivateMessage(int currentUserId, int senderId) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(2);
        occupantsIdsList.add(currentUserId);
        occupantsIdsList.add(senderId);
        return occupantsIdsList;
    }

    public static List<Integer> getOccupantsIdsListFromString(String occupantIds) {
        List<Integer> occupantIdsList = new ArrayList<>();
        String[] occupantIdsArray = occupantIds.split(OCCUPANT_IDS_DIVIDER);
        for (String occupantId : occupantIdsArray) {
            occupantIdsList.add(Integer.valueOf(occupantId));
        }
        return occupantIdsList;
    }

    public static List<Integer> getOccupantIdsWithUser(List<Integer> friendIdsList) {
        ConnectycubeUser user = AppSession.getSession().getUser();
        ArrayList<Integer> occupantIdsList = new ArrayList<>(friendIdsList);
        occupantIdsList.add(user.getId());
        return occupantIdsList;
    }

    public static String getOccupantsIdsStringFromList(Collection<Integer> occupantIdsList) {
        return TextUtils.join(OCCUPANT_IDS_DIVIDER, occupantIdsList);
    }

    public static ConnectycubeChatDialog getExistPrivateDialog(DataManager dataManager, int opponentId) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantForPrivateChat(opponentId);

        if (dialogOccupant != null) {
            return dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogOccupant.getDialog().getDialogId());
        } else {
            return null;
        }
    }

    public static String getFullNameById(DataManager dataManager, int userId) {
        QMUser user = QMUserService.getInstance().getUserCache().get((long)userId);
        return user != null ? user.getFullName() : "";
    }

    public static String getFullNamesFromOpponentIds(DataManager dataManager, String occupantsIdsString) {
        List<Integer> occupantsIdsList = getOccupantsIdsListFromString(occupantsIdsString);
        return getFullNamesFromOpponentIdsList(dataManager, occupantsIdsList);
    }

    public static String getFullNamesFromOpponentId(DataManager dataManager, Integer userId,
                                                    String occupantsIdsString) {
        List<Integer> occupantsIdsList = getOccupantsIdsListFromString(occupantsIdsString);
        occupantsIdsList.remove(userId);
        return getFullNamesFromOpponentIdsList(dataManager, occupantsIdsList);
    }

    private static String getFullNamesFromOpponentIdsList(DataManager dataManager, List<Integer> occupantsIdsList) {
        StringBuilder stringBuilder = new StringBuilder(occupantsIdsList.size());
        for (Integer id : occupantsIdsList) {
            stringBuilder.append(getFullNameById(dataManager, id)).append(OCCUPANT_IDS_DIVIDER).append(" ");
        }
        return stringBuilder.toString().substring(ConstsCore.ZERO_INT_VALUE, stringBuilder.length() - 2);
    }

    public static List<ParcelableQBDialog> qBDialogsToParcelableQBDialogs(List<ConnectycubeChatDialog> dialogList) {
        List<ParcelableQBDialog> parcelableDialogList = new ArrayList<ParcelableQBDialog>(dialogList.size());
        for (ConnectycubeChatDialog dialog : dialogList) {
            ParcelableQBDialog parcelableConnectycubeChatDialog = new ParcelableQBDialog(dialog);
            parcelableDialogList.add(parcelableConnectycubeChatDialog);
        }
        return parcelableDialogList;
    }


    public static List<ConnectycubeChatDialog> parcelableQBDialogsToQBDialogs(List<ParcelableQBDialog> parcelableQBDialogsList) {
        List<ConnectycubeChatDialog> qbDialogsList = new ArrayList<ConnectycubeChatDialog>(parcelableQBDialogsList.size());
        for (ParcelableQBDialog parcelableQBDialog : parcelableQBDialogsList) {
            ConnectycubeChatDialog qbDialog = parcelableQBDialog.getDialog();
            qbDialogsList.add(qbDialog);
        }
        return qbDialogsList;
    }

    public static QMUser getOpponentFromPrivateDialog(QMUser currentUser, List<DialogOccupant> occupantsList) {
        for (DialogOccupant dialogOccupant : occupantsList) {
            if (dialogOccupant != null && dialogOccupant.getUser() != null
                    && currentUser.getId().intValue() != dialogOccupant.getUser().getId().intValue()) {
                return dialogOccupant.getUser();
            }
        }
        return new QMUser();
    }

    private static int getRandomUserFromOccupantsList(List<Integer> occupantsList) {
        for (Integer id : occupantsList) {
            if (!id.equals(AppSession.getSession().getUser().getId())) {
                return id;
            }
        }
        return 0;
    }

    public static List<Message> createTempLocalMessagesList(DataManager dataManager,
                                                            List<ConnectycubeChatDialog> qbDialogsList, ConnectycubeChatDialog currentDialog) {
        List<Message> messagesList = new ArrayList<>();

        for (ConnectycubeChatDialog qbDialog : qbDialogsList) {
            // dialog is opened
            Log.d(TAG, "Fix double message currentDialog = " + currentDialog);
            if (currentDialog != null && qbDialog.getDialogId().equals(currentDialog.getDialogId())) {
                Log.d(TAG, "Fix double message currentDialog = " + currentDialog + " currentDialogID = " + currentDialog.getDialogId());
                continue;
            }

            DialogOccupant dialogOccupant = getDialogOccupant(dataManager, qbDialog);

            long tempMessageId = qbDialog.getDialogId().hashCode();
            Message message = createTempLocalMessage(tempMessageId, dialogOccupant, qbDialog.getLastMessage(), qbDialog.getLastMessageDateSent(), State.TEMP_LOCAL, qbDialog.getDialogId());
            messagesList.add(message);
        }
        return messagesList;
    }

    public static List<Message> createTempUnreadMessagesList(DataManager dataManager,
                                                             List<ConnectycubeChatDialog> qbDialogsList, ConnectycubeChatDialog currentDialog) {
        List<Message> messagesList = new ArrayList<>();

        for (ConnectycubeChatDialog qbDialog : qbDialogsList) {
            if (currentDialog != null && qbDialog.getDialogId().equals(currentDialog.getDialogId())) {
                Log.d(TAG, "currentDialog = " + currentDialog + " currentDialogID = " + currentDialog.getDialogId());
                continue;
            }

            Log.d(TAG, "qbDialog.getUnreadMessageCount= " + qbDialog.getUnreadMessageCount());

            DialogOccupant dialogOccupant = getDialogOccupant(dataManager, qbDialog);

            int unreadCount = qbDialog.getUnreadMessageCount();
            long tempMessageId = qbDialog.getDialogId().hashCode();
            long createdData = qbDialog.getLastMessageDateSent();

            for (int i = 0; i < unreadCount; i++) {
                Message message = createTempLocalMessage(tempMessageId++, dialogOccupant, qbDialog.getLastMessage(), createdData++, State.TEMP_LOCAL_UNREAD, qbDialog.getDialogId());
                messagesList.add(message);
            }
        }

        return messagesList;
    }

    private static DialogOccupant getDialogOccupant(DataManager dataManager, ConnectycubeChatDialog qbDialog) {
        int randomUserId = getRandomUserFromOccupantsList(qbDialog.getOccupants());
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager()
                .getDialogOccupant(qbDialog.getDialogId(), randomUserId);

        boolean onlyMeInDialog = qbDialog.getOccupants().size() == 1;

        if (dialogOccupant == null && onlyMeInDialog) {
            QMUser user = QMUserService.getInstance().getUserCache().get((long) AppSession.getSession().getUser().getId());
            DbUtils.saveDialogOccupant(dataManager,
                    createDialogOccupant(dataManager, qbDialog.getDialogId(), user), false);
            dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(qbDialog.getDialogId(), AppSession.getSession().getUser().getId());
        }
        return dialogOccupant;
    }

    public static Message createTempLocalMessage(long messageId, DialogOccupant dialogOccupant, String body, State state) {
        Message message = new Message();
        message.setMessageId(String.valueOf(messageId));
        message.setDialogOccupant(dialogOccupant);
        message.setState(state);
        message.setBody(body);
        return message;
    }

    public static Message createTempLocalMessage(DialogNotification dialogNotification) {
        Message message = new Message();
        message.setMessageId(dialogNotification.getDialogNotificationId());
        message.setDialogOccupant(dialogNotification.getDialogOccupant());
        message.setState(State.TEMP_LOCAL_UNREAD);
        message.setBody(dialogNotification.getBody());
        message.setCreatedDate(dialogNotification.getCreatedDate());
        return message;
    }

    private static Message createTempLocalMessage(long messageId, DialogOccupant dialogOccupant, String body, long createdDate, State state, String dialogId) {
        Message message = createTempLocalMessage(messageId, dialogOccupant, body, state);
        message.setCreatedDate(createdDate);
        message.setDialogId(dialogId);
        return message;
    }

    public static List<DialogOccupant> createDialogOccupantsList(DataManager dataManager, ConnectycubeChatDialog qbDialog, boolean onlyNewOccupant) {
        List<DialogOccupant> dialogOccupantsList = new ArrayList<>(qbDialog.getOccupants().size());

        List<Integer> userIdsForSave = new ArrayList<>();
        List<QMUser> qmUsers = new ArrayList<>();

        for (Integer userId : qbDialog.getOccupants()) {
            DialogOccupant dialogOccupant;
            if (onlyNewOccupant) {
                dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(qbDialog.getDialogId(), userId);
                if (dialogOccupant == null) {
                    dialogOccupant = createDialogOccupant(dataManager, qbDialog.getDialogId(), QMUserService.getInstance().getUserCache().get((long) userId));
                } else {
                    dialogOccupant.setStatus(DialogOccupant.Status.ACTUAL);
                }
                dialogOccupantsList.add(dialogOccupant);
            } else {
                dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(qbDialog.getDialogId(), userId);
                if (dialogOccupant == null) {
                    QMUser user = QMUserService.getInstance().getUserCache().get((long) userId);
                    if (user == null) {
                        userIdsForSave.add(userId);
                    } else {
                        qmUsers.add(user);
                    }
                }
            }
        }

        if (!userIdsForSave.isEmpty()) {
            qmUsers.addAll(QBRestHelper.loadAndSaveUserByIds(userIdsForSave));
        }
        for (QMUser qmUser : qmUsers) {
            dialogOccupantsList.add(createDialogOccupant(dataManager, qbDialog.getDialogId(), qmUser));
        }

        return dialogOccupantsList;
    }

    public static List<Long> getIdsFromDialogOccupantsList(List<DialogOccupant> dialogOccupantsList) {
        List<Long> idsList = new ArrayList<>(dialogOccupantsList.size());

        for (DialogOccupant dialogOccupant : dialogOccupantsList) {
            idsList.add(dialogOccupant.getDialogOccupantId());
        }

        return idsList;
    }

    public static List<Integer> getUsersIdsFromDialogOccupantsList(List<DialogOccupant> dialogOccupantsList) {
        List<Integer> idsList = new ArrayList<>(dialogOccupantsList.size());

        for (DialogOccupant dialogOccupant : dialogOccupantsList) {
            idsList.add(dialogOccupant.getUser().getId());
        }

        return idsList;
    }

    public static ArrayList<Integer> createOccupantsIdsFromDialogOccupantsList(
            List<DialogOccupant> dialogOccupantsList) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(dialogOccupantsList.size());
        for (DialogOccupant dialogOccupant : dialogOccupantsList) {
            occupantsIdsList.add(dialogOccupant.getUser().getId());
        }
        return occupantsIdsList;
    }

    public static ArrayList<Integer> createOccupantsIdsFromUsersList(
            List<QMUser> usersList) {
        ArrayList<Integer> occupantsIdsList = new ArrayList<>(usersList.size());
        for (QMUser user : usersList) {
            occupantsIdsList.add(user.getId());
        }
        return occupantsIdsList;
    }

    public static Message createLocalMessage(ConnectycubeChatMessage qbChatMessage, DialogOccupant dialogOccupant, State state) {
        long dateSent = getMessageDateSent(qbChatMessage);
        Message message = new Message();
        message.setMessageId(qbChatMessage.getId());
        message.setDialogOccupant(dialogOccupant);
        message.setCreatedDate(dateSent);

        if (messageIsRead(qbChatMessage)) {
            state = State.READ;
        } else if (messageIsDelivered(qbChatMessage)) {
            state = State.DELIVERED;
        }

        message.setState(state);
        message.setDialogId(qbChatMessage.getDialogId());
        message.setBody(qbChatMessage.getBody());
        if(qbChatMessage.getProperties() != null && qbChatMessage.getProperties().containsKey("replyMessage")) {
            String value = qbChatMessage.getProperties().get("replyMessage");
            if (value != null) {
                message.setPROPERTY_REPLY_MESSAGE(value);
            }
        }
        return message;
    }

    private static boolean messageIsRead(ConnectycubeChatMessage qbChatMessage) {
        return qbChatMessage.getReadIds() != null
                && qbChatMessage.getReadIds().contains(qbChatMessage.getRecipientId());
    }

    private static boolean messageIsDelivered(ConnectycubeChatMessage qbChatMessage) {
        return qbChatMessage.getDeliveredIds() != null
                && qbChatMessage.getDeliveredIds().contains(qbChatMessage.getRecipientId());
    }

    public static long getMessageDateSent(ConnectycubeChatMessage qbChatMessage) {
        long dateSent;
        String dateSentString = (String) qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_DATE_SENT);
        try {
            dateSent = dateSentString != null ? Long.parseLong(dateSentString) : qbChatMessage.getDateSent();
        } catch (NumberFormatException e) {
            dateSent = DateUtilsCore.getCurrentTime();
        }
        return dateSent;
    }

    public static String getRoomJid(String dialogId) {
        return ConnectycubeSettings.getInstance().getApplicationId()
                .concat("_")
                .concat(dialogId)
                .concat(ConstsCore.CHAT_MUC)
                .concat(ConnectycubeSettings.getInstance().getChatEndpoint());
    }

    public static Attachment createLocalAttachment(ConnectycubeAttachment connectycubeAttachment, Context context, int internalAttachmentId) {
        Attachment attachment = new Attachment();
        String remoteUrl = connectycubeAttachment.getUrl();
        if (connectycubeAttachment.getType().equalsIgnoreCase(connectycubeAttachment.LOCATION_TYPE)) {
            attachment.setType(Attachment.Type.LOCATION);
            attachment.setAdditionalInfo(connectycubeAttachment.getData());
            remoteUrl = LocationUtils.getRemoteUri(connectycubeAttachment.getData(), LocationUtils.defaultUrlLocationParams(context));
        } else if (connectycubeAttachment.getType().equalsIgnoreCase(Attachment.Type.CONTACT.toString())) {
            attachment.setType(Attachment.Type.CONTACT);
            attachment.setAdditionalInfo(connectycubeAttachment.getData());
        } else if (connectycubeAttachment.getType().equalsIgnoreCase(connectycubeAttachment.IMAGE_TYPE)) {
            attachment.setType(Attachment.Type.IMAGE);
        } else {
            attachment.setType(Attachment.Type.valueOf(connectycubeAttachment.getType().toUpperCase()));
        }

        if (connectycubeAttachment.getId() == null) {
            connectycubeAttachment.setId(String.valueOf(internalAttachmentId));
        }
        attachment.setAttachmentId(connectycubeAttachment.getId());
        attachment.setRemoteUrl(remoteUrl);
        attachment.setName(connectycubeAttachment.getName());
        attachment.setSize(connectycubeAttachment.getSize());
        attachment.setHeight(connectycubeAttachment.getHeight());
        attachment.setWidth(connectycubeAttachment.getWidth());
        attachment.setDuration(connectycubeAttachment.getDuration());
        return attachment;
    }


    public static DialogNotification createLocalDialogNotification(Context context, DataManager dataManager,
                                                                   ConnectycubeChatMessage qbChatMessage,
                                                                   DialogOccupant dialogOccupant) {
        DialogNotification dialogNotification = new DialogNotification();
        dialogNotification.setDialogNotificationId(qbChatMessage.getId());
        dialogNotification.setDialogOccupant(dialogOccupant);

        String chatNotificationTypeString = qbChatMessage.getProperty(ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE).toString();

        if (chatNotificationTypeString != null) {

            int chatNotificationTypeInt = Integer.parseInt(chatNotificationTypeString);
            NotificationType chatNotificationType = NotificationType.parseByValue(chatNotificationTypeInt);
            DialogNotification.Type dialogNotificationTypeLocal = DialogNotification.Type
                    .parseByCode(chatNotificationTypeInt);

            switch (chatNotificationType) {
                case GROUP_CHAT_CREATE:
                case GROUP_CHAT_UPDATE:
                    dialogNotification.setType(
                            ChatNotificationUtils.getUpdateChatLocalNotificationType(qbChatMessage));
                    dialogNotification.setBody(ChatNotificationUtils
                            .getBodyForUpdateChatNotificationMessage(context, dataManager, qbChatMessage));
                    break;
                case FRIENDS_REQUEST:
                case FRIENDS_ACCEPT:
                case FRIENDS_REJECT:
                case FRIENDS_REMOVE:
                    dialogNotification.setType(dialogNotificationTypeLocal);
                    dialogNotification.setBody(ChatNotificationUtils
                            .getBodyForFriendsNotificationMessage(context, dataManager,
                                    dialogNotificationTypeLocal, qbChatMessage));
                    break;
            }
        }

        long dateSent = getMessageDateSent(qbChatMessage);
        dialogNotification.setCreatedDate(dateSent);

        State msgState = State.DELIVERED;
        if (!CollectionsUtil.isEmpty(qbChatMessage.getReadIds())){
            msgState = qbChatMessage.getReadIds().contains
                    (AppSession.getSession().getUser().getId()) ? State.READ : State.DELIVERED;
        }

        dialogNotification.setState(msgState);

        return dialogNotification;
    }

    public static List<CombinationMessage> getCombinationMessagesListFromMessagesList(List<Message> messagesList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>(messagesList.size());

        for (Message message : messagesList) {
            combinationMessagesList.add(new CombinationMessage(message));
        }

        return combinationMessagesList;
    }

    public static List<CombinationMessage> getCombinationMessagesListFromDialogNotificationsList(List<DialogNotification> dialogNotificationsList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>(dialogNotificationsList.size());

        for (DialogNotification dialogNotification : dialogNotificationsList) {
            combinationMessagesList.add(new CombinationMessage(dialogNotification));
        }

        return combinationMessagesList;
    }

    public static DialogNotification convertMessageToDialogNotification(Message message) {
        DialogNotification dialogNotification = new DialogNotification();
        dialogNotification.setDialogNotificationId(message.getMessageId());
        dialogNotification.setDialogOccupant(message.getDialogOccupant());
        dialogNotification.setBody(message.getBody());
        dialogNotification.setCreatedDate(message.getCreatedDate());
        dialogNotification.setState(message.getState());
        return dialogNotification;
    }

    public static List<DialogNotification> readAllDialogNotification(List<DialogNotification> dialogNotificationsList, ConnectycubeUser currentConnectycubeUser) {
        List<DialogNotification> updateDialogNotificationsList = new ArrayList<>(dialogNotificationsList.size());

        for (DialogNotification dialogNotification : dialogNotificationsList) {
            if (!State.READ.equals(dialogNotification.getState())
                    && currentConnectycubeUser.getId().intValue() != dialogNotification.getDialogOccupant().getUser().getId().intValue()) {
                dialogNotification.setState(State.READ);
                updateDialogNotificationsList.add(dialogNotification);
            }
        }

        return updateDialogNotificationsList;
    }

    public static List<Message> readAllMessages(List<Message> messagesList, ConnectycubeUser currentConnectycubeUser) {
        List<Message> updateMessagesList = new ArrayList<>(messagesList.size());

        for (Message message : messagesList) {
            if (!State.READ.equals(message.getState())
                    && currentConnectycubeUser.getId().intValue() != message.getDialogOccupant().getUser().getId().intValue()) {
                message.setState(State.READ);
                updateMessagesList.add(message);
            }
        }

        return updateMessagesList;
    }

    public static List<CombinationMessage> createCombinationMessagesList(List<Message> messagesList,
                                                                         List<DialogNotification> dialogNotificationsList) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>();
        combinationMessagesList.addAll(getCombinationMessagesListFromMessagesList(messagesList));
        combinationMessagesList.addAll(getCombinationMessagesListFromDialogNotificationsList(
                dialogNotificationsList));
        Collections.sort(combinationMessagesList, new CombinationMessage.DateComparator());
        return combinationMessagesList;
    }

    public static List<CombinationMessage> createLimitedCombinationMessagesList(List<Message> messagesList,
                                                                                List<DialogNotification> dialogNotificationsList, int limit) {
        List<CombinationMessage> combinationMessagesList = new ArrayList<>();
        combinationMessagesList.addAll(getCombinationMessagesListFromMessagesList(messagesList));
        combinationMessagesList.addAll(getCombinationMessagesListFromDialogNotificationsList(
                dialogNotificationsList));
        Collections.sort(combinationMessagesList, new CombinationMessage.DateComparator());

        int indexStart = combinationMessagesList.size() < limit ? 0 : combinationMessagesList.size() - limit;
        int indexEnd = combinationMessagesList.size();

        return combinationMessagesList.subList(indexStart, indexEnd);
    }

    public static String getDialogLastMessage(String defaultLasMessage, Message message, DialogNotification dialogNotification) {
        String lastMessage = "";

        if (message == null && dialogNotification != null) {
            lastMessage = defaultLasMessage;
        } else if (dialogNotification == null && message != null) {
            lastMessage = getAbbreviatedLastMessage(message.getBody());
        } else if (message != null && dialogNotification != null) {
            lastMessage = message.getCreatedDate() > dialogNotification.getCreatedDate()
                    ? getAbbreviatedLastMessage(message.getBody()) : defaultLasMessage;
        }

        return lastMessage;
    }

    private static String getAbbreviatedLastMessage(String fullMessage){
        if (fullMessage == null){
            return "";
        }

        return fullMessage.substring(0,
                fullMessage.length() > ConstsCore.LAST_MESSAGE_LENGTH
                        ? ConstsCore.LAST_MESSAGE_LENGTH
                        : fullMessage.length());
    }

    public static long getDialogMessageCreatedDate(boolean lastMessage, Message message, DialogNotification dialogNotification) {
        long createdDate = 0;

        if (message == null && dialogNotification == null) {
            createdDate = 0;
        } else if (message != null && dialogNotification != null) {
            createdDate = lastMessage
                    ? (message.getCreatedDate() > dialogNotification.getCreatedDate() ? message.getCreatedDate() : dialogNotification.getCreatedDate())
                    : (message.getCreatedDate() < dialogNotification.getCreatedDate() ? message.getCreatedDate() : dialogNotification.getCreatedDate());
        } else if (message != null && dialogNotification == null) {
            createdDate = message.getCreatedDate();
        } else if (dialogNotification != null && message == null) {
            createdDate = dialogNotification.getCreatedDate();
        }

        return createdDate;
    }

    public static int getOnlineDialogOccupantsCount(QBFriendListHelper friendListHelper, List<Integer> occupantIdsList) {
        int onlineOccupantsCount = 0;

        for (int userId : occupantIdsList) {
            if (userId == AppSession.getSession().getUser().getId() || friendListHelper.isUserOnline(userId)) {
                onlineOccupantsCount++;
            }
        }

        return onlineOccupantsCount;
    }

    public static List<DialogOccupant> getUpdatedDialogOccupantsList(DataManager dataManager, String dialogId, List<Integer> dialogOccupantIdsList, DialogOccupant.Status status) {
        List<DialogOccupant> updatedDialogOccupantsList = new ArrayList<>(dialogOccupantIdsList.size());

        for (Integer userId : dialogOccupantIdsList) {
            QMUser user = QMUserService.getInstance().getUserCache().get((long) userId);
            if (user == null) {
                user = QBRestHelper.loadAndSaveUser(userId);
            }

            DialogOccupant dialogOccupant = getUpdatedDialogOccupant(dataManager, dialogId, status, userId);
            if (dialogOccupant == null) {
                dialogOccupant = createDialogOccupant(dataManager, dialogId, user);
                dialogOccupant.setStatus(status);
            }

            updatedDialogOccupantsList.add(dialogOccupant);
        }

        return updatedDialogOccupantsList;
    }

    public static DialogOccupant getUpdatedDialogOccupant(DataManager dataManager, String dialogId,
                                                          DialogOccupant.Status status, Integer userId) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(dialogId, userId);
        if (dialogOccupant != null) {
            dialogOccupant.setStatus(status);
        }
        return dialogOccupant;
    }

    public static DialogOccupant createDialogOccupant(DataManager dataManager, String dialogId, QMUser user) {
        DialogOccupant dialogOccupant = new DialogOccupant();
        dialogOccupant.setUser(user);
        dialogOccupant.setDialog(dataManager.getDialogDataManager().getByDialogId(dialogId));
        return dialogOccupant;
    }

    public static void addOccupantsToQBDialog(ConnectycubeChatDialog qbDialog, ConnectycubeChatMessage qbChatMessage) {
        qbDialog.setOccupantsIds(new ArrayList<Integer>(2));
        qbDialog.getOccupants().add(qbChatMessage.getSenderId());
        qbDialog.getOccupants().add(qbChatMessage.getRecipientId());
    }
}