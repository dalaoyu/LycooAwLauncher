package com.lycoo.commons.http;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * 下载
 * 每个具体的下载任务应该继承
 *
 * Created by lancy on 2019/6/5
 */
@Data
public class Downloads implements Parcelable {
    /**
     * 未知状态
     * 也就是除STATUS_WAITING， STATUS_DOWNLOADING， STATUS_ERROR， STATUS_SUCCESS之外的状态
     */
    public static final int STATUS_PENDING = 100;

    /**
     * 等待中...
     * 在“等待队列”中， 存在DownloadManager的生命周期
     */
    public static final int STATUS_WAITING = 101;

    /**
     * 下载中...
     * 在“下载队列”中， 存在DownloadManager的生命周期
     */
    public static final int STATUS_DOWNLOADING = 102;

    /**
     * 下载出错
     * 具体下载任务本身维护， 通常持久化至数据库
     */
    public static final int STATUS_ERROR = 103;

    /**
     * 下载成功
     * 具体下载任务本身维护， 通常持久化至数据库
     */
    public static final int STATUS_SUCCESS = 104;

    /**
     * 文件总大小
     * 字节为单位
     */
    protected Long total;

    /**
     * 文件下载进度
     */
    protected Integer progress;

    /**
     * 文件状态
     * ERROR, SUCCESS
     */
    protected Integer status;

    public Downloads() {
    }

    protected Downloads(Parcel in) {
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readLong();
        }
        if (in.readByte() == 0) {
            progress = null;
        } else {
            progress = in.readInt();
        }
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
    }

    public static final Creator<Downloads> CREATOR = new Creator<Downloads>() {
        @Override
        public Downloads createFromParcel(Parcel in) {
            return new Downloads(in);
        }

        @Override
        public Downloads[] newArray(int size) {
            return new Downloads[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(total);
        }
        if (progress == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(progress);
        }
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
    }
}
