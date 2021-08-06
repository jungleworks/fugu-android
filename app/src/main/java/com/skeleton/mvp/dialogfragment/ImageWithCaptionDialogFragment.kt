package com.skeleton.mvp.dialogfragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import com.skeleton.mvp.R
import com.skeleton.mvp.model.FuguFileDetails
import com.skeleton.mvp.util.EmojiGifEditText
import java.io.File
import java.io.FileInputStream

class ImageWithCaptionDialogFragment : DialogFragment() {

    var imageWithCaption: ImageWithCaption? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.image_message_dialog, container)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val ivSend: ImageView = dialogView.findViewById(R.id.ivSend)
        val etMsg: EmojiGifEditText = dialogView.findViewById(R.id.etMsg)
        val mentionsRecyclerView: androidx.recyclerview.widget.RecyclerView = dialogView.findViewById(R.id.rv_mentions)
        val llMessageLayout: LinearLayoutCompat = dialogView.findViewById(R.id.llMessageLayout)
        val llRoot: LinearLayoutCompat = dialogView.findViewById(R.id.llRoot)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        val exif = ExifInterface(File(getRealPathFromURI(imageWithCaption?.uri)).absolutePath)
        val rotation: Int = exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!.toInt()
        var angle = 0f
        when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                angle = 90f
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                angle = 180f
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                angle = 270f
            }
        }
        val mat = Matrix()
        mat.postRotate(angle)


        val bmp = BitmapFactory.decodeStream(FileInputStream(File(getRealPathFromURI(imageWithCaption?.uri))), null, null)!!
        val correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
        val d = BitmapDrawable(resources, correctBmp)
        llRoot.background = d
        return dialogView
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    companion object {
        fun newInstance(arg: Int, imageWithCaption: ImageWithCaption): ImageWithCaptionDialogFragment {
            val frag = ImageWithCaptionDialogFragment()
            val args = Bundle()
            frag.arguments = args
            frag.setImageWithCaptionObject(imageWithCaption)
            return frag

        }
    }

    private fun setImageWithCaptionObject(imageWithCaption: ImageWithCaption) {
        this.imageWithCaption = imageWithCaption
    }

    /**
     * get real path from image uri
     */
    private fun getRealPathFromURI(contentURI: Uri?): String {
        val result: String
        val cursor = activity?.contentResolver?.query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI!!.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    data class ImageWithCaption(val extension: String, val dimens: ArrayList<Int>, val uri: Uri?, val fileDetails: FuguFileDetails?)

}