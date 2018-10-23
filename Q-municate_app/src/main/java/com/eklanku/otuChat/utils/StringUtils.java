package com.eklanku.otuChat.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.eklanku.otuChat.App;
import com.connectycube.core.helper.MimeUtils;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.R;;
import com.quickblox.q_municate_db.models.Attachment;

import java.io.File;
import java.util.ArrayList;
import com.eklanku.otuChat.ui.activities.main.MainActivity;

public class StringUtils {

    public static String createHumanNameFromSystemPermission(String permission){
        String permissionName = permission.replace("android.permission.", "");
        String[] words = permissionName.split("_", 0);
        String newPermissionName = "";
        for(String word : words){
            newPermissionName+= word.substring(0,1) + word.substring(1).toLowerCase() + " ";
        }

        return newPermissionName;
    }

    public static String createCompositeString(ArrayList<String> permissions){
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : permissions){
            stringBuilder.append(createHumanNameFromSystemPermission(string));
            if (permissions.indexOf(string) == permissions.size() -2){
                stringBuilder.append(" and ");
            } else if (permissions.indexOf(string) == permissions.size() -1){
                stringBuilder.append("");
            } else {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public static Attachment.Type getAttachmentTypeByRequestCode(int requestCode) {
        Attachment.Type type;
        switch (requestCode) {
            case MediaUtils.IMAGE_REQUEST_CODE:
                type = Attachment.Type.IMAGE;
                break;
            case MediaUtils.CAMERA_PHOTO_REQUEST_CODE:
                type = Attachment.Type.IMAGE;
                break;
            case MediaUtils.CAMERA_VIDEO_REQUEST_CODE:
                type = Attachment.Type.VIDEO;
                break;
            case MediaUtils.AUDIO_REQUEST_CODE:
                type = Attachment.Type.AUDIO;
                break;
            case MediaUtils.LOCATION_REQUEST_CODE:
                type = Attachment.Type.LOCATION;
                break;
            case MediaUtils.DOCUMENT_REQUEST_CODE:
                type = Attachment.Type.DOC;
                break;
            case MediaUtils.CONTACT_REQUEST_CODE:
                type = Attachment.Type.CONTACT;
                break;
            default:
                type = Attachment.Type.OTHER;
                break;
        }
        return type;
    }

    public static String getAttachmentNameByType(Context context, Attachment.Type type) {
        String attachmentName = "";

        switch (type) {
            case IMAGE:
                attachmentName = context.getString(R.string.dialog_attach_image);
                break;
            case AUDIO:
            case VOICE:
                attachmentName = context.getString(R.string.dialog_attach_audio);
                break;
            case VIDEO:
                attachmentName = context.getString(R.string.dialog_attach_video);
                break;
            case LOCATION:
                attachmentName = context.getString(R.string.dialog_location);
                break;
            case DOC:
                attachmentName = context.getString(R.string.dialog_document);
                break;
            case CONTACT:
                attachmentName = context.getString(R.string.dialog_contact);
                break;
            //will be extend for new attachment types
        }

        return attachmentName;
    }

    public static Attachment.Type getAttachmentTypeByFile(File file) {
        return getAttachmentTypeByFileName(file.getName());
    }

    public static Attachment.Type getAttachmentTypeByFileName(String fileName){
        Attachment.Type attachmentType;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);
        if (mimeType == null){
            attachmentType = Attachment.Type.OTHER;
        } else if (mimeType.startsWith("image")){
            attachmentType = Attachment.Type.IMAGE;
        } else if (mimeType.startsWith("audio")){
            attachmentType = Attachment.Type.AUDIO;
        } else if (mimeType.startsWith("video")){
            attachmentType = Attachment.Type.VIDEO;
        } else if (mimeType.startsWith("text")) {
            attachmentType = Attachment.Type.DOC;
        } else {
            attachmentType = Attachment.Type.OTHER;
        }

        return attachmentType;
    }

    public static boolean isImageFile(File file) {
        return Attachment.Type.IMAGE.equals(StringUtils.getAttachmentTypeByFile(file));
    }

    public static String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = App.getInstance().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static boolean isNumeric(String strObject){
        String regexStr = "^[0-9]*$";
        if(strObject.trim().matches(regexStr)) {
            return true;
        }
        return false;
    }
}