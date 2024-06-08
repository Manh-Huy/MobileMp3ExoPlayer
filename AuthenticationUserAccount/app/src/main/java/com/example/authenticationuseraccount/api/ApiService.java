package com.example.authenticationuseraccount.api;

import com.example.authenticationuseraccount.model.Genre;
import com.example.authenticationuseraccount.model.ListenHistory;

import com.example.authenticationuseraccount.model.SearchHistory;
import com.example.authenticationuseraccount.model.SearchResult;
import com.example.authenticationuseraccount.model.business.Album;
import com.example.authenticationuseraccount.model.business.Artist;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.model.homepagemodel.Banner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    Interceptor interceptor = chain -> {
        try {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            return chain.proceed(builder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    HttpLoggingInterceptor loggingIntercepter = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
            .readTimeout(7, TimeUnit.SECONDS)
            .connectTimeout(7, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            //.addInterceptor(interceptor)
            .addInterceptor(loggingIntercepter);

    ApiService apiService = new Retrofit.Builder()
            //.baseUrl("https://mobilebackendtestupload.onrender.com/")
            .baseUrl("https://mobilebackendtestupload-q7eh.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okBuilder.build())
            .build()
            .create(ApiService.class);

    @GET("users")
    Call<List<User>> callTestAPI();

    @GET("songs")
    Observable<List<Song>> getSongs();

    @POST("users")
    Call<User> addUser(@Body User user);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String userID);

    @GET("users")
    Observable<List<User>> getUsers();

    @GET("history/{id}")
    Observable<List<ListenHistory>> getUserListenHistory(@Path("id") String userID);

    @POST("listenHistory")
    Completable addUserListenHistory(@Body ListenHistory listenHistory);

    @GET("search/getAllName")
    Observable<List<String>> getNameAllInfoSong();

    @FormUrlEncoded
    @POST("searchHistory")
    Completable addSearchHistory(@Field("userID") String userID, @Field("content") String query);

    @GET("searchHistory/{id}")
    Call<SearchHistory> getSearchHistoryById(@Path("id") String userID);

    @GET("banners")
    Observable<List<Banner>> getBanners();

    @GET("search")
    Observable<List<SearchResult>> getSearchAllResult(@Query("query") String query);
    @GET("search")
    Observable<List<Song>> getSearchSongResult(@Query("query") String query, @Query("song") boolean song);
    @GET("search")
    Observable<List<Artist>> getSearchArtistResult(@Query("query") String query, @Query("artist") boolean artist);
    @GET("search")
    Observable<List<Album>> getSearchAlbumResult(@Query("query") String query, @Query("album") boolean album);

    @GET("listenHistory/getSongLove")
    Observable<List<Song>> getUserLoveSong(@Query("userID") String userID);

    @GET("songs/recent")
    Observable<List<Song>> getUserRecentSong(@Query("userID") String userID);

    @GET("songs/recommend")
    Observable<List<Song>> getUserRecommendSong(@Query("userID") String userID);

    @GET("songs/forgotten")
    Observable<List<Song>> getUserForgottenSong(@Query("userID") String userID);

    @GET("genres/getAllGenre")
    Observable<List<Genre>> getAllGenres();

}


