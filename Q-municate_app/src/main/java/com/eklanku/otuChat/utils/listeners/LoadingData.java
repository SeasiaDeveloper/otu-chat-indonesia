package com.eklanku.otuChat.utils.listeners;

public interface LoadingData {

    void onShowLoading();

    void onHideLoading();

    void onErrorLoading(Throwable throwable);
}
