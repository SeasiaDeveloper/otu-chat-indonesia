package com.quickblox.q_municate_core.qb.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.ConnectycubeRoster;
import com.connectycube.chat.listeners.RosterListener;
import com.connectycube.chat.listeners.SubscriptionListener;
import com.connectycube.chat.model.ConnectycubeChatMessage;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubePresence;
import com.connectycube.chat.model.ConnectycubeRosterEntry;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.connectycube.users.model.ConnectycubeUser;
import com.quickblox.q_municate_core.models.NotificationType;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatNotificationUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.DateUtilsCore;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_db.models.UserRequest;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QBFriendListHelper extends BaseThreadPoolHelper implements Serializable {

    private static final int LOADING_DELAY = 500;

    private static final String TAG = QBFriendListHelper.class.getSimpleName();
    private static final String PRESENCE_CHANGE_ERROR = "Presence change error: could not find friend in DB by id = ";
    private static final String ENTRIES_UPDATING_ERROR = "Failed to update friends list";
    private static final String ENTRIES_DELETED_ERROR = "Failed to delete friends";
    private static final String SUBSCRIPTION_ERROR = "Failed to confirm subscription";
    private static final String CREATE_ENTRY_ERROR = "Failed to create subscription";
    private static final String ROSTER_INIT_ERROR = "ROSTER isn't initialized. Please make relogin";

    private QBRestHelper restHelper;
    private ConnectycubeRoster roster;
    private QBChatHelper chatHelper;
    private DataManager dataManager;
    private Timer userLoadingTimer;
    private List<Integer> userLoadingIdsList;

    //ThreadPoolExecutor
    private static final int THREAD_POOL_SIZE = 3;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private ThreadPoolExecutor threadPoolExecutor;

    public QBFriendListHelper(Context context) {
        super(context);
    }

    public void init(QBChatHelper chatHelper) {
        this.chatHelper = chatHelper;
        initThreads();
        restHelper = new QBRestHelper(context);
        dataManager = DataManager.getInstance();
        roster = ConnectycubeChatService.getInstance().getRoster(ConnectycubeRoster.SubscriptionMode.manual,
                new QMSubscriptionListener());
        roster.addRosterListener(new QMRosterListener());
        userLoadingTimer = new Timer();
    }

    private void initThreads() {
        BlockingQueue<Runnable> threadQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public void inviteFriend(int userId) throws Exception {
        if (isNotInvited(userId)) {
            invite(userId);
        }
    }

    public void addFriend(int userId) throws Exception {
        invite(userId);
    }

    public void acceptFriend(int userId) throws Exception {
        roster.confirmSubscription(userId);

        ConnectycubeChatMessage chatMessage = ChatNotificationUtils.createPrivateMessageAboutFriendsRequests(context,
                NotificationType.FRIENDS_ACCEPT);
        sendNotificationToFriend(chatMessage, userId);
    }

    public void rejectFriend(int userId) throws Exception {
        roster.reject(userId);
        clearRosterEntry(userId);
        deleteFriendOrUserRequest(userId);

        ConnectycubeChatMessage chatMessage = ChatNotificationUtils.createPrivateMessageAboutFriendsRequests(context,
                NotificationType.FRIENDS_REJECT);
        sendNotificationToFriend(chatMessage, userId);
    }

    public void removeFriend(int userId) throws Exception {
        roster.unsubscribe(userId);
        clearRosterEntry(userId);

        ConnectycubeChatMessage qbChatMessage = ChatNotificationUtils.createPrivateMessageAboutFriendsRequests(context,
                NotificationType.FRIENDS_REMOVE);
        qbChatMessage.setRecipientId(userId);
        sendNotificationToFriend(qbChatMessage, userId);

        deleteFriendOrUserRequest(userId);
    }

    public void invite(int userId) throws Exception {
        sendInvitation(userId);

        try {
            loadAndSaveUser(userId);
        } catch (ResponseException e){
            //TODO FIXME fix suppressing exception when moving to service
        }

        ConnectycubeChatMessage chatMessage = ChatNotificationUtils.createPrivateMessageAboutFriendsRequests(context,
                NotificationType.FRIENDS_REQUEST);
        sendNotificationToFriend(chatMessage, userId);
    }

    private synchronized void sendNotificationToFriend(ConnectycubeChatMessage qbChatMessage, int userId) throws ResponseException {
        ConnectycubeChatDialog qbDialog = chatHelper.createPrivateDialogIfNotExist(userId);
        if (qbDialog != null) {
            chatHelper.sendAndSaveChatMessage(qbChatMessage, qbDialog);
        }
    }

    private void clearRosterEntry(int userId) throws Exception {
        ConnectycubeRosterEntry rosterEntry = roster.getEntry(userId);
        if (rosterEntry != null && roster.contains(userId)) {
            roster.removeEntry(rosterEntry);
        }
    }

    private boolean isInvited(int userId) {
        ConnectycubeRosterEntry rosterEntry = roster.getEntry(userId);
        if (rosterEntry == null) {
            return false;
        }
        boolean isSubscribedToUser = rosterEntry.getType() == RosterPacket.ItemType.from;
        boolean isBothSubscribed = rosterEntry.getType() == RosterPacket.ItemType.both;
        return isSubscribedToUser || isBothSubscribed;
    }

    private boolean isNotInvited(int userId) {
        return !isInvited(userId);
    }

    private void sendInvitation(int userId) throws Exception {
        if (roster.contains(userId)) {
            roster.subscribe(userId);
        } else {
            roster.createEntry(userId, null);
        }
    }

    public Collection<Integer> updateFriendList() throws ResponseException {
        Collection<Integer> friendIdsList = new ArrayList<>();
        if (roster != null) {
            friendIdsList = updateLocalFriendsIfNeed();
        } else {
            ErrorUtils.logError(TAG, ROSTER_INIT_ERROR);
        }
        updateFriends(friendIdsList);
        return friendIdsList;
    }

    private Collection<Integer> updateLocalFriendsIfNeed() throws ResponseException {
        Collection<Integer> userIdsList = new ArrayList<>();
        Collection<Integer> friendList = new ArrayList<>();
        if (!roster.getEntries().isEmpty()) {
            for (ConnectycubeRosterEntry rosterEntry : roster.getEntries()) {
                userIdsList.add(rosterEntry.getUserId());
                if (dataManager.getFriendDataManager().getByUserId(rosterEntry.getUserId()) == null) {
                    friendList.add(rosterEntry.getUserId());
                }
            }
            loadAndSaveUsers(userIdsList);
        }
        return friendList;
    }

    private void updateFriends(Collection<Integer> friendIdsList) throws ResponseException {
        if(!friendIdsList.isEmpty()) {
            PagedRequestBuilder requestBuilder = new PagedRequestBuilder();
            requestBuilder.setPage(ConstsCore.USERS_PAGE_NUM);
            requestBuilder.setPerPage(ConstsCore.USERS_PER_PAGE);
            List<QMUser> qmUsers = QMUserService.getInstance().getUsersByIDsSync(friendIdsList, requestBuilder);
            saveFriends(qmUsers);
        }
    }

    private void removeFriendLocal(Integer userId) {
        dataManager.getFriendDataManager().deleteByUserId(userId);
    }


    private void updateUsersAndFriends(Collection<Integer> idsList) throws ResponseException {
        for (Integer userId : idsList) {
            updateFriend(userId);
        }
    }

    private void updateFriend(int userId) throws ResponseException {
        QMUser newUser = loadAndSaveUser(userId);
        if (newUser == null) {
            return;
        }
        saveFriend(newUser);
    }

    private void createUserRequest(QMUser user, UserRequest.RequestStatus requestStatus) {
        long currentTime = DateUtilsCore.getCurrentTime();
        UserRequest userRequest = new UserRequest(currentTime, null, requestStatus, user);
        dataManager.getUserRequestDataManager().createOrUpdate(userRequest);
    }

    private void createUserRequest(int userId) {
        if (userLoadingIdsList == null) {
            userLoadingIdsList = new ArrayList<>();
        }
        userLoadingIdsList.add(userId);
        checkLoadingUser();
    }

    private void checkLoadingUser() {
        if (userLoadingTimer != null) {
            userLoadingTimer.cancel();
        }
        userLoadingTimer = new Timer();
        userLoadingTimer.schedule(new UserLoadingTimerTask(), LOADING_DELAY);
    }

    private void deleteUserRequestByUser(int userId) {
        dataManager.getUserRequestDataManager().deleteByUserId(userId);
    }

    private void saveFriends(Collection<QMUser> usersCollection) {
        for (QMUser user : usersCollection) {
            saveFriend(user);
        }
    }

    private void saveFriend(QMUser user) {
        dataManager.getFriendDataManager().createOrUpdate(new Friend(user));
    }

    private void deleteFriend(int userId) {
        dataManager.getFriendDataManager().deleteByUserId(userId);
    }

    private void deleteFriendOrUserRequest(int userId) {
        boolean friend = dataManager.getFriendDataManager().existsByUserId(userId);
        boolean outgoingUserRequest = dataManager.getUserRequestDataManager().existsByUserId(userId);

        if (friend) {
            deleteFriend(userId);
        } else if (outgoingUserRequest) {
            deleteUserRequestByUser(userId);
        }
    }

    private void deleteFriends(Collection<Integer> userIdsList) throws ResponseException {
        for (Integer userId : userIdsList) {
            deleteFriend(userId);
        }
    }

    @Nullable
    private QMUser loadAndSaveUser(int userId) throws ResponseException {
        return QMUserService.getInstance().getUserSync(userId, true);
    }

    private boolean isUserOnline(ConnectycubePresence presence) {
        return ConnectycubePresence.Type.online.equals(presence.getType());
    }

    public boolean isUserOnline(int userId) {
        return roster != null
                && roster.getPresence(userId) != null
                && isUserOnline(roster.getPresence(userId));
    }

    private void notifyUserStatusChanged(int userId) {
        Intent intent = new Intent(QBServiceConsts.USER_STATUS_CHANGED_ACTION);
        intent.putExtra(QBServiceConsts.EXTRA_USER_ID, userId);
        intent.putExtra(QBServiceConsts.EXTRA_USER_STATUS, isUserOnline(userId));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void loadAndSaveUsers(Collection<Integer> userList, UserRequest.RequestStatus status) throws ResponseException {
        if (!userList.isEmpty()) {
            List<QMUser> loadedUserList = QMUserService.getInstance().getUsersByIDsSync(userList, null);
            for (QMUser user : loadedUserList) {
                createUserRequest(user, status);
            }
        }
    }

    private List<QMUser> loadAndSaveUsers(Collection<Integer> userList) throws ResponseException {
        List<QMUser> loadedUserList = new ArrayList<>();
        if (!userList.isEmpty()) {
            loadedUserList = QMUserService.getInstance().getUsersByIDsSync(userList, null);
        }
        return loadedUserList;
    }

    private class UserLoadingTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                loadAndSaveUsers(userLoadingIdsList, UserRequest.RequestStatus.INCOMING);
                //                notifyContactRequest();
            } catch (ResponseException e) {
                ErrorUtils.logError(e);
            }
        }
    }

    private class QMRosterListener implements RosterListener {

        @Override
        public void entriesDeleted(final Collection<Integer> userIdsList) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        deleteFriends(userIdsList);
                    } catch (ResponseException e) {
                        Log.e(TAG, ENTRIES_DELETED_ERROR, e);
                    }
                }
            });
        }

        @Override
        public void entriesAdded(Collection<Integer> userIdsList) {
        }

        @Override
        public void entriesUpdated(final Collection<Integer> idsList) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateUsersAndFriends(idsList);
                    } catch (ResponseException e) {
                        Log.e(TAG, ENTRIES_UPDATING_ERROR, e);
                    }
                }
            });
        }

        @Override
        public void presenceChanged(ConnectycubePresence presence) {
            QMUser user =  QMUserService.getInstance().getUserCache().get((long)presence.getUserId());
            if (user == null) {
                ErrorUtils.logError(TAG, PRESENCE_CHANGE_ERROR + presence.getUserId());
            } else {
                //Save user's last online status
                if (ConnectycubePresence.Type.online.equals(presence.getType())){
                    user.setLastRequestAt(new Date(System.currentTimeMillis()));
                    QMUserService.getInstance().getUserCache().update(user);
                }

                notifyUserStatusChanged(user.getId());
            }
        }
    }

    private class QMSubscriptionListener implements SubscriptionListener {

        @Override
        public void subscriptionRequested(int userId) {
            try {
                Log.d(TAG, "subscriptionRequested");
                roster.confirmSubscription(userId);
            } catch (Exception e) {
                Log.e(TAG, SUBSCRIPTION_ERROR, e);
            }
        }
    }

    public void subscribeToAllUsers(ArrayList<ConnectycubeUser> users) {
        for (ConnectycubeUser user : users) {
            if (!isUserSubscribed(user.getId())) {
                Log.d(TAG, "roster.createEntry " + user.getId());
                try {
                    roster.createEntry(user.getId(), user.getFullName(), null);
                } catch (Exception e) {
                    Log.e(TAG, CREATE_ENTRY_ERROR, e.getCause());
                }
            }
        }
    }

    private boolean isUserSubscribed(int userId) {
        ConnectycubeRosterEntry rosterEntry = roster.getEntry(userId);
        if (rosterEntry == null) {
            return false;
        }
        boolean isSubscribedFromUser = rosterEntry.getType() == RosterPacket.ItemType.from;
        return !isSubscribedFromUser;
    }
}