package com.eklanku.otuChat.tasks;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.fragments.dialogs.base.ProgressDialogFragment;
import com.eklanku.otuChat.utils.SchemeType;
import com.eklanku.otuChat.utils.StringUtils;
import com.eklanku.otuChat.utils.MediaUtils;
import com.eklanku.otuChat.utils.listeners.OnMediaPickedListener;
import com.quickblox.q_municate_core.core.concurrency.BaseAsyncTask;
import com.quickblox.q_municate_db.models.Attachment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
                        File file = new File(MediaUtils.getPathWithExtensionInLowerCase(filePath));
                        File optimizedFile =  optimizeMediaFile(file);
                        if(optimizedFile != null && optimizedFile.length() > 0){
                            file = optimizedFile;
                        }
                        return file;
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

        File optimizedFile =  optimizeMediaFile(file);
        if(optimizedFile != null && optimizedFile.length() > 0){
            file = optimizedFile;
        }
        return file;
    }

    private File optimizeMediaFile(File file){
        Bitmap bmp = getResizedBitmap(file.getPath());
        if( bmp != null)
        {
            int quality = 100;
            if(file.length() > 1024 * 100 ){ //more than 100KB
                quality = 80;
            }
            String newFilePath = MediaUtils.getPathWithoutExtensionInLowerCase(file.getPath()) + ".jpg";
            File file1 = new File( newFilePath );
            OutputStream os = null;
            try
            {
                os = new BufferedOutputStream(new FileOutputStream(file1));
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, os);
                bmp.recycle();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            finally
            {
                MediaUtils.closeSilently(os);
            }
            return file1;
        }else{
            return null;
        }
    }

    private Bitmap getResizedBitmap(String filePath){
        int maxSideLength = 1280;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if(options.outWidth > maxSideLength || options.outHeight > maxSideLength)
        {
            options.inSampleSize = calculateInSampleSize(options, maxSideLength, maxSideLength);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            int scaledWidth, scaledHeight;
            if(bitmap.getHeight() >  bitmap.getWidth()){
                scaledHeight = maxSideLength;
                scaledWidth = (int) (( (double)bitmap.getWidth() / (double)bitmap.getHeight() ) * maxSideLength);
            }else{
                scaledWidth =  maxSideLength;
                scaledHeight = (int) (( (double)bitmap.getHeight() / (double)bitmap.getWidth() ) * maxSideLength);
            }
            Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
            bitmap.recycle();
            return resultBitmap;
        }
        else{
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        }

    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
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