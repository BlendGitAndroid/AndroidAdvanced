package com.blend.architecture.retrofit;



import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitApi {
    @GET("wxarticle/chapters/json")
    Call<RetrofitBean> getOfficialAccounts();

    @GET("wxarticle/chapters/json")
    Observable<RetrofitBean> getOfficialAccountsObservable();
}
