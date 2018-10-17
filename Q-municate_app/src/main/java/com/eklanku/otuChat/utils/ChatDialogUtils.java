package com.eklanku.otuChat.utils;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_db.managers.DataManager;

public class ChatDialogUtils {

    public static String getTitleForChatDialog(ConnectycubeChatDialog chatDialog, DataManager dataManager) {
        if (ConnectycubeDialogType.GROUP.equals(chatDialog.getType())) {
            return chatDialog.getName();
        } else {
            Integer currentUserId = AppSession.getSession().getUser().getId();
            return ChatUtils.getFullNameById(dataManager, getPrivateChatOpponentId(chatDialog, currentUserId));
        }
    }

    public static Integer getPrivateChatOpponentId(ConnectycubeChatDialog chatDialog, Integer currentUserId){
        for (Integer opponentID : chatDialog.getOccupants()){
            if (!opponentID.equals(currentUserId)){
                return opponentID;
            }
        }

        return 0;
    }
}
