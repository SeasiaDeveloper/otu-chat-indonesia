package com.quickblox.q_municate_core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.RequestGetBuilder;
import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.models.ParcelableQBDialog;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;

import java.util.ArrayList;
import java.util.List;

public class QBLoadDialogsCommand extends ServiceCommand {

    private QBChatHelper chatHelper;

    private final String FIELD_DIALOG_TYPE = "type";
    private final String OPERATOR_EQ = "eq";

    private final int FIRST_PAGE_NUMBER = 1;

    // TODO: HACK!
    // This is temporary value,
    // by default MAX count of Dialogs should be !> (DIALOGS_PARTS * ConstsCore.CHATS_DIALOGS_PER_PAGE)
    // it is 200 Dialogs
    private final static int DIALOGS_PARTS = 10; // TODO: need to fix in the second release.


    private List<ConnectycubeChatDialog> dialogsListPrivate;
    private List<ConnectycubeChatDialog> dialogsListGroup;

    public QBLoadDialogsCommand(Context context, QBChatHelper chatHelper, String successAction,
                                String failAction) {
        super(context, successAction, failAction);
        this.chatHelper = chatHelper;
    }

    public static void start(Context context, boolean updateAll) {
        Intent intent = new Intent(QBServiceConsts.LOAD_CHATS_DIALOGS_ACTION, null, context, QBService.class);
        Bundle result = new Bundle();
        result.putBoolean(ConstsCore.DIALOGS_UPDATE_ALL, updateAll);
        intent.putExtras(result);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        boolean updateAll = extras.getBoolean(ConstsCore.DIALOGS_UPDATE_ALL);
        Log.d("QBLoadDialogsCommand", "perform updateAll= " + updateAll);
        final ArrayList<ParcelableQBDialog> parcelableQBDialog = new ArrayList<>();

        final Bundle returnedBundle = new Bundle();
        final RequestGetBuilder qbRequestGetBuilder = new RequestGetBuilder();

        qbRequestGetBuilder.setLimit(ConstsCore.CHATS_DIALOGS_PER_PAGE);
        qbRequestGetBuilder.sortDesc(QBServiceConsts.EXTRA_LAST_MESSAGE_DATE_SENT);

        parcelableQBDialog.addAll(ChatUtils.qBDialogsToParcelableQBDialogs(
                loadAllDialogsByPages(returnedBundle, qbRequestGetBuilder, updateAll)));

        //now all dialogs were loaded from rest
        updateAll = true;

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(QBServiceConsts.EXTRA_CHATS_DIALOGS, parcelableQBDialog);
        bundle.putBoolean(ConstsCore.DIALOGS_UPDATE_ALL, updateAll);

        return bundle;
    }

    private int loadAllDialogsByType(ConnectycubeDialogType dialogsType,  Bundle returnedBundle, RequestGetBuilder qbRequestGetBuilder, List<ConnectycubeChatDialog> allDialogsList, int pageNumber) throws ResponseException {
        boolean needToLoadMore = false;

        qbRequestGetBuilder.setSkip(allDialogsList.size());
        qbRequestGetBuilder.eq(FIELD_DIALOG_TYPE, dialogsType.getCode());
        List<ConnectycubeChatDialog> newDialogsList = getDialogs(qbRequestGetBuilder, returnedBundle);
        if (dialogsType == ConnectycubeDialogType.GROUP) {
            dialogsListGroup = newDialogsList;
        } else{
            dialogsListPrivate = newDialogsList;
        }
        allDialogsList.addAll(newDialogsList);
        Log.d("QBLoadDialogsCommand", "needToLoadMore = " + needToLoadMore + "newDialogsList.size() = " + newDialogsList.size());

        if (dialogsType == ConnectycubeDialogType.GROUP) {
            boolean needClean = (pageNumber == 0);
            tryJoinRoomChatsPage(newDialogsList, needClean);
        }

        return newDialogsList.size();
    }

    private List<ConnectycubeChatDialog> loadAllDialogsByPages(Bundle returnedBundle, RequestGetBuilder qbRequestGetBuilder, boolean updateAll) throws ResponseException {
        List<ConnectycubeChatDialog> allDialogsList = null;
        List<ConnectycubeChatDialog> allDialogsListPrivate = new ArrayList<>();
        List<ConnectycubeChatDialog> allDialogsListGroup = new ArrayList<>();
        boolean needToLoadMorePrivate = true;
        boolean needToLoadMoreGroup = true;
        int pageNumber = 0;

        final RequestGetBuilder qbRequestGetBuilderPrivate = new RequestGetBuilder();

        qbRequestGetBuilderPrivate.setLimit(ConstsCore.CHATS_DIALOGS_PER_PAGE);
        qbRequestGetBuilderPrivate.sortDesc(QBServiceConsts.EXTRA_LAST_MESSAGE_DATE_SENT);
        qbRequestGetBuilderPrivate.addRule(FIELD_DIALOG_TYPE, OPERATOR_EQ, ConnectycubeDialogType.PRIVATE.getCode());

        final RequestGetBuilder qbRequestGetBuilderGroup = new RequestGetBuilder();

        qbRequestGetBuilderGroup.setLimit(ConstsCore.CHATS_DIALOGS_PER_PAGE);
        qbRequestGetBuilderGroup.sortDesc(QBServiceConsts.EXTRA_LAST_MESSAGE_DATE_SENT);
        qbRequestGetBuilderGroup.addRule(FIELD_DIALOG_TYPE, OPERATOR_EQ, ConnectycubeDialogType.GROUP.getCode());

        int skipRow = 0;

        do {
            int privateDialogsSize = 0;
            int groupDialogsSize = 0;
            if(needToLoadMorePrivate) {
                privateDialogsSize = loadAllDialogsByType(ConnectycubeDialogType.PRIVATE, returnedBundle, qbRequestGetBuilderPrivate, allDialogsListPrivate, pageNumber);
                needToLoadMorePrivate = privateDialogsSize == ConstsCore.CHATS_DIALOGS_PER_PAGE;
            }

            if(needToLoadMoreGroup) {
                groupDialogsSize = loadAllDialogsByType(ConnectycubeDialogType.GROUP, returnedBundle, qbRequestGetBuilderGroup, allDialogsListGroup, pageNumber);
                needToLoadMoreGroup = groupDialogsSize == ConstsCore.CHATS_DIALOGS_PER_PAGE;
            }

            chatHelper.saveDialogsToCache(dialogsListPrivate, true);
            chatHelper.saveDialogsToCache(dialogsListGroup, true);
            dialogsListPrivate = null;
            dialogsListGroup = null;

            pageNumber++;

            int perPage = privateDialogsSize + groupDialogsSize;
            Log.d("QBLoadDialogsCommand", "sendLoadPageSuccess perPage= " + perPage);
            if(!updateAll) {
                Bundle bundle = new Bundle();
                bundle.putInt(ConstsCore.DIALOGS_START_ROW, skipRow);
                bundle.putInt(ConstsCore.DIALOGS_PER_PAGE, perPage);
                sendLoadPageSuccess(bundle);
            }
            skipRow += perPage;

        } while (needToLoadMorePrivate || needToLoadMoreGroup);

        allDialogsList = new ArrayList<>(allDialogsListPrivate.size() + allDialogsListGroup.size());
        allDialogsList.addAll(allDialogsListPrivate);
        allDialogsList.addAll(allDialogsListGroup);

        return allDialogsList;
    }

    private List<ConnectycubeChatDialog> getDialogs(RequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws ResponseException {
        return chatHelper.getDialogs(qbRequestGetBuilder, returnedBundle);
    }

    private void tryJoinRoomChatsPage(final List<ConnectycubeChatDialog> dialogsList, final boolean needClean) {
        chatHelper.tryJoinRoomChatsPage(dialogsList, needClean);
    }

    private void sendLoadPageSuccess(Bundle result){
        sendResult(result, successAction);
    }

    private void sendLoadPageFail(Bundle result){
        sendResult(result, failAction);
    }
}