package com.quickblox.q_municate_core.utils;

import com.connectycube.chat.model.ConnectycubeRosterEntry;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_db.models.UserRequest;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFriendUtils {

    public static List<QMUser> getUsersFromFriends(List<Friend> friendList) {
        List<QMUser> userList = new ArrayList<>(friendList.size());
        for (Friend friend : friendList) {
            userList.add(friend.getUser());
        }
        return userList;
    }

    public static List<QMUser> getUsersFromUserRequest(List<UserRequest> userRequestList) {
        List<QMUser> userList = new ArrayList<>(userRequestList.size());
        for (UserRequest userRequest : userRequestList) {
            if (userRequest.getRequestStatus() == UserRequest.RequestStatus.OUTGOING) {
                userList.add(userRequest.getUser());
            }
        }
        return userList;
    }

    public static QMUser createLocalUser(ConnectycubeUser connectycubeUser) {
        QMUser user = new QMUser();
        user.setId(connectycubeUser.getId());
        user.setFullName(connectycubeUser.getFullName());
        user.setEmail(connectycubeUser.getEmail());
        user.setPhone(connectycubeUser.getPhone());
        user.setLogin(connectycubeUser.getLogin());

        if (connectycubeUser.getLastRequestAt() != null) {
            user.setLastRequestAt(connectycubeUser.getLastRequestAt());
        }


        UserCustomData userCustomData = Utils.customDataToObject(connectycubeUser.getCustomData());

        if (userCustomData != null) {
            user.setAvatar(userCustomData.getAvatarUrl());
            user.setStatus(userCustomData.getStatus());
        }

        return user;
    }


    public static ConnectycubeUser createConnectycubeUser(QMUser user) {
        ConnectycubeUser connectycubeUser = new ConnectycubeUser();
        connectycubeUser.setId(user.getId());
        connectycubeUser.setLogin(user.getLogin());
        connectycubeUser.setFullName(user.getFullName());
        return connectycubeUser;
    }

    public static boolean isOutgoingFriend(ConnectycubeRosterEntry rosterEntry) {
        return RosterPacket.ItemStatus.subscribe.equals(rosterEntry.getStatus());
    }

    public static boolean isNoneFriend(ConnectycubeRosterEntry rosterEntry) {
        return RosterPacket.ItemType.none.equals(rosterEntry.getType())
                || RosterPacket.ItemType.from.equals(rosterEntry.getType());
    }

    public static boolean isEmptyFriendsStatus(ConnectycubeRosterEntry rosterEntry) {
        return rosterEntry.getStatus() == null;
    }

    public static List<QMUser> createUsersList(Collection<ConnectycubeUser> usersList) {
        List<QMUser> users = new ArrayList<QMUser>();
        for (ConnectycubeUser user : usersList) {
            users.add(createLocalUser(user));
        }
        return users;
    }

    public static List<QMUser> createUsers(Collection<QMUser> usersList) {
        List<QMUser> users = new ArrayList<QMUser>();
        for (ConnectycubeUser user : usersList) {
            users.add(createLocalUser(user));
        }
        return users;
    }

    public static Map<Integer, QMUser> createUserMap(List<ConnectycubeUser> userList) {
        Map<Integer, QMUser> userHashMap = new HashMap<Integer, QMUser>();
        for (ConnectycubeUser user : userList) {
            userHashMap.put(user.getId(), createLocalUser(user));
        }
        return userHashMap;
    }

    public static ArrayList<Integer> getFriendIdsList(List<ConnectycubeUser> friendList) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        for (ConnectycubeUser friend : friendList) {
            friendIdsList.add(friend.getId());
        }
        return friendIdsList;
    }

    public static ArrayList<Integer> getFriendIds(List<QMUser> friendList) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        for (QMUser friend : friendList) {
            friendIdsList.add(friend.getId());
        }
        return friendIdsList;
    }

    public static ArrayList<Integer> getFriendIdsFromUsersList(List<QMUser> friendList) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        for (QMUser friend : friendList) {
            friendIdsList.add(friend.getId());
        }
        return friendIdsList;
    }

    public static List<Integer> getFriendIdsListFromList(List<Friend> friendsList) {
        List<Integer> friendIdsList = new ArrayList<Integer>();
        for (Friend friend : friendsList) {
            friendIdsList.add(friend.getUser().getId());
        }
        return friendIdsList;
    }

    public static List<Friend> getFriendsListFromDialogOccupantsList(List<DialogOccupant> dialogOccupantsList) {
        List<Friend> friendsList = new ArrayList<>(dialogOccupantsList.size());
        for (DialogOccupant dialogOccupant : dialogOccupantsList) {
            friendsList.add(new Friend(dialogOccupant.getUser()));
        }
        return friendsList;
    }

    public static String getUserNameByID(Integer userId, List<ConnectycubeUser> usersList) {
        for (ConnectycubeUser user : usersList) {
            if (user.getId().equals(userId)) {
                return user.getFullName();
            }
        }
        return userId.toString();
    }

    public static List<ConnectycubeUser> getUsersByIDs(Integer[] ids, List<ConnectycubeUser> usersList) {
        ArrayList<ConnectycubeUser> result = new ArrayList<>();
        for (Integer userId : ids) {
            for (ConnectycubeUser user : usersList) {
                if (userId.equals(user.getId())){
                    result.add(user);
                }
            }
        }
        return result;
    }

    public static QMUser createDeletedUser(int userId) {
        QMUser user = new QMUser();
        user.setId(userId);
        user.setFullName(String.valueOf(userId));
        return user;
    }
}