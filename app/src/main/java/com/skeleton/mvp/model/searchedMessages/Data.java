
package com.skeleton.mvp.model.searchedMessages;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("searchable_messages")
    @Expose
    private List<SearchableMessage> searchableMessages = new ArrayList<>();
    @SerializedName("page_size")
    @Expose
    private Integer pageSize = 0;
    @SerializedName("thread_messages")
    @Expose
    private List<ThreadMessage> threadMessages = new ArrayList<>();

    public List<SearchableMessage> getSearchableMessages() {
        if (searchableMessages == null) {
            return new ArrayList<>();
        }
        return searchableMessages;
    }

    public void setSearchableMessages(List<SearchableMessage> searchableMessages) {

        this.searchableMessages = searchableMessages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<ThreadMessage> getThreadMessages() {
        if (threadMessages == null) {
            return new ArrayList<>();
        }
        return threadMessages;
    }

    public void setThreadMessages(List<ThreadMessage> threadMessages) {

        this.threadMessages = threadMessages;
    }

}
