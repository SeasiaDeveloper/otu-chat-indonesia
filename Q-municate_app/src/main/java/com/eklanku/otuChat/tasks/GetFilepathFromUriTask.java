package com.eklanku.otuChat.tasks;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.fragments.dialogs.base.ProgressDialogFragment;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.fragments.dialogs.base.ProgressDialogFragment;
import com.eklanku.otuChat.utils.SchemeType;
import com.eklanku.otuChat.utils.StringUtils;
import com.eklanku.otuChat.utils.MediaUtils;
import com.eklanku.otuChat.utils.listeners.OnMediaPickedListener;
import com.quickblox.q_municate_core.core.concurrency.BaseAsyncTask;
import com.quickblox.q_municate_db.models.Attachment;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class GetFilepathFromUriTask extends BaseAsyncTask<Intent, Void, File> {

    private WeakReference<FragmentManager> fmWeakReference;
    private OnMediaPickedListener listener;
    private int requestCode;
    private Attachment.Type type;

    public GetFilepathFromUriTask(FragmentManager fragmentManager, OnMediaPickedListener listener, Attachment.Type type, int requestCode) {
        this.fmWeakReference = new WeakReference<>(fragmentManager);
        this.listener = listener;
        this.requestCode = requestCode;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgress();
    }

    @Override
    public File performInBackground(Intent... params) throws Exception {
        Intent data = params[0];

        String filePath = null;
        Uri uri = data.getData();
        String uriScheme = uri.getScheme();

        boolean isFromGoogleApp = uri.toString().startsWith(SchemeType.SCHEME_CONTENT_GOOGLE);
        boolean isKitKatAndUpper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (SchemeType.SCHEME_CONTENT.equalsIgnoreCase(uriScheme) && !isFromGoogleApp && !isKitKatAndUpper) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = App.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    if (columnIndex >= 0) {
                        filePath = cursor.getString(columnIndex);
                        return new File(MediaUtils.getPathWithExtensionInLowerCase(filePath));
                    }
                }
                cursor.close();
            }
        }
        if (SchemeType.SCHEME_FILE.equalsIgnoreCase(uriScheme)) {
            filePath = uri.getPath();
        } else if (type.equals(Attachment.Type.DOC)) {
            String fileName = getFileName(uri);
            filePath = MediaUtils.saveUriToFile(uri, fileName);
        } else {
            filePath = MediaUtils.saveUriToFile(uri);
        }
        if (TextUtils.isEmpty(filePath)) {
            throw new IOException("Can't find a filepath for URI " + uri.toString());
        }
        File file = new File(MediaUtils.getPathWithExtensionInLowerCase(filePath));
        setCorrectRotationIfNeed(file);
        return file;
    }

    @Override
    public void onResult(File file) {
        hideProgress();
        Log.w(GetFilepathFromUriTask.class.getSimpleName(), "onResult listener = " + listener);
        if (listener != null) {
            if (type.equals(Attachment.Type.OTHER)) {
                type = StringUtils.getAttachmentTypeByFile(file);
            }
            listener.onMediaPicked(requestCode, type, file);
        }
    }

    @Override
    public void onException(Exception e) {
        hideProgress();
        Log.w(GetFilepathFromUriTask.class.getSimpleName(), "onException listener = " + listener);
        if (listener != null) {
            listener.onMediaPickError(requestCode, e);
        }
    }

    private void showProgress() {
        FragmentManager fragmentManager = fmWeakReference.get();
        if (fragmentManager != null) {
            ProgressDialogFragment.show(fragmentManager);
        }
    }

    private void hideProgress() {
        FragmentManager fragmentManager = fmWeakReference.get();
        if (fragmentManager != null) {
            ProgressDialogFragment.hide(fragmentManager);
        }
    }

    private void setCorrectRotationIfNeed(File file) {
        if (StringUtils.isImageFile(file)) {
            MediaUtils.normalizeRotationImageIfNeed(file);
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        } else {
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = App.getInstance().getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
            if (fileName == null) {
                fileName = uri.getPath();
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }
        }
        return fileName;
    }
}