package com.skeleton.mvp.utils;

import android.content.Context;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.skeleton.mvp.BuildConfig;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by rajatdhamija
 * 07/05/18.
 */

public class GetMimeTypeFromUrl {
    public static String getMimeType(String url, Context context) {
        String type = null;
        String extensions[] = url.split(Pattern.quote("."));
        String extension = extensions[extensions.length - 1];
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type==null){
            Uri contentURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(url));
            type = context.getContentResolver().getType(contentURI);
        }
        return type;
    }
}
