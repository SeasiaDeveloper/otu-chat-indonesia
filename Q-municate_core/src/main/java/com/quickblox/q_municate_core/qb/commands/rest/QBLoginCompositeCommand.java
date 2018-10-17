package com.quickblox.q_municate_core.qb.commands.rest;

import android.content.Context;
import android.content.Intent;

import com.quickblox.q_municate_core.core.command.CompositeServiceCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.connectycube.users.model.ConnectycubeUser;

public class QBLoginCompositeCommand extends CompositeServiceCommand {

    public QBLoginCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context, ConnectycubeUser user) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_USER, user);
        context.startService(intent);
    }
}