rocket
======

Rocket is an android lightweight framework with rest APIs and image cache features.Which based on google volley library.
For better convenience, we wrapped the volley and do some extra updates. And we add the cool features like : rest api, 
https, supporting from 2.2 and above sdks for image cache(You needn't to worry about the OOM issue below 3.0 sdks). 


1)Rest apis:
Rocket.with(this)
.setJsonClass(MapInfos.class)
.setCallback(new FutureCallback<MapInfos>() {

	@Override
	public void onCompleted(RocketError error, MapInfos result) {
		System.out.println("=========result="+result);
				
	}
}).load("http://XXX");
		
		
2)Image downloader:











3)Image cache:
