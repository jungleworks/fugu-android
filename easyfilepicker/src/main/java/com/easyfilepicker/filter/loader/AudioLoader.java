package com.easyfilepicker.filter.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

public class AudioLoader extends CursorLoader {
    private static final String[] AUDIO_PROJECTION = {
            //Base File
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            //Audio File
            MediaStore.Audio.Media.DURATION
    };

    private AudioLoader(Context context, Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public AudioLoader(Context context) {
        super(context);

//        setProjection(AUDIO_PROJECTION);
//        setUri(MediaStore.Files.getContentUri("external"));
//        setSortOrder(MediaStore.Audio.Media.DATE_ADDED + " DESC");
//
//        setSelection(MIME_TYPE + "=? or "
//                + MIME_TYPE + "=? or "
////                + MIME_TYPE + "=? or "
//                + MIME_TYPE + "=?");
//        String[] selectionArgs;
//        selectionArgs = new String[]{"audio/mpeg", "audio/mp3", "audio/x-ms-wma"};
//        setSelectionArgs(selectionArgs);

        // Return only audio and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        setProjection(AUDIO_PROJECTION);
        setUri(queryUri);
        setSortOrder(MediaStore.Video.Media.DATE_ADDED + " DESC");

        setSelection(selection);
        setSelectionArgs(null);
    }
}
