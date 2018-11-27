package com.eklanku.otuChat.utils.bridges;

public interface LoadingBridge {

    void showProgress();

    void hideProgress();

    void hideProgressImmediately();

    void showActionBarProgress();

    void hideActionBarProgress();
}