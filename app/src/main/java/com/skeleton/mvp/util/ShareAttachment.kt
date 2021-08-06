package com.skeleton.mvp.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.easyfilepicker.filter.entity.ImageFile
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.MultipleImageDisplayActivity
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.fugudatabase.CommonData
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.utils.DateUtils
import com.skeleton.mvp.utils.FuguImageUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ShareAttachment {
    fun sentSharedAttachment(context: Context, channelId: Long,
                             fuguImageUtils: FuguImageUtils?,
                             etMessage: EmojiGifEditText,
                             maxUploadSize: Long,
                             shareAttachmentCallBack: ShareAttachmentCallBack) {
        if (!TextUtils.isEmpty(CommonData.getImageUri())) {
            val globalUuid = UUID.randomUUID().toString()
            var uri = CommonData.getImageUriMain()
            try {
                uri.toString().equals("null")
            } catch (e: Exception) {
                uri = getUriFromPath(CommonData.getImageUri(), context)
            }
            val dimens = getImageHeightAndWidth(uri, context)
            var extension = uri.toString().split(".")[uri.toString().split(".").size - 1].toLowerCase()
            var type: FuguAppConstant.FileType? = FuguAppConstant.FILE_TYPE_MAP[extension]
            if (type == null) {
                type = FuguAppConstant.FileType.IMAGE_FILE
                extension = "jpg"
            }
            val localDate = DateUtils.getFormattedDate(Date())
            var fileDetails: FuguFileDetails? = null
            try {
                fileDetails = fuguImageUtils?.saveFile(uri, type, channelId, localDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (extension.toLowerCase().equals("png")) {
                extension = "jpg"
            }


            if (fileDetails != null) {
                (context as ChatActivity).runOnUiThread {
                    try {
                        if (extension.toLowerCase().equals("gif") || fileDetails?.fileExtension.equals("gif")) {
                            context.showImageDialog(uri, uri, context, uri.toString(), dimens, fileDetails, fileDetails!!.filePath)
                        } else {
                            context.showImageWithMessageDialog(extension, dimens, uri, fileDetails)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
            CommonData.deleteImageUri()
            CommonData.setSharedText("")
            CommonData.setVideoUri("")
            CommonData.setOtherFilesUriString("")
            CommonData.setMultipleImage("")
            shareAttachmentCallBack.onFileShared()
        } else if (!TextUtils.isEmpty(CommonData.getSharedText())) {
            (context as ChatActivity).runOnUiThread {
                shareAttachmentCallBack.onTextShared(CommonData.getSharedText())
            }
            CommonData.setImageUri("")
            CommonData.setVideoUri("")
            CommonData.setOtherFilesUriString("")
            CommonData.setMultipleImage("")
            shareAttachmentCallBack.onFileShared()

        } else if (!TextUtils.isEmpty(CommonData.getOtherFilesUriString())) {
            val localDate = DateUtils.getFormattedDate(Date())
            var extension = ""
            val extensions = CommonData.getOtherFilesUriString().split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            extension = extensions[extensions.size - 1].toLowerCase()
            if (FuguAppConstant.FILE_TYPE_MAP[extension] == null) {
                extension = "default"
            }
            var fileDetails: FuguFileDetails? = null
            try {
                fileDetails = fuguImageUtils?.saveFile(getUriFromPath(CommonData.getOtherFilesUriString(), context), FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()], channelId, localDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (fileDetails != null) {
                if (fileDetails.filePath?.isEmpty()!!) {
                    Toast.makeText(context, "File not found...", Toast.LENGTH_LONG).show()
                } else {
                    if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains("Bytes") || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize) {
                        (context as ChatActivity).addMessageToList(context.getString(R.string.fugu_empty), FuguAppConstant.FILE_MESSAGE, fileDetails.filePath, "", fileDetails, UUID.randomUUID().toString(), java.util.ArrayList(), null)
                    } else {
                        (context as ChatActivity).showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                    }
                }
            }
            CommonData.setSharedText("")
            CommonData.setImageUri("")
            CommonData.setVideoUri("")
            CommonData.setOtherFilesUriString("")
            CommonData.setMultipleImage("")
            shareAttachmentCallBack.onFileShared()
        } else if (!TextUtils.isEmpty(CommonData.getVideoUri())) {
            var videoUri = CommonData.getVideoUriMain()
            try {
                videoUri.toString().equals("null")
            } catch (e: Exception) {
                videoUri = getUriFromPath(CommonData.getVideoUri(), context)
            }
            val dimensGallery = getImageHeightAndWidth(videoUri, context)
            val localDate = DateUtils.getFormattedDate(Date())
            val date = DateUtils.getDate(localDate)
            var extension = videoUri.toString().split(".")[videoUri.toString().split(".").size - 1].toLowerCase()
            var type: FuguAppConstant.FileType? = FuguAppConstant.FILE_TYPE_MAP[extension]
            try {
                if (type == null) {
                    extension = getUriFromPath(CommonData.getVideoUri(), context).toString().split(".")[getUriFromPath(CommonData.getVideoUri(), context).toString().split(".").size - 1].toLowerCase()
                    type = FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()]
                }
            } catch (e: Exception) {

            }
            var fileDetails: FuguFileDetails? = null
            try {
                fileDetails = fuguImageUtils?.saveFile(videoUri, type, channelId, localDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (fileDetails != null) {
                if (fileDetails.filePath?.isEmpty()!!) {
                    Toast.makeText(context, "File not found...", Toast.LENGTH_LONG).show()
                } else {
                    if (fileDetails.fileSize.contains("KB") || fileDetails.fileSize.contains("Bytes")
                            || java.lang.Double.parseDouble(fileDetails.fileSizeDouble) * 1024 * 1024 <= maxUploadSize) {
                        if (FuguAppConstant.supportedFormats.contains(fileDetails.fileExtension.toLowerCase())) {
                            if (GeneralFunctions().getMimeType(fileDetails.filePath, context) == null) {
                                (context as ChatActivity).showErrorMessage("File Corrupted")
                            } else {
                                (context as ChatActivity).runOnUiThread {
                                    context.showVideoWithMessageDialog(extension, dimensGallery, videoUri, fileDetails)
                                }
                            }
                        } else {
                            (context as ChatActivity).showErrorMessage("Please send document file only.")
                        }
                    } else {
                        (context as ChatActivity).showErrorMessage("File size cannot be greater than " + (((maxUploadSize) / 1024) / 1024).toInt() + "MB.")
                    }
                }
            }
            CommonData.setSharedText("")
            CommonData.setImageUri("")
            CommonData.setVideoUri("")
            CommonData.setOtherFilesUriString("")
            CommonData.setMultipleImage("")
            shareAttachmentCallBack.onFileShared()
        } else if (!TextUtils.isEmpty(com.skeleton.mvp.fugudatabase.CommonData.getMultipleImage())) {
            try {
                val multipleImageList: ArrayList<String> = com.skeleton.mvp.fugudatabase.CommonData.getMultipleImageList()
                var list = ArrayList<ImageFile>()
                for (i in 0 until multipleImageList.size) {
                    list.add(ImageFile())
                    list.get(i).path = multipleImageList.get(i)
                }
                var extension = ""
                val cursor = (context as ChatActivity).contentResolver.query(getUriFromPath(list.get(0).path, context), null, null, null, null)
                cursor.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        try {
                            val extensions = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            extension = extensions[extensions.size - 1].toLowerCase()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                if (extension.toLowerCase().equals("png")) {
                    extension = "jpg"
                    val bitmap = BitmapFactory.decodeFile(list.get(0).path)
                    val inputStream = context.contentResolver.openInputStream(FileProvider.getUriForFile(context, "com.fuguchat.provider", File(list.get(0).path)))!!
                    val fileOutputStream = FileOutputStream(File(list.get(0).path))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                    val buffer = ByteArray(1024)
                    val bytesRead = 0
                    var i = inputStream.read(buffer)
                    while (i > -1) {
                        i = inputStream.read(buffer)
                        fileOutputStream.write(buffer, 0, bytesRead)
                    }
                    fileOutputStream.close()
                }
                val dimensGallery = getImageHeightAndWidth(getUriFromPath(list.get(0).path, context), context)
                val localDate = DateUtils.getFormattedDate(Date())
                val fileDetails = fuguImageUtils?.saveFile(getUriFromPath(list.get(0).path, context), FuguAppConstant.FILE_TYPE_MAP[extension.toLowerCase()], channelId, localDate)
                if (list.size > 1) {
                    val intent = Intent(context, MultipleImageDisplayActivity::class.java)
                    intent.putExtra(FuguAppConstant.RESULT_PICK_IMAGE, list)
                    context.startActivityForResult(intent, FuguAppConstant.REQUEST_MULTIPLE_IMAGES)
                } else {
                    if (extension.equals("gif")) {
                        context.showImageDialog(getUriFromPath(list.get(0).path, context), getUriFromPath(list.get(0).path, context), context, getUriFromPath(list.get(0).path, context).toString(), dimensGallery, fileDetails, list.get(0).path)
                    } else {
                        context.showImageWithMessageDialog(extension, dimensGallery, getUriFromPath(list.get(0).path, context), fileDetails)
                    }
                }

            } catch (e: java.lang.Exception) {
                (context as ChatActivity).showErrorMessage("Something went wrong!")
            }
            CommonData.setSharedText("")
            CommonData.setImageUri("")
            CommonData.setVideoUri("")
            CommonData.setOtherFilesUriString("")
            CommonData.setMultipleImage("")
            shareAttachmentCallBack.onFileShared()
        }
    }

    fun getUriFromPath(path: String, context: Context): Uri {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", File(path))
    }

    private fun getImageHeightAndWidth(uri: Uri?, context: Context): java.util.ArrayList<Int> {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(GeneralFunctions().getRealPathFromURI(uri, context)).absolutePath, options)
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            val dimens = java.util.ArrayList<Int>()
            val exif = ExifInterface(File(GeneralFunctions().getRealPathFromURI(uri, context)).absolutePath)
            val rotation: Int = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
            if (rotation == 6 || rotation == 8) {
                dimens.add(imageWidth)
                dimens.add(imageHeight)
            } else {
                dimens.add(imageHeight)
                dimens.add(imageWidth)
            }
            dimens
        } catch (e: Exception) {
            java.util.ArrayList()
        }
    }

    interface ShareAttachmentCallBack {
        fun onFileShared()
        fun onTextShared(text: String)
    }
}