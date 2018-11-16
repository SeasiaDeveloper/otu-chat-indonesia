package com.quickblox.q_municate_core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.connectycube.chat.model.ConnectycubeChatDialog;
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


    private List<ConnectycubeChatDialog> dialogsListFullMixed;

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

    private int loadAllDialogsMixed(Bundle returnedBundle, RequestGetBuilder qbRequestGetBuilder, List<ConnectycubeChatDialog> allDialogsList, int pageNumber) throws ResponseException {
        boolean needToLoadMore = false;

        qbRequestGetBuilder.setSkip(allDialogsList.size());
        List<ConnectycubeChatDialog> newDialogsList = getDialogs(qbRequestGetBuilder, returnedBundle);
        dialogsListFullMixed = newDialogsList;
        allDialogsList.addAll(newDialogsList);
        Log.d("QBLoadDialogsCommand", "needToLoadMore = " + needToLoadMore + "newDialogsList.size() = " + newDialogsList.size());

        boolean needClean = (pageNumber == 0);
        tryJoinRoomChatsPage(newDialogsList, needClean);

        return newDialogsList.size();
    }

    private List<ConnectycubeChatDialog> loadAllDialogsByPages(Bundle returnedBundle, RequestGetBuilder qbRequestGetBuilder, boolean updateAll) throws ResponseException {
        List<ConnectycubeChatDialog> allDialogsList = new ArrayList<>();
        boolean needToLoadMoreDialogs;
        int pageNumber = 0;

        int skipRow = 0;

        do {
            int dialogListSize = loadAllDialogsMixed(returnedBundle,
                    qbRequestGetBuilder,
                    allDialogsList,
                    pageNumber);
            needToLoadMoreDialogs = dialogListSize == ConstsCore.CHATS_DIALOGS_PER_PAGE;

            chatHelper.saveDialogsToCache(dialogsListFullMixed, true);
            dialogsListFullMixed = null;

            pageNumber++;

            int perPage = dialogListSize;
            Log.d("QBLoadDialogsCommand", "sendLoadPageSuccess perPage= " + perPage);
            if(!updateAll) {
                Bundle bundle = new Bundle();
                bundle.putInt(ConstsCore.DIALOGS_START_ROW, skipRow);
                bundle.putInt(ConstsCore.DIALOGS_PER_PAGE, perPage);
                sendLoadPageSuccess(bundle);
            }
            skipRow += perPage;

        } while (needToLoadMoreDialogs);

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