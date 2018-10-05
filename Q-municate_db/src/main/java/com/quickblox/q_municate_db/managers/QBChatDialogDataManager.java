package com.quickblox.q_municate_db.managers;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.quickblox.q_municate_db.managers.base.Manager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;

import java.util.Collection;
import java.util.List;
import java.util.Observer;

public class QBChatDialogDataManager implements Manager<ConnectycubeChatDialog> {
    private final DialogDataManager dialogDataManager;

    public QBChatDialogDataManager(DialogDataManager dialogDataManager) {
        this.dialogDataManager = dialogDataManager;
    }

    public ConnectycubeChatDialog getByDialogId(String dialogId) {
        ConnectycubeChatDialog chatDialog = null;
        Dialog dialog = dialogDataManager.getByDialogId(dialogId);

        if (dialog != null) {
            chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(DataManager.getInstance(), dialog);
        }

        return chatDialog;
    }

    public ConnectycubeChatDialog getByRoomJid(String roomJid) {
        ConnectycubeChatDialog chatDialog = null;
        Dialog dialog = dialogDataManager.getByRoomJid(roomJid);

        if (dialog != null) {
            chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(DataManager.getInstance(), dialog);
        }

        return chatDialog;
    }

    @Override
    public void create(ConnectycubeChatDialog object) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(object);
        dialogDataManager.create(dialog);
    }

    @Override
    public void createOrUpdate(ConnectycubeChatDialog chatDialog) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(chatDialog);
        dialogDataManager.createOrUpdate(dialog);
    }

    @Override
    public void createOrUpdate(ConnectycubeChatDialog object, boolean notify) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(object);
        dialogDataManager.createOrUpdate(dialog, notify);
    }

    @Override
    public ConnectycubeChatDialog get(long id) {
        return DialogTransformUtils.createQBDialogFromLocalDialog(
                DataManager.getInstance(),
                dialogDataManager.get(id));
    }

    @Override
    public void update(ConnectycubeChatDialog chatDialog) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(chatDialog);
        dialogDataManager.update(dialog);
    }

    @Override
    public void update(ConnectycubeChatDialog chatDialog, boolean notify) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(chatDialog);
        dialogDataManager.update(dialog, notify);
    }

    @Override
    public void updateAll(Collection<ConnectycubeChatDialog> objectsCollection) {
        dialogDataManager.updateAll(
                DialogTransformUtils.getListLocalDialogsFromQBDialogs(objectsCollection));
    }

    @Override
    public void delete(ConnectycubeChatDialog object) {
        Dialog dialog = DialogTransformUtils.createLocalDialog(object);
        dialogDataManager.delete(dialog);
    }

    @Override
    public void deleteById(long id) {
        dialogDataManager.deleteById(id);
    }

    @Override
    public boolean exists(long id) {
        return dialogDataManager.exists(id);
    }

    public boolean exists(String dialogId) {
        return getByDialogId(dialogId) != null;
    }

    @Override
    public void createOrUpdateAll(Collection<ConnectycubeChatDialog> ConnectycubeChatDialogsList) {
        dialogDataManager.createOrUpdateAll(
                DialogTransformUtils.getListLocalDialogsFromQBDialogs(ConnectycubeChatDialogsList));
    }

    public void deleteById(String dialogId) {
        dialogDataManager.deleteById(dialogId);
    }

    public List<ConnectycubeChatDialog> getAllSorted() {
        return DialogTransformUtils.getListQBDialogsFromLocalDialogs(dialogDataManager.getAllSorted());
    }

    public List<ConnectycubeChatDialog> getSkippedSorted(int startRow, int perPage) {
        return DialogTransformUtils.getListQBDialogsFromLocalDialogs(dialogDataManager.getSkippedSorted(startRow, perPage));
    }

    public long getAllCount() {
        return dialogDataManager.getAllCount();
    }

    public List<ConnectycubeChatDialog> getAll() {
        return DialogTransformUtils.getListQBDialogsFromLocalDialogs(dialogDataManager.getAll());
    }

    @Override
    public List<ConnectycubeChatDialog> getAllSorted(String sortedColumn, boolean ascending) {
        return DialogTransformUtils.getListQBDialogsFromLocalDialogs(
                dialogDataManager.getAllSorted(sortedColumn, ascending));
    }

    public void addObserver(Observer observer) {
        dialogDataManager.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        dialogDataManager.deleteObserver(observer);
    }

    public String getObserverKey() {
        return dialogDataManager.getObserverKey();
    }
}
