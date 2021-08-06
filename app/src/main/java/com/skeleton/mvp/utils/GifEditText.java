package com.skeleton.mvp.utils;

/**
 * Created by rajatdhamija on 09/04/18.
 */

public class GifEditText  {
//    public GifEditText(Context context) {
//        super(context);
//    }
//
//    public GifEditText(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public GifEditText(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
//        final InputConnection ic = super.onCreateInputConnection(editorInfo);
//        EditorInfoCompat.setContentMimeTypes(editorInfo,
//                new String[]{"image/gif"});
//
//        final InputConnectionCompat.OnCommitContentListener callback =
//                new InputConnectionCompat.OnCommitContentListener() {
//                    @Override
//                    public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
//                                                   int flags, Bundle opts) {
//                        // read and display inputContentInfo asynchronously
//                        if (BuildCompat.isAtLeastNMR1() && (flags &
//                                InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
//                            try {
//                                inputContentInfo.requestPermission();
//                            } catch (Exception e) {
//                                return false; // return false if failed
//                            }
//                        }
//
//                        // read and display inputContentInfo asynchronously.
//                        // call inputContentInfo.releasePermission() as needed.
//
//                        return true;  // return true if succeeded
//                    }
//                };
//        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
//    }
}
