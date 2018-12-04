package com.eklanku.otuChat.utils;

public interface MimeType {

    String IMAGE_MIME = "image/*";
    String IMAGE_MIME_JPEG = "image/jpeg";
    String IMAGE_MIME_PNG = "image/png";
    String VIDEO_MIME_MP4 = "video/mp4";
    String AUDIO_MIME_MP3 = "audio/mpeg";
    String DOCUMENT_MIME_MSWORD = "application/msword";
    String DOCUMENT_MIME_PDF = "application/pdf";
    String DOCUMENT_MIME_EXCEL = "application/vnd.ms-excel";
    String DOCUMENT_MIME_POWER_POINT = "application/vnd.ms-powerpoint";
    String[] mediaMimeTypes = {IMAGE_MIME_JPEG, IMAGE_MIME_PNG, VIDEO_MIME_MP4, AUDIO_MIME_MP3};
    String[] docsMimeTypes = {DOCUMENT_MIME_MSWORD, DOCUMENT_MIME_PDF, DOCUMENT_MIME_EXCEL, DOCUMENT_MIME_POWER_POINT};

    String STREAM_MIME = "application/octet-stream";

    String IMAGE_MIME_PREFIX = "image";
    String VIDEO_MIME_EXTENSION_MP4 = "mp4";
    String AUDIO_MIME_EXTENSION_MP3 = "mp3";
}