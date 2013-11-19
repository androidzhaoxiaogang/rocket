package fast.rocket.cache;

import fast.rocket.cache.ImageLoader.ImageCache;
import fast.rocket.utils.RocketUtils;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        return cacheSize;
    }

    public BitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    public BitmapLruCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    //Handle the OOM issue before 3.0.
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue,
			Bitmap newValue) {
		if (RocketUtils.hasHoneycomb()) {
			super.entryRemoved(evicted, key, oldValue, newValue);
		} else {
			if ((evicted) && (oldValue != null) && (!oldValue.isRecycled())) {
				oldValue.recycle();
				oldValue = null;
			}
		}
	}
    
}