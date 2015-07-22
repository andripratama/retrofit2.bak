/*
 * Copyright (C) 2015 8tory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.retrofit2;

import retrofit2.Retrofit;
import retrofit2.Retrofit.*;

import rx.Observable;
import java.io.File;

import retrofit.converter.*;
import java.util.List;
import rx.functions.*;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import com.github.mobile.model.*;

@Retrofit("https://api.github.com")
@Headers({
    "Accept: application/vnd.github.v3.full+json",
    "User-Agent: Retrofit2"
})
@RetryHeaders(
    value = "Cache-Control: max-age=640000",
    exceptions = retrofit2.RequestException.class
)
public abstract class GitHub {
    @GET("/repos/{owner}/{repo}/contributors")
    public abstract Observable<List<Contributor>> contributorList(
            @Path("owner") String owner,
            @Path("repo") String repo);

    public Observable<Contributor> contributors(
            String owner,
            String repo) {
        return contributorList(owner, repo).flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
            @Override public Observable<Contributor> call(List<Contributor> list) {
                return Observable.from(list);
            }
        });
    }

    @GET("https://api.github.com/repos/{owner}/{repo}/contributors")
    public abstract Observable<List<Contributor>> contributorListWithoutBaseUrl(
            @Path("owner") String owner,
            @Path("repo") String repo);

    public Observable<Contributor> contributorsWithoutBaseUrl(
            String owner,
            String repo) {
        return contributorListWithoutBaseUrl(owner, repo).flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
            @Override public Observable<Contributor> call(List<Contributor> list) {
                return Observable.from(list);
            }
        });
    }

    @GET("{url}")
    public abstract Observable<List<Contributor>> contributorListDynamic(@Path("url") String url);

    public Observable<Contributor> contributorsDynamic(String url) {
        return contributorListDynamic(url).flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
            @Override public Observable<Contributor> call(List<Contributor> list) {
                return Observable.from(list);
            }
        });
    }

    @POST("/user/edit")
    public abstract Observable<Contributor> updateUser(@Body Contributor user);

    @FormUrlEncoded
    @POST("/user/edit")
    public abstract Observable<Contributor> updateUser(@Field("first_name") String first, @Field("last_name") String last);

    @Multipart
    @PUT("/user/photo")
    public abstract Observable<Contributor> updateUser(@Part(value = "photo", mimeType = "image/png") File photo, @Part("description") String description);
    @Multipart
    @PUT("/user/photo")
    public abstract Observable<Contributor> updateUserWithTypedFile(@Part("photo") TypedFile photo, @Part("description") String description);
    @Multipart
    @PUT("/user/photo")
    public abstract Observable<Contributor> updateUserWithTypedStringAnootation(@Part("photo") TypedFile photo, @Part(value = "description", mimeType = "application/json") String description);
    @Multipart
    @PUT("/user/photo")
    public abstract Observable<Contributor> updateUserWithTypedString(@Part("photo") TypedFile photo, @Part("description") TypedString description);

    @Headers("Cache-Control: max-age=640000")
    @GET("/widget/list")
    public abstract Observable<Contributor> widgetList();

    @Headers({
        "Accept: application/vnd.github.v3.full+json",
        "User-Agent: Retrofit2"
    })
    @GET("/users/{username}")
    public abstract Observable<Contributor> getUser(@Path("username") String username);

    @Headers({
        "Accept: application/vnd.github.v3.full+json",
        "User-Agent: Retrofit2"
    })
    @GET("/user")
    public abstract Observable<Contributor> getUserWithAuthorization(@Header("Authorization") String authorization);

    @Headers({
        "Accept: application/vnd.github.v3.full+json",
        "User-Agent: Retrofit2"
    })
    @PUT("/user/starred/{owner}/{repo}")
    public abstract Observable<Contributor> star(@Query("access_token") String accessToken,
        @Path("owner") String owner,
        @Path("repo") String repo);

    @Headers({
        "Accept: application/vnd.github.v3.full+json",
        "User-Agent: Retrofit2"
    })
    @DELETE("/user/starred/{owner}/{repo}")
    public abstract Observable<Contributor> unstar(@Query("access_token") String accessToken,
        @Path("owner") String owner,
        @Path("repo") String repo);

    @DELETE("/repos/{owner}/{repo}")
    public abstract Observable<Contributor> deleteRepository(
            @Header("Authorization") String basicCredentials,
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("/authorizations")
    public abstract Observable<Authorization> getAuthorizations(@Header("Authorization") String token);

    @POST("/authorizations")
    public abstract Observable<Authorization> createDeleteAuthorization(@Header("Authorization") String basicCredentials,
            @Body Authorization authorization);

    @GET("/users/{username}/repos")
    @QueryBinding(value = "access_token", binder = AuthorizationBinder.class)
    public abstract Observable<Repository> repositories(@Header("username") String username);

    //@GET("/users/repos") // TODO intercepter // TODO header-binding
    //public abstract Observable<Repository> repositories();

    public static class AuthorizationBinder implements Bindable<Authorization> {
        @Override public String call(Authorization authorization) {
            return authorization.getToken();
        }
    }

    public static GitHub create() {
        return new Retrofit_GitHub();
    }

    public static GitHub create(Converter converter) {
        //if (converter == null) {
        //    /*
        //    Gson gson = new GsonBuilder()
        //        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        //        //.registerTypeAdapter(Date.class, new DateTypeAdapter())
        //        .create();
        //    converter = new GsonConverter(gson);
        //    */
        //    converter = new JacksonConverter();
        //}
        return new Retrofit_GitHub(converter);
    }
}