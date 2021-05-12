# CoroutineCachePro


CoroutineCachePro is a Kotlin library, which provides features to take full advantage of HttpCache.


If you are already using Coroutine for network calls, you can optimize your API calls and also enable offline experience with a very few changes


### How to include in your project

###### Add the dependency to your `build.gradle`:
```groovy
implementation 'com.pv-libs.CachePro:CoroutineCachePro:0.1.0'
```

Setup
---
#### 1. Initializing ``OkHttpClient``
 - Create an instance of ``CachePro``
 - attach the cachePro instance to ``OkHttpClient.Builder`` as shown
```kotlin
val cachePro = CachePro.Builder(context)
    .setEnableOffline(true)    // default true
    .setForceCache(true)       // default true
    .build()

val okHttpClient = OkHttpClient.Builder()
    .cache(cache)               // need to add cache to work as expected
    .attachCachePro(cachePro)   // attaching cachePro to OkHttpClient
    .build()
```

#### 2. Initializing ``Retorfit``
 - Set the above created ``okHttpClient`` as client.
 - ``CoroutineCacheProCallAdapter.Factory`` should be the first CallAdapterFactory added to retrofit
```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://reqres.in")
    .client(okHttpClient)
    .addCallAdapterFactory(CoroutineCacheProCallAdapter.Factory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

## Features

#### 1) Flow from retrofit 

Modify return type of your API call to ``Flow<Response<NetworkResponse>>``

which provides the cache response immediately and in background validates the cache response, and notifies if the current cache response is invalid.  
```kotlin
@GET("/api/users")
fun getUsersListFlow(): Flow<Response<GetUsersResponse>>
```

#### 2) CoroutineApiCaller
Modify return type of your API call to ``CoroutineApiCaller<NetworkResponse>``

**CoroutineApiCaller** provides a ``Flow`` and a function ``fetchFromServer()`` which enables you to implement features like swipe to refresh much more efficiently.

By default ``Flow`` from CoroutineApiCaller functions exactly like the above 'flow' but it allows for multiple triggers of network calls, which happen in background and you only get notified if there is any change in response.
  
```kotlin
@GET("/api/users")
fun getUsersApiCaller(): CoroutineApiCaller<GetUsersResponse>
```

```kotlin
class UsersViewModel : ViewModel{

    private val usersApiCaller: CoroutineApiCaller<GetUsersResponse> = dataManager.getUsersApiCaller()

    init{
        // should only be called once
        usersApiCaller.getResponseFlow().collect {
            when (it) {
                is ApiResult.Success -> {
                    onResponse(it.data)
                }
                is ApiResult.Error -> {
                    showToast(it.exception.localizedMessage)
                }
            }
        }
    }

    fun refreshData(){
        // triggers a new network request and notifies through above Flow if there is any change in api response
        usersListApiCaller.fetchFromServer()
    }

    // CoroutineApiCaller also provides a LiveData which informs if there is any network request currently running in background.
    val inApiRunningLiveData = usersListApiCaller.isApiInProgressLiveData

}
```
Checkout the given sample 'SampleActivity'

#### 2) Instant Offline Support
If application makes a ``GET`` request when device is not connected to any network, CoroutineCachePro will make retrofit check if there is any data in ``CACHE`` and return it.  


#### 3) Ability to Force Cache
CoroutineCachePro provides ability to force every ``GET`` request to use **Cache**, if your api responses doesn't contain **Cache-Control**.

This applies to every ``GET`` API request, to disable cache for specific API use ``@ApiNoCache`` 
```kotlin
@ApiNoCache
@GET("/api/users")
suspend fun getUsersList(): Response<GetUsersResponse>
```




