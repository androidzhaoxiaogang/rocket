# rocket
======

Rocket is an android lightweight framework with rest APIs and image cache features.Which based on google volley library.
For better convenience, we wrapped the volley and do some extra updates. And we add the cool features like : rest api, 
https, supporting from 2.2 and above sdks for image cache(You needn't to worry about the OOM issue below 3.0 sdks). 

## Features:

### rest apis
### image downloader
### image cache(LRU)
### https
### cokkie
### cache strategy

## Rest apis:

``` java
Rocket.with(getActivity())
.targetType(IntrosInfo.class)
.invoke(new JsonCallback<IntrosInfo>() {

	@Override
	public void onCompleted(RocketError error, IntrosInfo result) {
		handleResult(error, result);
	}
})
.load(uri);
```		
		
## Image downloader:

``` java
Rocket.with(mImageView)
.placeholder(R.drawable.bg_list_header)
.skipMemoryCache()
.invoke(new ImageCallback() {
					
	@Override
	public void onComplete(RocketError error, Bitmap result) {
		if(error == null) {
			hasFetched = true;
		}
	}
})
.load(requestUrl);
```

## Image cache:

``` java
Rocket.with(holder.image)
.placeholder(R.drawable.bg_row_icon)
.load(url);
```

## support 
android 2.2sdk and above

## Contributors

* [Zhao xiaogang](https://github.com/androidzhaoxiaogang) - <https://github.com/androidzhaoxiaogang>
* [Yang Hao](https://github.com/haozi89) - <https://github.com/haozi89>
