package com.skeleton.mvp.model;

/**
 * Created by bhavya on 11/08/17.
 */

public class FuguFileDetails {

    private String fileName = "";
    private String fileExtension = "";
    private String filePath = "";
    private String fileSize = "";
    private String fileSizeDouble;
    private String muid;

    public Long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }

    private Long downloadId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        if (filePath == null) {
            filePath = "";
        }
        return filePath;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeDouble() {
        return fileSizeDouble;
    }

    public void setFileSizeDouble(String fileSizeDouble) {
        this.fileSizeDouble = fileSizeDouble;
    }
}
