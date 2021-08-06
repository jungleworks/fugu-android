package com.skeleton.mvp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.FuguConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.CreatePollActivity;
import com.skeleton.mvp.adapter.FuguAttachmentAdapter;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.model.FuguFileDetails;
import com.skeleton.mvp.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import id.zelory.compressor.Compressor;


/**
 * Created by Bhavya on 12-05-2016.
 */
public class FuguImageUtils implements FuguAppConstant, Animation.AnimationListener {

    private static final String TAG = FuguImageUtils.class.getSimpleName();

    private Activity activity, imageWithTextMessageActivity;

    private int cameraRequestCode;
    private int galleryRequestCode;
    private int selectFileRequestCode;
    private int selectAudioRequestCode;
    private int selectVideoRequestCode;
    private int createPollRequestCode = START_POLL;


    private int cameraPermission;
    private int readFilePermission;
    private int openGalleryPermission;
    private int saveBitmapPermission;
    private int readFile;
    private Animation fuguAnimSlideUp, fuguAnimSlideDown;
    private Dialog dialog;
    private String muid;
    private String extension = "jpg";
    private boolean showDocs = false;
    private boolean isOneToOne = false;
    private List<WorkspacesInfo> workspaceInfo;

    public FuguImageUtils(Activity activity) {

        this.activity = activity;

        cameraPermission = PERMISSION_CONSTANT_CAMERA;
        readFilePermission = PERMISSION_READ_IMAGE_FILE;
        openGalleryPermission = PERMISSION_CONSTANT_GALLERY;
        saveBitmapPermission = PERMISSION_SAVE_BITMAP;
        readFile = PERMISSION_READ_FILE;
        try {
            workspaceInfo = com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getWorkspacesInfo();
        } catch (Exception e) {

        }
    }

    public void setCallbaks(int cameraRequestCode, int galleryRequestCode, int selectFileRequestCode,
                            int selectAudioRequestCode, int selectVideoRequestCode, int createPollRequestCode, boolean showDocs, boolean isOneToOne) {
        this.cameraRequestCode = cameraRequestCode;
        this.galleryRequestCode = galleryRequestCode;
        this.selectFileRequestCode = selectFileRequestCode;
        this.selectAudioRequestCode = selectAudioRequestCode;
        this.selectVideoRequestCode = selectVideoRequestCode;
        this.createPollRequestCode = createPollRequestCode;
        this.showDocs = showDocs;
        this.isOneToOne = isOneToOne;
    }

    public void showImageChooser(int cameraRequestCode, int galleryRequestCode, int selectFileRequestCode,
                                 int selectAudioRequestCode, int selectVideoRequestCode, int createPollRequestCode, boolean showDocs, boolean isOneToOne) {

        this.cameraRequestCode = cameraRequestCode;
        this.galleryRequestCode = galleryRequestCode;
        this.selectFileRequestCode = selectFileRequestCode;
        this.selectAudioRequestCode = selectAudioRequestCode;
        this.selectVideoRequestCode = selectVideoRequestCode;
        this.showDocs = showDocs;
        this.createPollRequestCode = createPollRequestCode;

        try {
            dialog = new Dialog(activity,
                    R.style.FuguDialogTheme);
            fuguAnimSlideUp = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                    R.anim.fugu_slide_up);
            fuguAnimSlideUp.setAnimationListener(this);

            fuguAnimSlideDown = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                    R.anim.fugu_slide_down);
            fuguAnimSlideDown.setAnimationListener(this);

            dialog.setContentView(R.layout.fugu_dialog_image_chooser);
            RelativeLayout rlDialog = dialog.findViewById(R.id.rlDialog);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            RecyclerView rvAttachment = dialog.findViewById(R.id.rvAttachment);
            final LinearLayout llRecycler = dialog.findViewById(R.id.llRecycler);
            FuguAttachmentAdapter fuguAttachmentAdapter = new FuguAttachmentAdapter(activity, showDocs, isOneToOne);
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            rvAttachment.setLayoutManager(layoutManager);
            rvAttachment.setAdapter(fuguAttachmentAdapter);
            rlDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llRecycler.clearAnimation();
                    llRecycler.setAnimation(fuguAnimSlideDown);
                }
            });
            llRecycler.startAnimation(fuguAnimSlideUp);

            fuguAttachmentAdapter.setOnAttachListener(action -> {
                switch (action) {
                    case OPEN_CAMERA_ADD_IMAGE:
                        startCamera();
                        dialog.dismiss();
                        break;
                    case OPEN_GALLERY_ADD_IMAGE:
                        openGallery();
                        dialog.dismiss();
                        break;
                    case SELECT_FILE:
                        selectFile();
                        dialog.dismiss();
                        break;
                    case SELECT_VIDEO:
                        selectVideo();
                        dialog.dismiss();
                        break;
                    case SELECT_DOCUMENT:
                        selectDocument();
                        dialog.dismiss();
                        break;
                    case START_POLL:
                        openCreatePollActivity();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            });

            TextView btnCancel = dialog.findViewById(R.id.btnCancel);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llRecycler.clearAnimation();
                    llRecycler.startAnimation(fuguAnimSlideDown);
                }
            });
            dialog.show();

        } catch (Exception e) {

            FuguLog.e(TAG, "Crashed: " + e);
        }
    }

    public void openCreatePollActivity() {
        activity.startActivityForResult(new Intent(activity, CreatePollActivity.class), createPollRequestCode);
    }

    public void selectVideo() {
        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                openGalleryPermission)) return;

        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("video/*");
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            activity.startActivityForResult(photoPickerIntent, selectVideoRequestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.fugu_no_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    public void selectDocument() {
        FuguLog.e(TAG, "select doc");

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                readFile)) return;

        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("*/*");
//            String[] mimetypes = {"application/msword", "application/vnd.ms-powerpoint", "application/pdf",
//                    "application/vnd.ms-excel", "application/x-wav", "application/rtf", "text/csv",
//                    "application/vnd.android.package-archive", "text/*"};
//            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            activity.startActivityForResult(photoPickerIntent, selectFileRequestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.fugu_file_not_found), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method to start the Camera
     */
    public void startCamera() {

        FuguLog.e(TAG, "startCamera");

        /** Code to check whether the Location Permission is Granted */
        String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /*  Check if the Permission for the Camera was Granted  */
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                permissionsRequired, "Please grant permission to access Camera",
                cameraPermission)) return;


        /*  Check whether the Camera feature is available or not    */
        if (!isCameraAvailable()) {
            Toast.makeText(activity, "Camera feature unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }

        /*  Check for the SD CARD or External Storage   */
        if (!isExternalStorageAvailable()) {
            Toast.makeText(activity, "External storage unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            String muid = UUID.randomUUID().toString();
            Log.d("Muid check startCamera", muid);
            String fileName = CommonData.getWorkspaceResponse(workspaceInfo.
                    get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "")
                    .replaceAll("'s", "") + "_" + muid + ".jpg";
            CommonData.setTime(muid);
            File fileToBeWritten = new File(getDirectory(FileType.IMAGE_FILE), fileName);
            if (!fileToBeWritten.exists()) {
                try {
                    fileToBeWritten.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider", fileToBeWritten));
            if (cameraRequestCode == 0) {
                cameraRequestCode = OPEN_CAMERA_ADD_IMAGE;
            }
            activity.startActivityForResult(takePictureIntent, cameraRequestCode);
        }
    }

    /**
     * Method to retrieve the App Directory,MA
     * where files like logs can be Saved
     *
     * @param type The FileType
     * @return directory corresponding to the FileType
     */
    public File getDirectory(FileType type) {

        try {
            String strFolder = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + type.directory;

            String[] filePathArray = strFolder.split("/");
            if (filePathArray[filePathArray.length - 1].equals(FuguAppConstant.IMAGE)) {

                if (workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getMediaVisibility() == 0) {

                    Log.i("true", "true");
                    strFolder = strFolder.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES);
                }

            }
            Log.i("Path", strFolder);

            File folder = new File(strFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (filePathArray[filePathArray.length - 1].equals(FuguAppConstant.IMAGE)) {
                if (workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getMediaVisibility() == 0) {
                    File f = new File(strFolder + "/.nomedia");
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    Log.i("FilePath", strFolder);
                }
            }

            return folder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isCameraAvailable() {

        FuguLog.e(TAG, "isCameraAvailable");

        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Method to check whether the Camera feature
     * is Available or not
     *
     * @return
     */
    private boolean isExternalStorageAvailable() {

        FuguLog.e(TAG, "isExternalStorageAvailable");

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Method to perform operation on image after
     * selecting from Gallery
     *
     * @param uri
     */
    public void copyFileFromUri(Uri uri, String extension) throws IOException {

        FuguLog.e(TAG, "copyFileFromUri");
        if (activity == null)
            return;

        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        muid = UUID.randomUUID().toString();
        Log.d("Muid check copyFileFromUri", muid);
        String fileName = CommonData.getWorkspaceResponse(workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + "_" + muid + "." + extension;
        CommonData.setTime(muid);
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File(getDirectory(FileType.IMAGE_FILE), fileName)
        );

        byte[] buffer = new byte[1024];

        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1)
            fileOutputStream.write(buffer, 0, bytesRead);

        fileOutputStream.close();

        inputStream.close();

    }

    /**
     * Method to compress and save the bitmap to a file
     *
     * @return
     */
    public String compressAndSaveBitmap(Context context, String extension, String originalName) {

        FuguLog.e(TAG, "compressAndSaveBitmap");

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        if (screenWidth > 1024)
            screenWidth = 1024;

        return compressAndSaveBitmap(null, screenWidth, extension, originalName);
    }

    public String compressAndSaveBitmap(ImageView imgSnapshot, int squareEdge, String extension, String originalName) {

        FuguLog.e(TAG, "compressAndSaveBitmap");

        return compressAndSaveBitmap(originalName, imgSnapshot, squareEdge, squareEdge, extension);
    }

    /**
     * Method to compress and save the Bitmap
     *
     * @param imgSnapshot
     * @param defaultWidth
     * @param defaultHeight
     */
    public String compressAndSaveBitmap(String originalName, ImageView imgSnapshot, int defaultWidth, int defaultHeight, String extension) {

        FuguLog.e(TAG, "compressAndSaveBitmap");

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                saveBitmapPermission)) return null;


        int targetWidth = 0;
        int targetHeight = 0;

        if (imgSnapshot != null) {
            targetWidth = imgSnapshot.getWidth();
            if (targetWidth > 1024) targetWidth = 1024;

            targetHeight = imgSnapshot.getHeight();
            if (targetHeight > 1024) targetHeight = 1024;
        }

        if (targetWidth == 0 || targetHeight == 0) {

            targetWidth = defaultWidth;
            targetHeight = defaultHeight;

            FuguLog.e(TAG, "Bitmap Scaling Default values (" + defaultWidth + ", " + defaultHeight + ")");
        }
        String fileName = originalName + "_" + CommonData.getTime() + "." + extension;
        FileType type = FuguAppConstant.FILE_TYPE_MAP.get(extension.toLowerCase());
        if (type == null) {
            type = FileType.IMAGE_FILE;
        }
        if (type.directory.equals(FuguAppConstant.IMAGE)) {
            if (new File(getDirectory(FileType.IMAGE_FILE), fileName).exists()) {
                return new File(getDirectory(FileType.IMAGE_FILE), fileName).getAbsolutePath();
            } else {
                fileName = originalName + "_" + CommonData.getTime() + "." + "png";
                if (new File(getDirectory(FileType.PNG_FILE), fileName).exists()) {
                    return new File(getDirectory(FileType.PNG_FILE), fileName).getAbsolutePath();
                } else {
                    fileName = originalName + "_" + CommonData.getTime() + "." + "jpeg";
                    return new File(getDirectory(FileType.JPEG_FILE), fileName).getAbsolutePath();
                }
            }
        } else {
            return new File(getDirectory(FuguAppConstant.FILE_TYPE_MAP.get(extension.toLowerCase())), fileName).getAbsolutePath();
        }
    }

    /**
     * Method to center crop a bitmap
     *
     * @param source
     * @return
     */
    public Bitmap centerCrop(Bitmap source) {

        FuguLog.e(TAG, "centerCrop: Source bitmap: " + source);

        if (source == null) return null;


        if (source.getWidth() >= source.getHeight())
            return Bitmap.createBitmap(
                    source,
                    source.getWidth() / 2 - source.getHeight() / 2,
                    0,
                    source.getHeight(),
                    source.getHeight()
            );

        return Bitmap.createBitmap(
                source,
                0,
                source.getHeight() / 2 - source.getWidth() / 2,
                source.getWidth(),
                source.getWidth()
        );
    }


    /**
     * Method to open the Gallery view
     */

    public void openGallery() {

        FuguLog.e(TAG, "openGallery");


        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                openGalleryPermission)) return;

        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            String[] mimetypes = {"image/jpeg", "image/png", "image/jpg",
                    "image/gif"};
            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            activity.startActivityForResult(photoPickerIntent, galleryRequestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.fugu_no_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    public void selectFile() {
        FuguLog.e(TAG, "selectPdf");

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                readFile)) return;

        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("audio/*");
            activity.startActivityForResult(photoPickerIntent, selectAudioRequestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(),
                    activity.getString(R.string.fugu_file_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to convert the Bitmap into file
     *
     * @param imagePath
     */
    public Bitmap convertFileToBitmap(String imagePath, ImageView targetView) {

        FuguLog.e(TAG, "convertFileToBitmap");

        int requiredWidth = 0;
        int requiredHeight = 0;

        if (targetView != null) {
            requiredWidth = targetView.getWidth();
            requiredHeight = targetView.getHeight();
        }

        if (requiredHeight == 0 || requiredWidth == 0) {
            requiredWidth = 1024;
            requiredHeight = 1024;
        }

        return convertFileToBitmap(imagePath, requiredWidth, requiredHeight);
    }

    /**
     * Method to convert the Bitmap into file
     *
     * @param imagePath
     * @Developer: Rishabh
     * @Dated: 12.07.2015
     */
    public Bitmap convertFileToBitmap(String imagePath, int width, int height) {

        FuguLog.e(TAG, "convertFileToBitmap");

        // Check and ask for Permissions
        if (!FuguConfig.getInstance().askUserToGrantPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                readFilePermission)) return null;

        // Options to calculate the Bitmap Dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Avoid memory allocation
        options.inJustDecodeBounds = true;

        // Decode the byte array to know the width and height of bitmap
        BitmapFactory.decodeFile(imagePath, options);

        // Get the MimeType of the Image
        // String mimeType = options.outMimeType;

        // Calculate and set the Sample Size of the Bitmap
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Enable memory Allocation to the Bitmap
        options.inJustDecodeBounds = false;

        Bitmap targetBitmap;
        Bitmap sampledBitmap = null;
        try {
            // Create a Sample and Scaled down bitmap
            sampledBitmap = BitmapFactory.decodeFile(imagePath, options);


            // Cropped bitmap -> May throw an OutOfMemoryError
//            Bitmap croppedBitmap = centerCrop(sampledBitmap);
            Bitmap croppedBitmap = sampledBitmap;

            try {

                ExifInterface exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Matrix matrix = new Matrix();

                int angle = 0;

                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        angle = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        angle = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        angle = 270;
                        break;
                }

                matrix.postRotate(angle);

                // -> May throw an OutOfMemoryError
                targetBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0,
                        croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);

            } catch (Throwable err) {

                targetBitmap = croppedBitmap;
                err.printStackTrace();
            }
        } catch (Throwable err) {

            targetBitmap = sampledBitmap;
            err.printStackTrace();
        }

        return targetBitmap;
    }

    /**
     * A power of two value is calculated because the decoder
     * uses a final value by rounding down to the nearest power of two.
     *
     * @param options
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight) {

        FuguLog.e(TAG, "calculateInSampleSize");

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > requiredHeight || width > requiredWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > requiredHeight && (halfWidth / inSampleSize) > requiredWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * returns the byte size of the give bitmap
     */
    public int byteSizeOf(Bitmap bitmap) {

        FuguLog.e(TAG, "byteSizeOf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    /**
     * Method to convert a Bitmap to file
     *
     * @param bitmap
     * @param filePath
     * @throws IOException
     */
    public File convertBitmapToFile(Bitmap bitmap, String filePath) throws IOException {

        FuguLog.e(TAG, "convertBitmapToFile");

        // Create a file to write bitmap data
        File file = new File(filePath);
        file.createNewFile();

        if (bitmap == null)
            return null;

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50   /*ignored for PNG*/, bos);

        // Write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();

        return file;
    }

    /**
     * Method to draw text on bitmap
     *
     * @param bitmap
     * @param mText
     * @return
     */
    public Bitmap drawTextOnBitmap(Bitmap bitmap, String mText) {

        FuguLog.e(TAG, "drawTextOnBitmap");

        if (mText.indexOf('\n') == -1)
            return drawSingleLineTextOnBitmap(activity, bitmap, mText);

        return drawMultilineTextOnBitmap(bitmap, mText);

    }

    /**
     * Method to blur {@link Bitmap}s
     *
     * @param activity
     * @param source
     * @return blurred {@link Bitmap}
     */
    @SuppressLint("NewApi")
    public Bitmap blurImage(Activity activity, Bitmap source, int blurRadius) {

        FuguLog.e(TAG, "blurImage");

        RenderScript rsScript = RenderScript.create(activity);
        Allocation alloc = Allocation.createFromBitmap(rsScript, source);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript,
                alloc.getElement());
        blur.setRadius(blurRadius);
        // blur.setRadius(23);
        blur.setInput(alloc);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        Bitmap output = Bitmap.createBitmap(metrics.widthPixels, 400,
                source.getConfig());
        Allocation outAlloc = Allocation.createFromBitmap(rsScript, output);
        blur.forEach(outAlloc);
        outAlloc.copyTo(output);

        rsScript.destroy();
        return output;
    }

    public Bitmap drawSingleLineTextOnBitmap(Context context, Bitmap bitmap, String mText) {

        FuguLog.e(TAG, "drawSingleLineTextOnBitmap");

        try {
            float scale = context.getResources().getDisplayMetrics().density;

            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are immutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new anti-aliased Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(29, 24, 24));
            // text size in pixels
            paint.setTextSize((int) (33 * scale));
            // text font

            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 4;
            int y = (bitmap.getHeight() + bounds.height()) / 4;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to draw text on bitmap
     *
     * @param bitmap
     * @param mText
     * @return
     */
    public Bitmap drawMultilineTextOnBitmap(Bitmap bitmap, String mText) {

        FuguLog.e(TAG, "drawMultilineTextOnBitmap");

        try {
            float scale = activity.getResources().getDisplayMetrics().density;

            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are immutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);

            //Create anti-aliased paint
            TextPaint mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

            mTextPaint.setTextSize((int) (33 * scale));


            StaticLayout mTextLayout = new StaticLayout(mText, mTextPaint, canvas.getWidth(),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            canvas.save();

            Rect bounds = new Rect();
            mTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
            mTextPaint.setColor(Color.rgb(61, 61, 61));
            mTextPaint.setTextAlign(Paint.Align.CENTER);

            int origin_x = (bitmap.getWidth()) / 2;
            int origin_y = (bitmap.getHeight()) / 3;

            canvas.translate(origin_x, origin_y);
            mTextLayout.draw(canvas);
            canvas.restore();


            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to save the Bitmap
     *
     * @param bitmap
     */
    public String saveBitmapImage(Bitmap bitmap) throws IOException {

        FuguLog.e(TAG, "saveBitmapImage");
        String timeStamp = new SimpleDateFormat("ddMMyyyy_hhmmss", Locale.ENGLISH).format(new Date());
        String fileName = CommonData.getWorkspaceResponse(workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + "_" + timeStamp + ".gif";
        CommonData.setTime(timeStamp);
//        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName).getAbsolutePath();
        String path = new File(getDirectory(FileType.IMAGE_FILE), fileName).getAbsolutePath();

        convertBitmapToFile(bitmap, path);

        return path;
    }

    public FuguFileDetails saveFile(Uri uri, FileType type, Long channelId, String dateTime) {
        FuguLog.e(TAG, "saveFile");
//        if (muid == null) {
        String muid = UUID.randomUUID().toString();
        CommonData.setTime(muid);
        FuguFileDetails fuguFileDetails = new FuguFileDetails();

        if (uri != null) {
            Log.i("uricontentresolver", "not null");
        } else {
            Log.i("uricontentresolver", "null");
        }

        if (!TextUtils.isEmpty(uri.getScheme()) && uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fuguFileDetails.setFileName(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    try {
                        String[] extension = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.");
                        fuguFileDetails.setFileExtension(extension[extension.length - 1].toLowerCase());
                    } catch (Exception e) {
                        fuguFileDetails.setFileExtension(".mp3");
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            try {
                if (!TextUtils.isEmpty(uri.getScheme()) && uri.getScheme().equals("file")) {
                    String[] extension = uri.toString().split("\\.");
                    fuguFileDetails.setFileExtension(extension[extension.length - 1].toLowerCase());
                }
            } catch (Exception e) {

            }
        }
        try {
            String[] urlArray = getRealPathFromURI(uri).split("\\.");

            if ((urlArray.length > 0 && urlArray[urlArray.length - 1].toLowerCase().equals("gif"))) {
                fuguFileDetails.setFileExtension("gif");
                type = FileType.GIF_FILE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(fuguFileDetails.getFileExtension())) {
            fuguFileDetails.setFileExtension("jpg");
        }
        String fileName = fuguFileDetails.getFileName() + "_" + muid;

        try {
            File file;
            if (type.extension.equals(".default")) {
                file = new File(getDirectory(type), fileName + "." + fuguFileDetails.getFileExtension());
            } else {
                file = new File(getDirectory(type), fileName + type.extension);
            }

            String path = Environment.getExternalStorageDirectory() + File.separator + FuguAppConstant.APP_NAME_SHORT  +
                    File.separator + CommonData.getWorkspaceResponse(workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getFuguSecretKey()).getWorkspaceName().replaceAll(" ", "").replaceAll("'s", "") + File.separator + type.directory;

            String[] filePathArray = path.split("/");
            if (filePathArray[filePathArray.length - 1].equals(FuguAppConstant.IMAGE)) {

                if (workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getMediaVisibility() == 0) {

                    Log.i("true", "true");
                    path = path.replace(FuguAppConstant.IMAGE, FuguAppConstant.PRIVATE_IMAGES);
                }

            }
            Log.i("Path", path);

            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (filePathArray[filePathArray.length - 1].equals(FuguAppConstant.IMAGE)) {
                if (workspaceInfo.get(com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()).getMediaVisibility() == 0) {
                    File f = new File(path + "/.nomedia");
                    if (!f.exists()) {
                        f.createNewFile();
                    }
                    Log.i("FilePath", path);
                }
            }

            File compressedImage = null;

            if (fuguFileDetails.getFileExtension().toLowerCase().equals("png")) {
                fuguFileDetails.setFileExtension("jpg");
            }

            if (type.directory.equals(FuguAppConstant.IMAGE)) {
                try {
                    if (fuguFileDetails.getFileExtension().equals("png")) {
                        compressedImage = new Compressor(activity)
                                .setMaxWidth(1200)
                                .setMaxHeight(1200)
                                .setQuality(30)
                                .setCompressFormat(Bitmap.CompressFormat.PNG)
                                .setDestinationDirectoryPath(path)
                                .compressToFile(new File(getRealPathFromURI(uri)), fileName + "." + fuguFileDetails.getFileExtension());
                    } else {
                        compressedImage = new Compressor(activity)
                                .setMaxWidth(1200)
                                .setMaxHeight(1200)
                                .setQuality(30)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(path)
                                .compressToFile(new File(getRealPathFromURI(uri)), fileName + "." + fuguFileDetails.getFileExtension());
                    }
                } catch (Exception e) {
                    compressedImage = file;
                    InputStream inputStream = activity.getContentResolver().openInputStream(uri);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];

                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1)
                        fileOutputStream.write(buffer, 0, bytesRead);

                    fileOutputStream.close();

                    inputStream.close();
                }
                fuguFileDetails.setFileSize(readableFileSize(compressedImage.length()));
                fuguFileDetails.setFileSizeDouble(readableFileSizeDouble(compressedImage.length()));
                fuguFileDetails.setFilePath(compressedImage.getAbsolutePath());
                fuguFileDetails.setMuid(muid);
                FuguLog.e("file path", compressedImage.getAbsolutePath());
            } else {
                Log.e("File uri", uri.toString());
                InputStream inputStream = activity.getContentResolver().openInputStream(uri);

                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];

                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1)
                    fileOutputStream.write(buffer, 0, bytesRead);

                fileOutputStream.close();

                inputStream.close();
                fuguFileDetails.setFileSize(readableFileSize(file.length()));
                fuguFileDetails.setFileSizeDouble(readableFileSizeDouble(file.length()));
                fuguFileDetails.setFilePath(file.getAbsolutePath());
                fuguFileDetails.setMuid(muid);
                FuguLog.e("file path", file.getAbsolutePath());
            }

//            ExifInterface exif = new ExifInterface(compressedImage.getAbsolutePath());
//            exif.setAttribute(ExifInterface.TAG_MAKE, channelId.toString());
//            exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
//            exif.saveAttributes();
            return fuguFileDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName).getAbsolutePath();

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return (new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups]).replace(",", ".");
    }

    public String readableFileSizeDouble(long size) {
        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return (new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))).replace(",", ".");
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == fuguAnimSlideDown) {
            dialog.dismiss();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


}
