package com.skeleton.mvp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.skeleton.mvp.BuildConfig
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.HomeSearchActivity
import com.skeleton.mvp.adapter.GroupSuggestion
import com.skeleton.mvp.adapter.SearchMessageAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.network.ApiError
import com.skeleton.mvp.data.network.CommonParams
import com.skeleton.mvp.data.network.ResponseResolver
import com.skeleton.mvp.data.network.RestClient
import com.skeleton.mvp.model.SearchMessage
import com.skeleton.mvp.model.searchedMessages.SearchMessagesResponse
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.utils.EndlessScrolling

/**
 * Created
 * rajatdhamija on 03/07/18.
 */
class MessagesSearchFragment : Fragment(), HomeSearchActivity.UpdateMessagesData {

    lateinit var rvSearchMessage: androidx.recyclerview.widget.RecyclerView
    lateinit var searchMessageAdapter: SearchMessageAdapter
    private var endlessScrolling: EndlessScrolling? = null
    var messageList = ArrayList<SearchMessage>()
    lateinit var llNewSearch: LinearLayout
    lateinit var layoutManager: androidx.recyclerview.widget.LinearLayoutManager
    var pageStart: Int = 1
    var pageSize: Int = 20
    var searchedMessageText: String = ""
    var searchChannelId = -1L
    lateinit var pbLoading: ProgressBar
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages_search, container, false)
        rvSearchMessage = view.findViewById(R.id.rvSearchMessages)
        llNewSearch = view.findViewById(R.id.llNewSearch)
        pbLoading = view.findViewById(R.id.pbLoading);
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvSearchMessage.layoutManager = layoutManager
        searchMessageAdapter = SearchMessageAdapter(messageList, activity, "", true, false)
        rvSearchMessage.adapter = searchMessageAdapter
        scrollingListener()
        return view
    }

    override fun updateList(messageList: ArrayList<SearchMessage>, searchMessage: String, channelId: Long?) {
        this.messageList = messageList
        searchedMessageText = searchMessage
        if (channelId != null) {
            searchChannelId = channelId
        }
        searchMessageAdapter.updateList(messageList, searchMessage)
        searchMessageAdapter.notifyDataSetChanged()
        if (messageList.size == 0) {
            rvSearchMessage.visibility = View.GONE
            if (!TextUtils.isEmpty(searchMessage)) {
                llNewSearch.visibility = View.VISIBLE
            } else {
                llNewSearch.visibility = View.GONE
            }
        } else {
            rvSearchMessage.visibility = View.VISIBLE
            llNewSearch.visibility = View.GONE
        }
        scrollingListener()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {

        }
    }

    private fun scrollingListener() {
        if (endlessScrolling == null) {
            endlessScrolling = object : EndlessScrolling(layoutManager) {
                override fun onLoadMore(currentPages: Int) {
                    if (messageList.size >= pageSize - 2) {
                        pageStart += pageSize
                        pbLoading.visibility = View.VISIBLE
                        apiSearchMessage()
                    }
                }

                override fun onHide() {

                }

                override fun onShow() {

                }
            }
            rvSearchMessage.addOnScrollListener(endlessScrolling!!)
        }
    }

    private fun apiSearchMessage() {
        val commonParams = CommonParams.Builder()
        commonParams.add(FuguAppConstant.EN_USER_ID, com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].enUserId)
        commonParams.add(AppConstants.SEARCH_TEXT, searchedMessageText)
        commonParams.add(FuguAppConstant.PAGE_START, pageStart)
        if (searchChannelId.compareTo(-1L) != 0) {
            commonParams.add(FuguAppConstant.CHANNEL_ID, searchChannelId)
        }
        RestClient.getApiInterface(false).getSearchMessages(com.skeleton.mvp.data.db.CommonData.getCommonResponse().getData().getUserInfo().getAccessToken(), CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition()).getFuguSecretKey(), 1, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(object : ResponseResolver<SearchMessagesResponse>() {
                    override fun onSuccess(searchMessagesResponse: SearchMessagesResponse?) {
                        pbLoading.visibility = View.GONE
                        pageSize = searchMessagesResponse?.data?.pageSize!!
                        val searchableMessages = searchMessagesResponse?.data?.searchableMessages
                        val threadMessages = searchMessagesResponse?.data?.threadMessages
                        var channelImage = ""
                        for (i in searchMessagesResponse?.data?.searchableMessages!!.indices) {
                            if (searchableMessages?.get(i)?.channelImage?.replace("\\", "")?.split("\"")!!.size > 2) {
                                channelImage = searchableMessages.get(i)?.channelImage?.replace("\\", "")?.split("\"")!![3]
                            } else {
                                channelImage = ""
                            }
                            var name = ""
                            if (!TextUtils.isEmpty(searchableMessages.get(i)?.label)) {
                                name = searchableMessages.get(i)?.label!!
                            } else if (!TextUtils.isEmpty(searchableMessages.get(i)?.fullName)) {
                                name = searchableMessages.get(i)?.fullName!!
                            }

                            messageList.add(SearchMessage(name,
                                    searchableMessages.get(i)?.searchableMessage,
                                    searchableMessages.get(i)?.dateTime,
                                    searchableMessages.get(i)?.fullName,
                                    false,
                                    searchableMessages.get(i)?.chatType!!,
                                    searchableMessages.get(i)?.userId,
                                    searchableMessages.get(i)?.messageIndex!!,
                                    "",
                                    searchableMessages.get(i)?.muid,
                                    searchableMessages.get(i).channelId,
                                    searchableMessages.get(i).notification,
                                    searchableMessages[i].messageType,
                                    searchableMessages[i].fileName,
                                    searchableMessages[i].fileSize,
                                    "", searchableMessages[i].imageurl))
                        }
                        for (i in searchMessagesResponse.data?.threadMessages!!.indices) {
                            var name = ""
                            if (!TextUtils.isEmpty(threadMessages?.get(i)?.label)) {
                                name = threadMessages?.get(i)?.label!!
                            } else if (!TextUtils.isEmpty(threadMessages?.get(i)?.fullName)) {
                                name = threadMessages?.get(i)?.fullName!!
                            }
                            if (threadMessages?.get(i)?.channelImage?.replace("\\", "")?.split("\"")!!.size > 2) {
                                channelImage = threadMessages.get(i)?.channelImage?.replace("\\", "")?.split("\"")!![3]
                            } else {
                                channelImage = ""
                            }
                            messageList.add(SearchMessage(name,
                                    threadMessages.get(i)?.searchableMessage,
                                    threadMessages.get(i)?.dateTime,
                                    threadMessages.get(i)?.fullName,
                                    true,
                                    threadMessages.get(i)?.chatType!!,
                                    threadMessages.get(i)?.threadUserId,
                                    0,
                                    "",
                                    threadMessages.get(i)?.muid,
                                    threadMessages.get(i).channelId,
                                    "UNMUTED",
                                    threadMessages[i].messageType,
                                    threadMessages[i].fileName,
                                    threadMessages[i].fileSize,
                                    "", threadMessages[i].imageurl))
                        }

                        searchMessageAdapter.updateList(messageList, searchedMessageText)
                        searchMessageAdapter.notifyDataSetChanged()
                    }

                    override fun onError(error: ApiError?) {
                    }

                    override fun onFailure(throwable: Throwable?) {
                    }

                })
    }

    public interface SearchMessages {
        fun searchMessage(pageSize: Int)
    }
}