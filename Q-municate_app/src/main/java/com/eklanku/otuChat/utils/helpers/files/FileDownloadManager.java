package com.eklanku.otuChat.utils.helpers.files;

import android.util.Log;

import com.connectycube.core.io.ByteStreams;
import com.connectycube.storage.ConnectycubeStorage;
import com.eklanku.otuChat.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FileDownloadManager {
    private static volatile FileDownloadManager instance;

    private FileDownloadManager() {
    }

    public static FileDownloadManager getInstance() {
        if (instance == null) {
            synchronized (FileDownloadManager.class) {
                if (instance == null) {
                    instance = new FileDownloadManager();
                }
            }
        }
        return instance;
    }

    public Observable<File> downLoadFile(String fileName, String uid, FileUtils fileUtils) {
        Observable<File> result;
        File file = fileUtils.checkIfFileExistByName(fileName);
        if (!file.exists()) {
            result = downLoadFile(uid).flatMap((Func1<InputStream, Observable<File>>)
                    inputStream -> saveFile(inputStream, fileName, fileUtils.getFilesFolderPath()));
        } else {
            result = Observable.defer(() -> Observable.just(file));
        }
        return result.observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<InputStream> downLoadFile(final String uid) {
        return Observable.fromCallable(() -> ConnectycubeStorage.downloadFile(uid).perform())
                .subscribeOn(Schedulers.io());
    }

    private Observable<File> saveFile(InputStream is, String fileName, String rootFolder) {
        return Observable.defer(() -> {
            File file = null;
            try {
                file = new File(rootFolder, fileName);
                byte[] content = ByteStreams.toByteArray(is);
                FileOutputStream out = new FileOutputStream(file);
                out.write(content);
                out.close();
            } catch (IOException e) {
                Observable.error(e);
            }
            return Observable.just(file);
        });
    }
}
