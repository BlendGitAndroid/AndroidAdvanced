package com.blend.architecture;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {
    //请求的对象  RequestBean 对应的json字符串
    private String data;
    //    请求对象的类型
    private int type;
    //    反序列化   A进程
    protected Request(Parcel in) {
        data = in.readString();
        type=in.readInt();
    }

    public String getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public Request(String data, int type) {
        this.data = data;
        this.type = type;
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    //  序列化
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeInt(type);

    }
}
