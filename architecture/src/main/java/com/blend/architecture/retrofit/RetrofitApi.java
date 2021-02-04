package com.blend.architecture.retrofit;



import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitApi {
    //GET注解
    @GET("wxarticle/chapters/json")
    Call<RetrofitBean<List<RetrofitBean.DataBean>>> getOfficialAccounts();

    @GET("wxarticle/chapters/json")
    LiveData<RetrofitBean<List<RetrofitBean.DataBean>>> getLiveDataOfficialAccounts();

    @GET("wxarticle/chapters/json")
    Observable<RetrofitBean<List<RetrofitBean.DataBean>>> getOfficialAccountsObservable();

    //HTTP注解
    @HTTP(method = "get", path = "wxarticle/chapters/json", hasBody = false)
    Call<RetrofitBean<List<RetrofitBean.DataBean>>> getProject2();

    ///////上传单张图片//////

    /**
     * Multipart：表示请求实体是一个支持文件上传的Form表单，需要配合使用@Part,适用于有文件上传的场景
     * Part:用于表单字段,Part和PartMap与Multipart注解结合使用,适合文件上传的情况
     * PartMap:用于表单字段,默认接受的类型是Map<String,REquestBody>，可用于实现多文件上传
     * Part 后面支持三种类型，{@link okhttp3.RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型；
     *
     * @param file 服务器指定的上传图片的key值
     * @return
     */

    //post上传图片
    @Multipart
    @POST("project/upload")
    Call<RetrofitBean<Boolean>> upload1(@Part("file" + "\";filename=\"" + "test.png") RequestBody file);

    /**
     * Part 后面支持三种类型，{@link RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型；
     *
     * @param userName
     * @param passWord
     * @param file
     * @return
     */
    @Multipart
    @POST("project/xxx")
    Call<RetrofitBean<Boolean>> upload2(@Part("username") RequestBody userName,
                                        @Part("password") RequestBody passWord,
                                        @Part MultipartBody.Part file);

    //post请求登录
    @POST("user/login")
    @FormUrlEncoded
    Call<RetrofitBean<Boolean>> login(@Field("username") String username, @Field("password") String password);
}
