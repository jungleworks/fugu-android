package com.easyfilepicker.filter.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageFile extends BaseFile implements Parcelable {
    private int orientation;   //0, 90, 180, 270
    private String muid;
    private String destinationPath;
    private String mimeType;

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeString(getBucketId());
        dest.writeString(getBucketName());
        dest.writeLong(getDate());
        dest.writeByte((byte) (isSelected() ? 1 : 0));
        dest.writeInt(orientation);
        dest.writeString(getMuid());
        dest.writeString(getDestinationPath());
        dest.writeString(getMimeType());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageFile> CREATOR = new Creator<ImageFile>() {
        @Override
        public ImageFile[] newArray(int size) {
            return new ImageFile[size];
        }

        @Override
        public ImageFile createFromParcel(Parcel in) {
            ImageFile file = new ImageFile();
            file.setId(in.readLong());
            file.setName(in.readString());
            file.setPath(in.readString());
            file.setSize(in.readLong());
            file.setBucketId(in.readString());
            file.setBucketName(in.readString());
            file.setDate(in.readLong());
            file.setSelected(in.readByte() != 0);
            file.setOrientation(in.readInt());
            file.setMuid(in.readString());
            file.setDestinationPath(in.readString());
            file.setMimeType(in.readString());
            return file;
        }
    };
}
