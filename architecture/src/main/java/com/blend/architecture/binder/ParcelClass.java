package com.blend.architecture.binder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcel内部包装了可序列化的数据
 * <p>
 * 与Serializable的区别：Serializable是java的序列化接口，使用起来简单但是开销大，序列化和反序列化过程需要大量I/O操作，
 * Parcelable：适用于Android平台，缺点是使用起来有点麻烦，但是效率高。
 * 在内存序列化上首选Parcelable，因为效率高；在将对象序列化到存储设备上或者将对象序列化后通过网络传输有点负责，推荐使用
 * Serializable。
 */
public class ParcelClass implements Parcelable {

    public int userId;
    public String userName;

    //从序列化后的对象中创建原始对象，通过一系列的read方法来完成
    protected ParcelClass(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
    }

    //反序列化
    public static final Creator<ParcelClass> CREATOR = new Creator<ParcelClass>() {

        //从序列化后的对象中创建原始对象
        @Override
        public ParcelClass createFromParcel(Parcel in) {
            return new ParcelClass(in);
        }

        //创建指定长度的原始对象数组
        @Override
        public ParcelClass[] newArray(int size) {
            return new ParcelClass[size];
        }
    };

    //内容描述功能，默认返回0
    @Override
    public int describeContents() {
        return 0;
    }

    //将当前对象写入序列化结构中，通过一系列的write方法来完成
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
    }
}
