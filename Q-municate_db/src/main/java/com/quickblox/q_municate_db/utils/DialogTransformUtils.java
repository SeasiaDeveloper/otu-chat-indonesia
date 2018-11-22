package com.quickblox.q_municate_db.utils;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.core.helper.CollectionsUtil;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogOccupant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DialogTransformUtils {

    public static ConnectycubeChatDialog createQBDialogFromLocalDialog(DataManager dataManager, Dialog dialog) {
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(dialog.getDialogId());
        ConnectycubeChatDialog qbDialog = createQBDialogFromLocalDialog(dialog, dialogOccupantsList);
        return qbDialog;
    }

    private static ConnectycubeChatDialog createQBDialogFromLocalDialog(Dialog dialog, List<DialogOccupant> dialogOccupantsList) {
        ConnectycubeChatDialog qbDialog = new ConnectycubeChatDialog();
        qbDialog.setDialogId(dialog.getDialogId());
        qbDialog.setRoomJid(dialog.getRoomJid());
        qbDialog.setPhoto(dialog.getPhoto());
        qbDialog.setName(dialog.getTitle());
        qbDialog.setOccupantsIds(createOccupantsIdsFromDialogOccupantsList(dialogOccupantsList));
        qbDialog.setType(
                Dialog.Type.PRIVATE.equals(dialog.getType()) ? ConnectycubeDialogType.PRIVATE : Dialog.Type.GROUP.equals(dialog.getType()) ? ConnectycubeDialogType.GROUP : ConnectycubeDialogType.PUBLIC_GROUP);

        qbDialog.setUpdatedAt(new Date(dialog.getModifiedDateLocal()));
        return qbDialog;
    }

    public static ArrayList<Integer> createOccupantsIdsFromDialogOccupantsList(
            List<DialogOccupant> dialogOccupantsList) {
        ArrayList<Integer> occupantsIdsList;
        if (dialogOccupantsList != null)
        {
            occupantsIdsList = new ArrayList<>(dialogOccupantsList.size());
            for (DialogOccupant dialogOccupant : dialogOccupantsList)
            {
                occupantsIdsList.add(dialogOccupant.getUser().getId());
            }
        }
        else
        {
            occupantsIdsList = new ArrayList<>();
        }
        return occupantsIdsList;
    }

    public static Dialog createLocalDialog(ConnectycubeChatDialog qbDialog) {
        Dialog dialog = new Dialog();
        dialog.setDialogId(qbDialog.getDialogId());
        dialog.setRoomJid(qbDialog.getRoomJid());
        dialog.setTitle(qbDialog.getName());
        dialog.setPhoto(qbDialog.getPhoto());
        if (qbDialog.getUpdatedAt() != null) {
            dialog.setUpdatedAt(qbDialog.getUpdatedAt().getTime()/1000);
        }
        dialog.setModifiedDateLocal(qbDialog.getLastMessageDateSent());

        if (ConnectycubeDialogType.PRIVATE.equals(qbDialog.getType())) {
            dialog.setType(Dialog.Type.PRIVATE);
        } else if (ConnectycubeDialogType.GROUP.equals(qbDialog.getType())) {
            dialog.setType(Dialog.Type.GROUP);
        } else if (ConnectycubeDialogType.PUBLIC_GROUP.equals(qbDialog.getType())) {
            dialog.setType(Dialog.Type.BROADCAST);
        }
        return dialog;
    }

    public static List<Dialog> getListLocalDialogsFromQBDialogs(Collection<ConnectycubeChatDialog> chatDialogs){
        List<Dialog> dialogsList = new ArrayList<>(chatDialogs.size());
        for (ConnectycubeChatDialog chatDialog : chatDialogs){
            dialogsList.add(DialogTransformUtils.createLocalDialog(chatDialog));
        }

        return dialogsList;
    }

    public static List<ConnectycubeChatDialog> getListQBDialogsFromLocalDialogs(Collection<Dialog> dialogsList) {
        List<ConnectycubeChatDialog> chatDialogList = new ArrayList<>();

        if (!CollectionsUtil.isEmpty(dialogsList)) {
            for (Dialog dialog : dialogsList) {
                chatDialogList.add(DialogTransformUtils.createQBDialogFromLocalDialog(DataManager.getInstance(), dialog));
            }
        }

        return chatDialogList;
    }
}
