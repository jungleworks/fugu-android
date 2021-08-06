package com.easyfilepicker;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;


public class Util implements AppConstant.MimeTypeConstants {

    private static final String AppName = "Hippo";

    public static boolean detectIntent(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String getDurationString(long duration) {
//        long days = duration / (1000 * 60 * 60 * 24);
        long hours = (duration % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;

        String hourStr = (hours < 10) ? "0" + hours : hours + "";
        String minuteStr = (minutes < 10) ? "0" + minutes : minutes + "";
        String secondStr = (seconds < 10) ? "0" + seconds : seconds + "";

        if (hours != 0) {
            return hourStr + ":" + minuteStr + ":" + secondStr;
        } else {
            return minuteStr + ":" + secondStr;
        }
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample.pptx
     *
     * @param url String of a URL
     * @return the file name of URL with suffix
     */
    public static String extractFileNameWithSuffix(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample
     *
     * @param url String of a URL
     * @return the file name of URL without suffix
     */
    public static String extractFileNameWithoutSuffix(String url) {
        try {
            return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            try {
                return url.substring(url.lastIndexOf("/") + 1);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return "";
        }
    }

    public static String getMuid(String name) {
        try {
            return name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download/
     *
     * @param url String of a URL
     * @return the path of URL with the file separator
     */
    public static String extractPathWithSeparator(String url) {
        return url.substring(0, url.lastIndexOf("/") + 1);
    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download
     *
     * @param url String of a URL
     * @return the path of URL without the file separator
     */
    public static String extractPathWithoutSeparator(String url) {
        Log.d("easyFilePickerURL", url);
        try{
            return url.substring(0, url.lastIndexOf("/"));
        } catch (Exception e){
            return url;
        }

    }

    /**
     * Extract the suffix in a URL
     * /storage/emulated/legacy/Download/sample.pptx = pptx
     *
     * @param url String of a URL
     * @return the suffix of URL
     */
    public static String extractFileSuffix(String url) {
        if (url.contains(".")) {
            return url.substring(url.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static String getFilePath(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                File file = new File(contentUri.getPath());
                final String[] split = file.getPath().split(":");//split the path.
                return getPath(context, contentUri);
            } catch (Exception e) {
                return getPath(context, contentUri);
            }
        } else {
            return getPath(context, contentUri);
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        try {
            //check here to KITKAT or new version
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri;
                    try {
                        contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    } catch (Exception e) {
                        contentUri = uri;
                        e.printStackTrace();
                    }

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                } else if (isGoogleDriveDocument(uri)) {
                    try {
                        String mimeType = context.getContentResolver().getType(uri);

                        String fileName = "drive_file";
                        if (mimeType.equals(MIME_TYPE_IMAGE_JPEG)) {
                            fileName = fileName + ".jpeg";
                        } else if (mimeType.equals(MIME_TYPE_IMAGE_PNG)) {
                            fileName = fileName + ".png";
                        } else if (mimeType.equals(MIME_TYPE_PDF)) {
                            fileName = fileName + ".pdf";
                        } else if (mimeType.equals(MIME_TYPE_CSV_1) || mimeType.equals(MIME_TYPE_CSV_2)) {
                            fileName = fileName + ".csv";
                        } else if (mimeType.equals(MIME_TYPE_DOC)) {
                            fileName = fileName + ".doc";
                        } else if (mimeType.equals(MIME_TYPE_DOCX)) {
                            fileName = fileName + ".docx";
                        } else if (mimeType.equals(MIME_TYPE_PPT)) {
                            fileName = fileName + ".ppt";
                        } else if (mimeType.equals(MIME_TYPE_PPTX)) {
                            fileName = fileName + ".pptx";
                        } else if (mimeType.equals(MIME_TYPE_XLS)) {
                            fileName = fileName + ".xls";
                        } else if (mimeType.equals(MIME_TYPE_XLSX)) {
                            fileName = fileName + ".xlsx";
                        } else if (mimeType.equals(MIME_TYPE_TXT)) {
                            fileName = fileName + ".txt";
                        } else {
                            return null;
                        }

                        InputStream inputStream = context.getContentResolver().openInputStream(uri);
                        File myFile = new File(getDirectory(FileType.GENERAL_FILE), fileName);
                        FileOutputStream fileOutputStream = new FileOutputStream(myFile);

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileOutputStream.close();
                        inputStream.close();
                        return myFile.getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveDocument(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * The type of file being Saved
     */
    enum FileType {

        LOG_FILE("logs", ".log"),
        IMAGE_FILE("snapshots", ""),
        GENERAL_FILE("public", ".txt"),
        PRIVATE_FILE("system", ".sys");

        public final String extension;
        public final String directory;

        FileType(String relativePath, String extension) {
            this.extension = extension;
            this.directory = relativePath;
        }
    }

    public static File getDirectory(FileType type) {

        try {
            String strFolder = Environment.getExternalStorageDirectory()
                    + File.separator + "Tookan" + File.separator + type.directory;
            File folder = new File(strFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            return folder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final int NOT_FOUND = -1;

    /**
     * The extension separator character.
     * @since 1.4
     */
    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';


    /**
     * Gets the extension of a filename.
     * <p>
     * This method returns the textual part of the filename after the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the filename is {@code null}.
     */
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p>
     * This method also checks that there is no directory separator after the last dot. To do this it uses
     * {@link #indexOfLastSeparator(String)} which will handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param filename
     *            the filename to find the last extension separator in, null returns -1
     * @return the index of the last extension separator character, or -1 if there is no such character
     */
    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename  the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1024 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableSize(long bytes, boolean si) {
        int unit = si ? 1024 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.0f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getDirectoryPath(String folder) {
        return Environment.getExternalStorageDirectory()
                + File.separator + AppName + File.separator + folder;
    }

    public static String getOrCreateDirectoryPath(String folderName) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + AppName + File.separator + folderName;
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdirs();

        return path;
    }

    public static String getFileName(String filename, String muid) {
        if(!filename.contains(muid)) {
            String ext = Util.getExtension(filename);
            String name = Util.extractFileNameWithoutSuffix(filename);
            filename = name + "_" + muid + "." + ext;
        }
        return filename;
    }
}
