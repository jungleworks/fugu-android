package com.easyfilepicker.filter.callback;


import com.easyfilepicker.filter.entity.BaseFile;
import com.easyfilepicker.filter.entity.Directory;

import java.util.List;


public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
