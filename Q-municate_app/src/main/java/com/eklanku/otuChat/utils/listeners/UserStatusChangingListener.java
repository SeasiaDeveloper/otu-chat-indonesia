package com.eklanku.otuChat.utils.listeners;

public interface UserStatusChangingListener {

    void onChangedUserStatus(int userId, boolean online);
}