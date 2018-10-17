package com.quickblox.q_municate_core.qb.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.connectycube.chat.JIDHelper;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.ConnectycubeRestChatService;
import com.connectycube.chat.SystemMessagesManager;
import com.connectycube.chat.exception.ChatException;
import com.connectycube.chat.listeners.ChatDialogMessageListener;
import com.connectycube.chat.listeners.ChatDialogParticipantListener;
import com.connectycube.chat.listeners.ChatDialogTypingListener;
import com.connectycube.chat.listeners.MessageStatusListener;
import com.connectycube.chat.listeners.SystemMessageListener;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatMessage;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.chat.model.ConnectycubePresence;
import com.connectycube.chat.utils.DialogUtils;
import com.connectycube.storage.ConnectycubeStorage;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.helper.CollectionsUtil;
import com.connectycube.core.helper.StringifyArrayList;
import com.connectycube.core.request.RequestGetBuilder;
import com.connectycube.core.request.RequestUpdateBuilder;
import com.quickblox.q_municate_core.R;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.models.NotificationType;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatNotificationUtils;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.DateUtilsCore;
import com.quickblox.q_municate_core.utils.DbUtils;
import com.quickblox.q_municate_core.utils.FinderUnknownUsers;
import com.quickblox.q_municate_core.utils.MediaUtils;
import com.quickblox.q_municate_core.utils.MimeTypeAttach;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class QBChatHelper extends BaseThreadPoolHelper{

    private static final String TAG = QBChatHelper.class.getSimpleName();


    private ConnectycubeChatService chatService;
    private ConnectycubeUser chatCreator;
    private ConnectycubeChatDialog currentDialog;
    protected DataManager dataManager;
    private TypingListener typingListener;
    private PrivateChatMessagesStatusListener privateChatMessagesStatusListener;
    private List<QBNotificationChatListener> notificationChatListeners = new CopyOnWriteArrayList<>();
    private final AllChatMessagesListener allChatMessagesListener;
    private final QMSystemMessageListener systemMessagesListener;
    private SystemMessagesManager systemMessagesManager;
    private List<ConnectycubeChatDialog> groupDialogsList;
    private ChatDialogParticipantListener participantListener;
    private final ConnectionListener chatConnectionListener;


    public QBChatHelper(Context context) {
        super(context);
        participantListener = new ParticipantListener();
        allChatMessagesListener = new AllChatMessagesListener();
        systemMessagesListener = new QMSystemMessageListener();
        typingListener = new TypingListener();
        privateChatMessagesStatusListener = new PrivateChatMessagesStatusListener();

        chatConnectionListener = new ChatConnectionListener();
        QBNotificationChatListener notificationChatListener = new PrivateChatNotificationListener();
        addNotificationChatListener(notificationChatListener);

        dataManager = DataManager.getInstance();
    }

    public synchronized void initCurrentChatDialog(ConnectycubeChatDialog dialog, Bundle additional) throws ResponseException {
        currentDialog = dialog;
        initCurrentDialogForChatIfPossible();
    }

    private void initCurrentDialogForChatIfPossible() {
        if (currentDialog != null && chatService != null && chatService.isLoggedIn()) {
            currentDialog.initForChat(chatService);
            if (ConnectycubeDialogType.GROUP.equals(currentDialog.getType())) {
                tryJoinRoomChat(currentDialog);
                currentDialog.addParticipantListener(participantListener);
            } else {
                currentDialog.addIsTypingListener(typingListener);
            }
        }
    }

    public synchronized void closeChat(ConnectycubeChatDialog chatDialog, Bundle additional) {
        if (currentDialog != null
                && currentDialog.getDialogId().equals(chatDialog.getDialogId())
                && chatService != null
                && chatService.isLoggedIn()) {
            chatDialog.initForChat(chatService);
            if (ConnectycubeDialogType.GROUP.equals(currentDialog.getType())) {
                if (currentDialog.getParticipantListeners().contains(participantListener)) {
                    currentDialog.removeParticipantListener(participantListener);
                }
            } else {
                if (currentDialog.getIsTypingListeners().contains(typingListener)) {
                    currentDialog.removeIsTypingListener(typingListener);
                }
            }

            currentDialog = null;
        }
    }

    public void initChatService() {
        Log.v(TAG, "initChatService()");
        this.chatService = ConnectycubeChatService.getInstance();
        chatService.addConnectionListener(chatConnectionListener);
    }

    public void init(ConnectycubeUser chatCreator) {
        Log.v(TAG, "init()");
        this.chatCreator = chatCreator;

        initCurrentDialogForChatIfPossible();

        initMainChatListeners();
    }

    private void initMainChatListeners() {
        chatService.getMessageStatusesManager().addMessageStatusListener(privateChatMessagesStatusListener);
        chatService.getIncomingMessagesManager().addDialogMessageListener(allChatMessagesListener);

        systemMessagesManager = ConnectycubeChatService.getInstance().getSystemMessagesManager();
        systemMessagesManager.addSystemMessageListener(systemMessagesListener);
    }

    public boolean isLoggedInToChat(){
        return chatService != null && chatService.isLoggedIn();
    }

    protected void addNotificationChatListener(QBNotificationChatListener notificationChatListener) {
        notificationChatListeners.add(notificationChatListener);
    }

    public void sendChatMessage(String message) throws ResponseException {
        ConnectycubeChatMessage qbChatMessage = getConnectycubeChatMessage(message);
        sendAndSaveChatMessage(qbChatMessage, currentDialog);
    }



    public void sendChatReplyMessage(String message, String replyMessage) throws ResponseException {
        ConnectycubeChatMessage qbChatMessage = getConnectycubeChatMessage(message);
        qbChatMessage.setProperty("replyMessage",replyMessage);
        sendAndSaveChatMessage(qbChatMessage, currentDialog);
    }


    public void sendMessageWithAttachment(Attachment.Type attachmentType, Object attachmentObject, String localPath) throws ResponseException {
        String messageBody = "";
        ConnectycubeAttachment attachment = null;
        switch (attachmentType) {
            case IMAGE:
                messageBody = context.getString(R.string.dlg_attached_last_message);
                attachment = getAttachmentImage((ConnectycubeFile) attachmentObject, localPath);
                break;
            case LOCATION:
                messageBody = context.getString(R.string.dlg_location_last_message);
                attachment = getAttachmentLocation((String) attachmentObject);
                break;
            case VIDEO:
                messageBody = context.getString(R.string.dlg_attached_video_last_message);
                attachment = getAttachmentVideo((ConnectycubeFile) attachmentObject, localPath);
                break;
            case AUDIO:
                messageBody = context.getString(R.string.dlg_attached_audio_last_message);
                attachment = getAttachmentAudio((ConnectycubeFile) attachmentObject, localPath);
                break;
            case DOC:
                break;
            case OTHER:
                break;
        }

        ConnectycubeChatMessage message = getConnectycubeChatMessage(messageBody);
        message.addAttachment(attachment);

        sendAndSaveChatMessage(message, currentDialog);
    }

    public void sendMessageReplyWithAttachment(Attachment.Type attachmentType, Object attachmentObject, String localPath, String replyMessage) throws ResponseException {
        String messageBody = "";
        ConnectycubeAttachment attachment = null;
        switch (attachmentType) {
            case IMAGE:
                messageBody = context.getString(R.string.dlg_attached_last_message);
                attachment = getAttachmentImage((ConnectycubeFile) attachmentObject, localPath);
                break;
            case LOCATION:
                messageBody = context.getString(R.string.dlg_location_last_message);
                attachment = getAttachmentLocation((String) attachmentObject);
                break;
            case VIDEO:
                messageBody = context.getString(R.string.dlg_attached_video_last_message);
                attachment = getAttachmentVideo((ConnectycubeFile) attachmentObject, localPath);
                break;
            case AUDIO:
                messageBody = context.getString(R.string.dlg_attached_audio_last_message);
                attachment = getAttachmentAudio((ConnectycubeFile) attachmentObject, localPath);
                break;
            case DOC:
                break;
            case OTHER:
                break;
        }

        ConnectycubeChatMessage message = getConnectycubeChatMessage(messageBody);
        message.setProperty("replyMessage",replyMessage);
        message.addAttachment(attachment);

        sendAndSaveChatMessage(message, currentDialog);
    }

    public void sendAndSaveChatMessage(ConnectycubeChatMessage qbChatMessage, ConnectycubeChatDialog chatDialog) throws ResponseException {
        addNecessaryPropertyForConnectycubeChatMessage(qbChatMessage, chatDialog.getDialogId());

        sendChatMessage(qbChatMessage, chatDialog);
        if (ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType())) {
            DbUtils.updateDialogModifiedDate(dataManager, chatDialog, ChatUtils.getMessageDateSent(qbChatMessage), false);
            DbUtils.saveMessageOrNotificationToCache(context, dataManager, chatDialog.getDialogId(), qbChatMessage, State.SYNC, true);
        }
    }

    public void sendChatMessage(ConnectycubeChatMessage message, ConnectycubeChatDialog chatDialog) throws ResponseException {
        message.setMarkable(true);
        chatDialog.initForChat(chatService);

        if (ConnectycubeDialogType.GROUP.equals(chatDialog.getType())) {
            if (!chatDialog.isJoined()) {
                tryJoinRoomChat(chatDialog);
            }
        }

        String error = null;
        try {
            chatDialog.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            error = context.getString(R.string.dlg_fail_connection);
        }
        if (error != null) {
            throw new ResponseException(error);
        }
    }


    protected void addNecessaryPropertyForConnectycubeChatMessage(ConnectycubeChatMessage qbChatMessage, String dialogId) {
        long time = DateUtilsCore.getCurrentTime();
        qbChatMessage.setDialogId(dialogId);
        qbChatMessage.setProperty(ChatNotificationUtils.PROPERTY_DATE_SENT, time + ConstsCore.EMPTY_STRING);
    }

    public List<ConnectycubeChatDialog> getDialogs(RequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws ResponseException {
        List<ConnectycubeChatDialog> qbDialogsList = ConnectycubeRestChatService.getChatDialogs(null, qbRequestGetBuilder).perform();
        return qbDialogsList;
    }


    public List<ConnectycubeChatMessage> getDialogMessages(RequestGetBuilder customObjectRequestBuilder,
                                                 Bundle returnedBundle, ConnectycubeChatDialog qbDialog,
                                                 long lastDateLoad) throws ResponseException {
        List<ConnectycubeChatMessage> qbMessagesList = ConnectycubeRestChatService.getDialogMessages(qbDialog,
                customObjectRequestBuilder).perform();

        if (qbMessagesList != null && !qbMessagesList.isEmpty()) {
            DbUtils.saveMessagesToCache(context, dataManager, qbMessagesList, qbDialog.getDialogId());
        }

        return qbMessagesList;
    }

    public void deleteDialog(String dialogId, ConnectycubeDialogType dialogType) {
        try {
            if (ConnectycubeDialogType.GROUP.equals(dialogType)){
                prepareAndSendNotificationsToOpponents(dialogId);
            }

            ConnectycubeRestChatService.deleteDialog(dialogId, false).perform();
        } catch (ResponseException e) {
            ErrorUtils.logError(e);
        }

        DbUtils.deleteDialogLocal(dataManager, dialogId);
    }

    public void saveDialogsToCache(List<ConnectycubeChatDialog> qbDialogsList, boolean allDialogs){
        if (qbDialogsList != null && !qbDialogsList.isEmpty()) {
            FinderUnknownUsers finderUnknownUsers = new FinderUnknownUsers(context, AppSession.getSession().getUser(), qbDialogsList);
            finderUnknownUsers.find();
            if(allDialogs) {
                DbUtils.saveDialogsToCacheAll(DataManager.getInstance(), qbDialogsList, currentDialog);
            } else {
                DbUtils.saveDialogsToCacheByIds(DataManager.getInstance(), qbDialogsList, currentDialog);
            }
            DbUtils.updateDialogsOccupantsStatusesIfNeeded(DataManager.getInstance(), qbDialogsList);
        }
    }

    private void prepareAndSendNotificationsToOpponents(String dialogId) throws ResponseException {
        ConnectycubeChatDialog storeChatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        if (storeChatDialog == null || storeChatDialog.getDialogId() == null) {
            return;
        }

        ArrayList<Integer> actualOpponentsIds =
                ChatUtils.createOccupantsIdsFromDialogOccupantsList(dataManager.getDialogOccupantDataManager()
                        .getActualDialogOccupantsByDialog(storeChatDialog.getDialogId()));
        storeChatDialog.setOccupantsIds(actualOpponentsIds);
        storeChatDialog.initForChat(ConnectycubeChatService.getInstance());

        List<Integer> occupantsIdsList = new ArrayList<>();
        occupantsIdsList.add(AppSession.getSession().getUser().getId());
        sendGroupMessageToFriends(
                storeChatDialog,
                DialogNotification.Type.OCCUPANTS_DIALOG, occupantsIdsList, true);
    }

    private ConnectycubeChatMessage getConnectycubeChatMessage(String body) {
        long time = DateUtilsCore.getCurrentTime();
        ConnectycubeChatMessage chatMessage = new ConnectycubeChatMessage();
        chatMessage.setBody(body);
        chatMessage.setProperty(ChatNotificationUtils.PROPERTY_DATE_SENT, String.valueOf(time));
        chatMessage.setSaveToHistory(ChatNotificationUtils.VALUE_SAVE_TO_HISTORY);

        return chatMessage;
    }

    private ConnectycubeAttachment getAttachment(ConnectycubeFile file, String attachType, String contentType) {
        ConnectycubeAttachment attachment = new ConnectycubeAttachment(attachType);
        attachment.setId(file.getUid());
        attachment.setName(file.getName());
        attachment.setContentType(contentType);
        attachment.setSize(file.getSize());
        return attachment;
    }

    private ConnectycubeAttachment getAttachmentImage(ConnectycubeFile file, String localPath) {
        ConnectycubeAttachment attachment = getAttachment(file, ConnectycubeAttachment.IMAGE_TYPE, MimeTypeAttach.IMAGE_MIME);

        if (!TextUtils.isEmpty(localPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(localPath, options);
            attachment.setWidth(options.outWidth);
            attachment.setHeight(options.outHeight);
        }

        return attachment;
    }

    private ConnectycubeAttachment getAttachmentLocation(String location) {
        ConnectycubeAttachment attachment = new ConnectycubeAttachment(Attachment.Type.LOCATION.toString().toLowerCase());
        attachment.setData(location);
        attachment.setId(String.valueOf(location.hashCode()));

        return attachment;
    }

    private ConnectycubeAttachment getAttachmentAudio(ConnectycubeFile file, String localPath) {
        ConnectycubeAttachment attachment = getAttachment(file, ConnectycubeAttachment.AUDIO_TYPE, MimeTypeAttach.AUDIO_MIME);

        if (!TextUtils.isEmpty(localPath)) {
            int durationSec = MediaUtils.getMetaData(localPath).durationSec();
            attachment.setDuration(durationSec);
        }
        return attachment;
    }

    private ConnectycubeAttachment getAttachmentVideo(ConnectycubeFile file, String localPath) {
        ConnectycubeAttachment attachment = getAttachment(file, ConnectycubeAttachment.VIDEO_TYPE, MimeTypeAttach.VIDEO_MIME);

        if (!TextUtils.isEmpty(localPath)) {
            MediaUtils.MetaData metaData = MediaUtils.getMetaData(localPath);
            attachment.setHeight(metaData.videoHeight());
            attachment.setWidth(metaData.videoWidth());
            attachment.setDuration(metaData.durationSec());
        }
        return attachment;
    }

    public void sendTypingStatusToServer(boolean startTyping) {
        if (currentDialog != null && chatService.isLoggedIn()) {
            try {
                if (startTyping) {
                    currentDialog.sendIsTypingNotification();
                } else {
                    currentDialog.sendStopTypingNotification();
                }
            } catch (XMPPException | SmackException.NotConnectedException /*| ResponseException*/ e) {
                ErrorUtils.logError(e);
            }
        }
    }

    public ConnectycubeChatDialog createPrivateChatOnRest(int opponentId) throws ResponseException {
        ConnectycubeChatDialog dialog = ConnectycubeRestChatService.createChatDialog(DialogUtils.buildPrivateDialog(opponentId)).perform();
        return dialog;
    }

    public ConnectycubeChatDialog createPrivateDialogIfNotExist(int userId) throws ResponseException {
        ConnectycubeChatDialog existingPrivateDialog = ChatUtils.getExistPrivateDialog(dataManager, userId);
        if (existingPrivateDialog == null) {
            existingPrivateDialog = createPrivateChatOnRest(userId);
            DbUtils.saveDialogToCache(dataManager, existingPrivateDialog, false);
        }
        return existingPrivateDialog;
    }

    protected void checkForSendingNotification(boolean ownMessage, ConnectycubeChatMessage qbChatMessage, QMUser user,
                                               boolean isPrivateChat) {
        String dialogId = qbChatMessage.getDialogId();
        if (qbChatMessage.getId() == null || dialogId == null) {
            return;
        }

        if (!ownMessage) {
            sendNotificationBroadcast(QBServiceConsts.GOT_CHAT_MESSAGE, qbChatMessage, user, dialogId, isPrivateChat);
        }

        if (currentDialog != null) {
            if (!ownMessage && !currentDialog.getDialogId().equals(dialogId)) {
                sendNotificationBroadcast(QBServiceConsts.GOT_CHAT_MESSAGE_LOCAL, qbChatMessage, user, dialogId, isPrivateChat);
            }
        } else if (!ownMessage) {
            sendNotificationBroadcast(QBServiceConsts.GOT_CHAT_MESSAGE_LOCAL, qbChatMessage, user, dialogId,
                    isPrivateChat);
        }
    }

    private void sendNotificationBroadcast(String action, ConnectycubeChatMessage chatMessage, QMUser user, String dialogId,
                                           boolean isPrivateMessage) {
        Intent intent = new Intent(action);
        String messageBody = chatMessage.getBody();
        String extraChatMessage;

        if (!CollectionsUtil.isEmpty(chatMessage.getAttachments())) {
            //in current version we can send only one attachment
            ConnectycubeAttachment attachment = chatMessage.getAttachments().iterator().next();
            extraChatMessage = context.getResources().getString(R.string.was_attached, attachment.getType());
        } else {
            extraChatMessage = messageBody;
        }

        intent.putExtra(QBServiceConsts.EXTRA_CHAT_MESSAGE, extraChatMessage);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QBServiceConsts.EXTRA_IS_PRIVATE_MESSAGE, isPrivateMessage);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        context.sendBroadcast(intent);
    }

    protected void notifyMessageTyping(int userId, boolean isTyping) {
        Intent intent = new Intent(QBServiceConsts.TYPING_MESSAGE);
        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
        intent.putExtra(QBServiceConsts.EXTRA_IS_TYPING, isTyping);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected Message parseReceivedMessage(ConnectycubeChatMessage qbChatMessage) {
        long dateSent = ChatUtils.getMessageDateSent(qbChatMessage);
        String attachUrl = ChatUtils.getAttachUrlIfExists(qbChatMessage);
        String dialogId = qbChatMessage.getDialogId();

        Message message = new Message();
        message.setMessageId(qbChatMessage.getId());
        message.setBody(qbChatMessage.getBody());
        message.setCreatedDate(dateSent);
        message.setState(State.DELIVERED);
        if (qbChatMessage.getProperties() != null && qbChatMessage.getProperties().containsKey("replyMessage")) {
            String value = qbChatMessage.getProperties().get("replyMessage");
            if (value != null) {
                message.setPROPERTY_REPLY_MESSAGE(value);
            }
        }

        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(dialogId, qbChatMessage.getSenderId());
        if (dialogOccupant == null) {
            dialogOccupant = new DialogOccupant();
            ConnectycubeChatDialog chatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
            if (chatDialog != null) {
                dialogOccupant.setDialog(DialogTransformUtils.createLocalDialog(chatDialog));
            }
            QMUser user = QMUserService.getInstance().getUserCache().get((long) qbChatMessage.getSenderId());
            if (user != null) {
                dialogOccupant.setUser(user);
            }
        }

        message.setDialogOccupant(dialogOccupant);

        if (qbChatMessage.getAttachments() != null && !qbChatMessage.getAttachments().isEmpty()) {
            Attachment attachment = new Attachment();
//            if (getAttachmentType(qbChatMessage.getAttachments()).equalsIgnoreCase(Attachment.Type.LOCATION.toString())) {
//                attachment.setType(Attachment.Type.LOCATION);
//            } else {
//                attachment.setType(Attachment.Type.IMAGE);
//            }
            if (getAttachmentType(qbChatMessage.getAttachments()).equalsIgnoreCase(ConnectycubeAttachment.IMAGE_TYPE)) {
                attachment.setType(Attachment.Type.IMAGE);
            } else {
                attachment.setType(Attachment.Type.valueOf(getAttachmentType(qbChatMessage.getAttachments()).toUpperCase()));
            }
            attachment.setRemoteUrl(attachUrl);
            message.setAttachment(attachment);
        }

        return message;
    }

    private String getAttachmentType(Collection<ConnectycubeAttachment> attachments) {
        ConnectycubeAttachment attachment = attachments.iterator().next();
        return attachment.getType();
    }

    //Update message status

    public void updateStatusNotificationMessageRead(ConnectycubeChatDialog chatDialog, CombinationMessage combinationMessage) throws Exception {
        updateStatusMessageReadServer(chatDialog, combinationMessage, true);
        DbUtils.updateStatusNotificationMessageLocal(dataManager, combinationMessage.toDialogNotification());
    }

    public void updateStatusMessageRead(ConnectycubeChatDialog chatDialog, CombinationMessage combinationMessage,
                                        boolean isOnline) throws Exception {
        updateStatusMessageReadServer(chatDialog, combinationMessage, isOnline);
        DbUtils.updateStatusMessageLocal(dataManager, combinationMessage.toMessage());
    }

    public void updateStatusMessageReadServer(ConnectycubeChatDialog chatDialog, CombinationMessage combinationMessage,
                                              boolean isOnline) throws Exception {
        if (isOnline) {
            chatDialog.initForChat(chatService);

            ConnectycubeChatMessage qbChatMessage = new ConnectycubeChatMessage();
            qbChatMessage.setId(combinationMessage.getMessageId());
            qbChatMessage.setDialogId(chatDialog.getDialogId());
            qbChatMessage.setSenderId(combinationMessage.getDialogOccupant().getUser().getId());

            chatDialog.readMessage(qbChatMessage);
        } else {
            StringifyArrayList<String> messagesIdsList = new StringifyArrayList<String>();
            messagesIdsList.add(combinationMessage.getMessageId());
            ConnectycubeRestChatService.markMessagesAsRead(chatDialog.getDialogId(), messagesIdsList).perform();
        }
    }

    //Group dialogs

    private void createGroupDialogByNotification(ConnectycubeChatMessage qbChatMessage, DialogNotification.Type notificationType) {
        qbChatMessage.setBody(context.getString(R.string.cht_notification_message));

        ConnectycubeChatDialog qbDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, qbChatMessage.getBody(), ConnectycubeDialogType.GROUP);

        qbDialog.getOccupants().add(chatCreator.getId());
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        String roomJidId = qbDialog.getRoomJid();
        if (roomJidId != null) {
            tryJoinRoomChat(qbDialog);
            new FinderUnknownUsers(context, chatCreator, qbDialog).find();
        }

        DialogNotification dialogNotification = ChatUtils.convertMessageToDialogNotification(parseReceivedMessage(qbChatMessage));
        dialogNotification.setType(notificationType);
        Message message = ChatUtils.createTempLocalMessage(dialogNotification);
        DbUtils.saveTempMessage(dataManager, message);

        boolean ownMessage = !message.isIncoming(chatCreator.getId());
        QMUser user = QMUserService.getInstance().getUserCache().get((long) qbChatMessage.getSenderId());
        checkForSendingNotification(ownMessage, qbChatMessage, user, false);
    }

    public synchronized void tryJoinRoomChatsPage(List<ConnectycubeChatDialog> qbDialogsList, boolean needClean) {
        if (!qbDialogsList.isEmpty()) {
            initGroupDialogsList(needClean);
            for (ConnectycubeChatDialog dialog : qbDialogsList) {
                if (!ConnectycubeDialogType.PRIVATE.equals(dialog.getType())) {
                    groupDialogsList.add(dialog);
                    tryJoinRoomChat(dialog, null);
                }
            }
        }
    }

    public void tryJoinRoomChats() {
        List<ConnectycubeChatDialog> qbDialogsList = dataManager.getConnectycubeChatDialogDataManager().getAll();
        if (CollectionsUtil.isEmpty(qbDialogsList)) {
            return;
        }
        for (ConnectycubeChatDialog dialog : qbDialogsList) {
            if (ConnectycubeDialogType.PRIVATE != dialog.getType()) {
                tryJoinRoomChat(dialog, null);
            }
        }
    }

    private void initGroupDialogsList(boolean needClean) {
        if (groupDialogsList == null) {
            groupDialogsList = new ArrayList<>();
        } else {
            if (needClean) {
                groupDialogsList.clear();
            }
        }
    }

    public void tryJoinRoomChat(ConnectycubeChatDialog dialog) {
        try {
            joinRoomChat(dialog);
        } catch (Exception e) {
            ErrorUtils.logError(e);
        }
    }

    public void tryJoinRoomChat(ConnectycubeChatDialog dialog, EntityCallback<Void> callback) {
        joinRoomChat(dialog, callback);
    }

    public void joinRoomChat(ConnectycubeChatDialog dialog, EntityCallback<Void> callback) {
        dialog.initForChat(chatService);
        if (!dialog.isJoined()) { //join only to unjoined dialogs
            dialog.join(history(), callback); //join asynchronously, this doesn't block current thread to enqueue join for next dialog
        }
    }

    public void joinRoomChat(ConnectycubeChatDialog dialog) throws XMPPException, SmackException {
        dialog.initForChat(chatService);
        if (!dialog.isJoined()) { //join only to unjoined dialogs
            dialog.join(history());
        }
    }

    private DiscussionHistory history() {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0); // without getting messages
        return history;
    }

    public void leaveDialogs() throws XMPPException, SmackException.NotConnectedException {
        if (groupDialogsList != null) {
            for (ConnectycubeChatDialog dialog : groupDialogsList) {
                if (dialog.isJoined()) {
                    dialog.leave();
                }
            }
        }
    }

    public void leaveRoomChat(ConnectycubeChatDialog chatDialog) throws Exception {
        chatDialog.initForChat(chatService);
        chatDialog.leave();
        List<Integer> userIdsList = new ArrayList<Integer>();
        userIdsList.add(chatCreator.getId());
        removeUsersFromDialog(chatDialog, userIdsList);
    }

    public boolean isDialogJoined(ConnectycubeChatDialog dialog) {
        return dialog.isJoined();
    }

    public void sendGroupMessageToFriends(ConnectycubeChatDialog qbDialog, DialogNotification.Type notificationType,
                                          Collection<Integer> occupantsIdsList, boolean leavedFromDialog) throws ResponseException {
        ConnectycubeChatMessage chatMessage = ChatNotificationUtils.createGroupMessageAboutUpdateChat(context, qbDialog,
                notificationType, occupantsIdsList, leavedFromDialog);
        sendChatMessage(chatMessage, qbDialog);
    }

    public ConnectycubeChatDialog addUsersToDialog(String dialogId, List<Integer> userIdsList) throws Exception {
        StringifyArrayList<Integer> occupantsIdsList = new StringifyArrayList<>(userIdsList);
        ConnectycubeChatDialog chatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);

        RequestUpdateBuilder requestBuilder = new RequestUpdateBuilder();
        requestBuilder.push(com.connectycube.chat.Consts.DIALOG_OCCUPANTS, occupantsIdsList.getItemsAsString());
        return updateDialog(chatDialog, requestBuilder);
    }

    public void removeUsersFromDialog(ConnectycubeChatDialog chatDialog, List<Integer> userIdsList) throws ResponseException {
        RequestUpdateBuilder requestBuilder = new RequestUpdateBuilder();
        requestBuilder.pullAll(com.connectycube.chat.Consts.DIALOG_OCCUPANTS, userIdsList.toArray());
        updateDialog(chatDialog, requestBuilder);
        DataManager.getInstance().getConnectycubeChatDialogDataManager().deleteById(chatDialog.getDialogId());
    }

    public ConnectycubeChatDialog updateDialog(ConnectycubeChatDialog dialog) throws ResponseException {
        return updateDialog(dialog, (RequestUpdateBuilder) null);
    }

    public ConnectycubeChatDialog updateDialog(ConnectycubeChatDialog dialog, File inputFile) throws ResponseException {
        ConnectycubeFile file = ConnectycubeStorage.uploadFileTask(inputFile, true, (String) null).perform();
        dialog.setPhoto(file.getPublicUrl());
        return updateDialog(dialog, (RequestUpdateBuilder) null);
    }

    private ConnectycubeChatDialog updateDialog(ConnectycubeChatDialog chatDialog, RequestUpdateBuilder requestBuilder) throws ResponseException {
        ConnectycubeChatDialog updatedDialog = ConnectycubeRestChatService.updateChatDialog(chatDialog, requestBuilder).perform();
        return updatedDialog;
    }

    public ConnectycubeChatDialog createGroupChat(String name, List<Integer> friendIdsList, String photoUrl) throws Exception {
        ArrayList<Integer> occupantIdsList = (ArrayList<Integer>) ChatUtils.getOccupantIdsWithUser(friendIdsList);

        ConnectycubeChatDialog dialogToCreate = new ConnectycubeChatDialog();
        dialogToCreate.setName(name);
        dialogToCreate.setType(ConnectycubeDialogType.GROUP);
        dialogToCreate.setOccupantsIds(occupantIdsList);
        dialogToCreate.setPhoto(photoUrl);

        ConnectycubeChatDialog qbDialog = ConnectycubeRestChatService.createChatDialog(dialogToCreate).perform();
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        joinRoomChat(qbDialog);

        sendSystemMessageAboutCreatingGroupChat(qbDialog, friendIdsList);

        ConnectycubeChatMessage chatMessage = ChatNotificationUtils.createGroupMessageAboutCreateGroupChat(context, qbDialog, photoUrl);
        sendChatMessage(chatMessage, qbDialog);

        return qbDialog;
    }

    //System messages
    //
    public void sendSystemMessage(ConnectycubeChatMessage chatMessage, int opponentId, String dialogId) {
        addNecessaryPropertyForConnectycubeChatMessage(chatMessage, dialogId);
        chatMessage.setRecipientId(opponentId);
        try {
            systemMessagesManager.sendSystemMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
            ErrorUtils.logError(e);
        }
    }

    public void sendSystemMessageAboutCreatingGroupChat(ConnectycubeChatDialog dialog, List<Integer> friendIdsList) throws Exception {
        for (Integer friendId : friendIdsList) {
            try {
                sendSystemMessageAboutCreatingGroupChat(dialog, friendId);
            } catch (ResponseException e) {
                ErrorUtils.logError(e);
            }
        }
    }

    private void sendSystemMessageAboutCreatingGroupChat(ConnectycubeChatDialog dialog, Integer friendId) throws Exception {
        ConnectycubeChatMessage chatMessageForSending = ChatNotificationUtils
                .createSystemMessageAboutCreatingGroupChat(context, dialog);

        addNecessaryPropertyForConnectycubeChatMessage(chatMessageForSending, dialog.getDialogId());
        sendSystemMessage(chatMessageForSending, friendId, dialog.getDialogId());
    }

    public ConnectycubeFile loadAttachFile(File inputFile) throws Exception {
        ConnectycubeFile file;

        try {
            file = ConnectycubeStorage.uploadFileTask(inputFile, true, null).perform();
        } catch (ResponseException exc) {
            throw new Exception(context.getString(R.string.dlg_fail_upload_attach));
        }

        return file;
    }

    private void updateGroupDialogByNotification(ConnectycubeChatMessage qbChatMessage) {
        String dialogId = qbChatMessage.getDialogId();
        ConnectycubeChatDialog qbDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        if (qbDialog == null) {
            qbDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, ConnectycubeDialogType.GROUP);
        }

        ChatNotificationUtils.updateDialogFromQBMessage(context, dataManager, qbChatMessage, qbDialog);
        DbUtils.saveDialogToCache(dataManager, qbDialog);

        notifyUpdatingDialog();
    }

    protected void notifyUpdatingDialog() {
        Intent intent = new Intent(QBServiceConsts.UPDATE_DIALOG);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void onChatMessageReceived(String dialogId, ConnectycubeChatMessage chatMessage, Integer senderId) {
        boolean ownMessage = chatMessage.getSenderId().equals(chatCreator.getId());

        if (ChatNotificationUtils.isNotificationMessage(chatMessage)) {
            if (isNotificationToGroupChat(chatMessage)) {
                if (!ownMessage) {
                    updateGroupDialogByNotification(chatMessage);
                } else if(isNotificationDeletedGroupChat(chatMessage)) {
//                    not notify and return if currentUser deleted groupDialog
                    return;
                }
            } else {
                for (QBNotificationChatListener notificationChatListener : notificationChatListeners) {
                    notificationChatListener.onReceivedNotification((String) chatMessage.getProperty(
                            ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE), chatMessage);
                }
            }
        }

        QMUser user = QMUserService.getInstance().getUserCache().get((long) senderId);
        ConnectycubeChatDialog chatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);

        //Can receive message from unknown dialog only for ConnectycubeDialogType.PRIVATE
        if (chatDialog == null) {
            chatDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, chatMessage, ConnectycubeDialogType.PRIVATE);
            ChatUtils.addOccupantsToQBDialog(chatDialog, chatMessage);
            DbUtils.saveDialogToCache(dataManager, chatDialog, false);
        }

        boolean isPrivateChatMessage = ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType());
        boolean needNotifyObserver = true;

        DbUtils.updateDialogModifiedDate(dataManager, chatDialog, ChatUtils.getMessageDateSent(chatMessage), false);
        DbUtils.saveMessageOrNotificationToCache(context, dataManager, dialogId, chatMessage,
                !ownMessage
                        ? State.DELIVERED
                        : State.SYNC,
                needNotifyObserver);

        checkForSendingNotification(ownMessage, chatMessage, user, isPrivateChatMessage);
    }

    private boolean isNotificationToGroupChat(ConnectycubeChatMessage chatMessage) {
        String updatedInfo = (String) chatMessage.getProperty(ChatNotificationUtils.PROPERTY_ROOM_UPDATE_INFO);
        return updatedInfo != null;
    }

    private boolean isNotificationDeletedGroupChat(ConnectycubeChatMessage chatMessage) {
        String deletedOccupantsIdsString = (String) chatMessage.getProperty(ChatNotificationUtils.PROPERTY_ROOM_DELETED_OCCUPANTS_IDS);
        return deletedOccupantsIdsString != null;
    }

    protected void notifyUpdatingDialogDetails(int userId, boolean online) {
        Intent intent = new Intent(QBServiceConsts.UPDATE_DIALOG_DETAILS);
        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
        intent.putExtra(QBServiceConsts.EXTRA_STATUS, online);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void friendRequestMessageReceived(ConnectycubeChatMessage qbChatMessage, DialogNotification.Type notificationType) {
        String dialogId = qbChatMessage.getDialogId();
        Message message = parseReceivedMessage(qbChatMessage);

        if (!QMUserService.getInstance().getUserCache().exists((long) qbChatMessage.getSenderId())) {
            QBRestHelper.loadAndSaveUser(qbChatMessage.getSenderId());
        }

        DialogNotification dialogNotification = ChatUtils.convertMessageToDialogNotification(message);
        dialogNotification.setType(notificationType);

        ConnectycubeChatDialog chatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        if (chatDialog == null) {
            ConnectycubeChatDialog newChatDialog = ChatNotificationUtils.parseDialogFromQBMessage(context, qbChatMessage, ConnectycubeDialogType.PRIVATE);
            ArrayList<Integer> occupantsIdsList = ChatUtils.createOccupantsIdsFromPrivateMessage(chatCreator.getId(), qbChatMessage.getSenderId());
            newChatDialog.setOccupantsIds(occupantsIdsList);
            DbUtils.saveDialogToCache(dataManager, newChatDialog, false);
        }

        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupant(dialogId, qbChatMessage.getSenderId());
        DbUtils.saveDialogNotificationToCache(context, dataManager, dialogOccupant, qbChatMessage, false);

        if (dialogOccupant != null) {
            checkForSendingNotification(false, qbChatMessage, dialogOccupant.getUser(), true);
        }
    }

    interface QBNotificationChatListener {

        void onReceivedNotification(String notificationType, ConnectycubeChatMessage chatMessage);
    }

    private class ChatConnectionListener extends AbstractConnectionListener{
        @Override
        public void reconnectionSuccessful() {
            tryJoinRoomChats();
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            chatCreator = AppSession.getSession().getUser();
            initMainChatListeners();
        }
    }

    private class AllChatMessagesListener implements ChatDialogMessageListener {

        @Override
        public void processMessage(final String dialogId, final ConnectycubeChatMessage qbChatMessage, final Integer senderId) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onChatMessageReceived(dialogId, qbChatMessage, senderId);
                }
            });
        }

        @Override
        public void processError(String dialogId, ChatException exception, ConnectycubeChatMessage qbChatMessage, Integer integer) {
            //TODO VT need implement for process error message
        }
    }

    private class PrivateChatMessagesStatusListener implements MessageStatusListener {

        @Override
        public void processMessageDelivered(String messageId, String dialogId, Integer userId) {
            DbUtils.updateStatusMessageLocal(dataManager, messageId, State.DELIVERED);
        }

        @Override
        public void processMessageRead(String messageId, String dialogId, Integer userId) {
            DbUtils.updateStatusMessageLocal(dataManager, messageId, State.READ);
        }
    }

    private class TypingListener implements ChatDialogTypingListener {

        @Override
        public void processUserIsTyping(String dialogId, Integer userId) {
            notifyMessageTyping(userId, true);
        }

        @Override
        public void processUserStopTyping(String dialogId, Integer userId) {
            notifyMessageTyping(userId, false);
        }
    }

    private class QMSystemMessageListener implements SystemMessageListener {

        @Override
        public void processMessage(final ConnectycubeChatMessage qbChatMessage) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String notificationTypeString = (String) qbChatMessage
                            .getProperty(ChatNotificationUtils.PROPERTY_NOTIFICATION_TYPE);
                    NotificationType notificationType = NotificationType.parseByValue(
                            Integer.parseInt(notificationTypeString));
                    if (NotificationType.GROUP_CHAT_CREATE.equals(notificationType)) {
                        createGroupDialogByNotification(qbChatMessage, DialogNotification.Type.CREATE_DIALOG);
                    }
                }
            });

        }

        @Override
        public void processError(ChatException e, ConnectycubeChatMessage qbChatMessage) {
            ErrorUtils.logError(e);
        }
    }

    private class PrivateChatNotificationListener implements QBNotificationChatListener {

        @Override
        public void onReceivedNotification(String notificationTypeString, ConnectycubeChatMessage chatMessage) {
            NotificationType notificationType = NotificationType.parseByValue(
                    Integer.parseInt(notificationTypeString));
            switch (notificationType) {
                case FRIENDS_REQUEST:
                    friendRequestMessageReceived(chatMessage, DialogNotification.Type.FRIENDS_REQUEST);
                    break;
                case FRIENDS_ACCEPT:
                    friendRequestMessageReceived(chatMessage, DialogNotification.Type.FRIENDS_ACCEPT);
                    break;
                case FRIENDS_REJECT:
                    friendRequestMessageReceived(chatMessage, DialogNotification.Type.FRIENDS_REJECT);
                    break;
                case FRIENDS_REMOVE:
                    friendRequestMessageReceived(chatMessage, DialogNotification.Type.FRIENDS_REMOVE);
                    clearFriendOrUserRequestLocal(chatMessage.getSenderId());
                    break;
            }
        }

        private void clearFriendOrUserRequestLocal(int userId) {
            boolean friend = dataManager.getFriendDataManager().existsByUserId(userId);
            boolean outgoingUserRequest = dataManager.getUserRequestDataManager().existsByUserId(userId);
            if (friend) {
                dataManager.getFriendDataManager().deleteByUserId(userId);
            } else if (outgoingUserRequest) {
                dataManager.getUserRequestDataManager().deleteByUserId(userId);
            }
        }
    }

    private class ParticipantListener implements ChatDialogParticipantListener {

        @Override
        public void processPresence(String dialogId, ConnectycubePresence presence) {
            boolean validData = currentDialog != null && presence.getUserId() != null && currentDialog.getRoomJid() != null;
            if (validData && currentDialog.getRoomJid().equals(JIDHelper.INSTANCE.getRoomJidByDialogId(dialogId))) {
                notifyUpdatingDialogDetails(presence.getUserId(), ConnectycubePresence.Type.online.equals(presence.getType()));
            }
        }
    }
}