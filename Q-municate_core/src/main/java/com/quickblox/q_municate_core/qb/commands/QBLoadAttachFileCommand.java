package com.quickblox.q_municate_core.qb.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.connectycube.storage.model.ConnectycubeFile;
import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.models.Attachment;

import java.io.File;

public class QBLoadAttachFileCommand extends ServiceCommand {

    private static final String TAG = QBLoadAttachFileCommand.class.getSimpleName();

    private final QBChatHelper chatHelper;

    public QBLoadAttachFileCommand(Context context, QBChatHelper chatHelper,
                                   String successAction, String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, File file, Attachment.Type type, String dialogId) {
        Intent intent = new Intent(QBServiceConsts.LOAD_ATTACH_FILE_ACTION, null, context, QBService.class);
        intent.putExtra(QBServiceConsts.EXTRA_FILE, file);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(QBServiceConsts.EXTRA_ATTACHMENT_TYPE, type);
        context.startService(intent);
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        File file = (File) extras.getSerializable(QBServiceConsts.EXTRA_FILE);
        String dialogId = (String) extras.getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);
        Attachment.Type type = (Attachment.Type) extras.getSerializable(QBServiceConsts.EXTRA_ATTACHMENT_TYPE);

        ConnectycubeFile connectycubeFile = chatHelper.loadAttachFile(file);

        Bundle result = new Bundle();
        result.putSerializable(QBServiceConsts.EXTRA_ATTACH_FILE, connectycubeFile);
        result.putString(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        result.putString(QBServiceConsts.EXTRA_FILE_PATH, file.getAbsolutePath());
        result.putSerializable(QBServiceConsts.EXTRA_ATTACHMENT_TYPE, type);

        return result;
    }
}