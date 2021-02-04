package com.blend.architecture.retrofit;


import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class MyGsonConvertFactory extends Converter.Factory {

    public static MyGsonConvertFactory create() {
        return new MyGsonConvertFactory(new Gson());
    }

    private Gson mGson;

    public MyGsonConvertFactory(Gson gson) {
        mGson = gson;
    }

    /**
     * 将API方法的输入参数类型从 type转换为requestBody
     */
    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
        return new MyGsonRequestBodyConverter(mGson, adapter);
    }

    /**
     * 判断能否将API方法的返回类型从ResponseBody 转换为type
     * 如果不能直接返回null，反之返回对应的Converter.Factory对象
     * type是由CallAdapter 接口里面的responseType()函数返回的。
     */
    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
        return new MyGsonResponseBodyConverter(mGson, adapter);
    }

    private class MyGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private Gson mGson;
        private TypeAdapter<T> mTypeAdapter;
        private MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
        private Charset UTF_8 = Charset.forName("UTF-8");

        public MyGsonRequestBodyConverter(Gson gson, TypeAdapter<T> typeAdapter) {
            mGson = gson;
            mTypeAdapter = typeAdapter;
        }

        @Nullable
        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = mGson.newJsonWriter(writer);
            mTypeAdapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    private class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        private Gson mGson;
        private TypeAdapter<T> mTypeAdapter;

        public MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> typeAdapter) {
            mGson = gson;
            mTypeAdapter = typeAdapter;
        }

        @Nullable
        @Override
        public T convert(ResponseBody value) throws IOException {
            JsonReader jsonReader = mGson.newJsonReader(value.charStream());
            T result;
            try {
                result = mTypeAdapter.read(jsonReader);
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw new JsonIOException("JSON document was not fully consumed.");
                }
            } finally {
                value.close();
            }
            return result;
        }
    }
}
