package com.easyfilepicker.filter;

import com.easyfilepicker.filter.callback.FileLoaderCallbacks;
import com.easyfilepicker.filter.callback.FilterResultCallback;
import com.easyfilepicker.filter.entity.AudioFile;
import com.easyfilepicker.filter.entity.ImageFile;
import com.easyfilepicker.filter.entity.NormalFile;
import com.easyfilepicker.filter.entity.VideoFile;

import androidx.fragment.app.FragmentActivity;

import static com.easyfilepicker.filter.callback.FileLoaderCallbacks.*;


public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback){
        activity.getSupportLoaderManager().initLoader(0, null,
                new FileLoaderCallbacks(activity, callback, TYPE_IMAGE));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, TYPE_VIDEO));
    }

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback){
        activity.getSupportLoaderManager().initLoader(2, null,
                new FileLoaderCallbacks(activity, callback, TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity,
                                FilterResultCallback<NormalFile> callback, String[] suffix){
        activity.getSupportLoaderManager().initLoader(3, null,
                new FileLoaderCallbacks(activity, callback, TYPE_FILE, suffix));
    }
}
