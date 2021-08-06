package com.skeleton.mvp.data.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Developer: Click Labs
 *
 * MultiParams builder helper
 */
public final class MultipartParams {

    private HashMap<String, RequestBody> map = new HashMap<>();

    /**
     * Constructor
     *
     * @param builder object of builder class of MultipartParams
     */
    private MultipartParams(final Builder builder) {
        this.map = builder.map;

    }

    /**
     * Gets map.
     *
     * @return the map
     */
    public HashMap<String, RequestBody> getMap() {
        return map;
    }


    /**
     * The type Builder.
     */
    public static class Builder {
        private HashMap<String, RequestBody> map = new HashMap<>();

        /**
         * Instantiates a new Builder.
         */
        public Builder() {
        }

        /**
         * Add value of the map
         *
         * @param key   the key
         * @param value the value
         * @return the builder
         */
        public Builder add(final String key, final Object value) {

            if (value == null || String.valueOf(value).isEmpty()) {
                return this;
            }
            map.put(key, RetrofitUtils.getRequestBodyFromString(String.valueOf(value)));
            return this;
        }

        /**
         * Add value to the map
         *
         * @param key          the key
         * @param value        the value
         * @param isAllowEmpty the is allow empty
         * @return the builder
         */
        public Builder add(final String key, final Object value, final boolean isAllowEmpty) {
            if (!isAllowEmpty && (value == null || String.valueOf(value).isEmpty())) {
                return this;
            }
            map.put(key, RetrofitUtils.getRequestBodyFromString(String.valueOf(value)));
            return this;
        }

        /**
         * Add file to map.
         *
         * @param key   the key
         * @param mFile the file to add
         * @return the builder
         */
        public Builder addFile(final String key, final File mFile) {
            if (mFile == null) {
                return this;
            }
            map.put(key + "\"; filename=\"" + mFile.getName(), RequestBody.create(MediaType.parse(RetrofitUtils.getMimeType(mFile)), mFile));
            return this;
        }

        /**
         * Add array of files to map
         *
         * @param key            the key
         * @param mFileArrayList the files array list
         * @return the builder
         */
        public Builder addArrayOfFiles(final String key, final ArrayList<File> mFileArrayList) {
            if (mFileArrayList == null || mFileArrayList.size() == 0) {
                return this;
            }

            for (int i = 0; i < mFileArrayList.size(); i++) {
                if (mFileArrayList.get(i) != null) {
                    map.put(key + "\"; filename=\"" + mFileArrayList.get(i).getName(),
                            RequestBody.create(MediaType.parse(RetrofitUtils.getMimeType(mFileArrayList.get(i))),
                                    mFileArrayList.get(i)));
                }
            }
            return this;
        }


        /**
         * Build multipart params.
         *
         * @return the multipart params
         */
        public MultipartParams build() {
            return new MultipartParams(this);
        }
    }
}
