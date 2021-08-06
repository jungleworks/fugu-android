package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyfilepicker.filter.entity.ImageFile
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.MultipleImageDisplayAdapter
import com.skeleton.mvp.adapter.SmallImageDisplayAdapter
import com.skeleton.mvp.constant.FuguAppConstant

class MultipleImageDisplayActivity : AppCompatActivity(), SmallImageDisplayAdapter.OnItemClicked {

    private var rvImages: androidx.recyclerview.widget.RecyclerView? = null
    private var rvSmallImages: androidx.recyclerview.widget.RecyclerView? = null
    private var multipleImageDisplayAdapter: MultipleImageDisplayAdapter? = null
    private var smallImageDisplayAdapter: SmallImageDisplayAdapter? = null
    private var ivSend: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_image_display)
        val list: ArrayList<ImageFile> = intent!!.getParcelableArrayListExtra(FuguAppConstant.RESULT_PICK_IMAGE)!!
        rvImages = findViewById(R.id.rvImages)
        ivSend = findViewById(R.id.ivSend)
        rvSmallImages = findViewById(R.id.rvSmallImages)
        multipleImageDisplayAdapter = MultipleImageDisplayAdapter(list)
        smallImageDisplayAdapter = SmallImageDisplayAdapter(list, this@MultipleImageDisplayActivity)
        rvImages?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MultipleImageDisplayActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvSmallImages?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MultipleImageDisplayActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rvImages?.adapter = multipleImageDisplayAdapter
        rvSmallImages?.adapter = smallImageDisplayAdapter
        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvImages)

        rvImages?.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                smallImageDisplayAdapter?.updateCurrentSelectedItemPosition((recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
            }
        })

        ivSend?.setOnClickListener {
            val intent = Intent()
            intent.putExtra(FuguAppConstant.RESULT_PICK_IMAGE, list)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    override fun onItemClicked(position: Int) {
        rvImages?.smoothScrollToPosition(position)
    }
}
