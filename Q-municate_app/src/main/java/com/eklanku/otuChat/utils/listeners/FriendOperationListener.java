package com.eklanku.otuChat.utils.listeners;

public interface FriendOperationListener {
//ToDo deleteMe
    void onAcceptUserClicked(int position, int userId);

    void onRejectUserClicked(int position, int userId);
}