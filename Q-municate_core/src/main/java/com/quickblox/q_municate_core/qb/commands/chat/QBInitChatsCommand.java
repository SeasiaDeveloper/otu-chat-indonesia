package com.quickblox.q_municate_core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.connectycube.users.model.ConnectycubeUser;

public class QBInitChatsCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    public QBInitChatsCommand(Context context, QBChatHelper chatHelper, String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.INIT_CHATS_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        ConnectycubeUser user;

        if (extras == null) {
            user = AppSession.getSession().getUser();
        } else {
            user = (ConnectycubeUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        }

        chatHelper.init(user);

        return extras;
    }
}