# retrofit2

![retrofit2](art/retrofit2.png)

Retrofit2 turns your REST API into a Java interface.

Inspired by retrofit, compile-time version.

```java
@Retrofit("https://api.github.com")
public abstract class GitHubService {
  @GET("/users/{user}/repos")
  List<Repo> listRepos(@Path("user") String user);
  
  public static GitHubService create() {
    Gson gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .registerTypeAdapter(Date.class, new DateTypeAdapter())
      .create();
    return new Retrofit_GitHubService(new GsonConverter(gson));
  }
}
```

```java
GitHubService github = GitHubService.create();
```

Each call on the generated GitHubService makes an HTTP request to the remote webserver.

```java
List<Repo> repos = github.listRepos("octocat");
```

Use annotations to describe the HTTP request:

* URL parameter replacement and query parameter support
* Object conversion to request body (e.g., JSON, protocol buffers)
* Multipart request body and file upload