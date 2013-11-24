package fast.rocket.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: haozi
 * Date: 13-11-24
 * Time: 下午7:49
 * To change this template use File | Settings | File Templates.
 */
public class CookieManager {
    private Map<String, HashMap<String, String>> mCookieStore;
    private HashMap<String, String> mCookie;
    private static CookieManager mCookieManagerInstance;

    private CookieManager() {
        mCookie = new HashMap<String, String>();
        mCookieStore = new HashMap<String, HashMap<String, String>>();
    }

    public synchronized static CookieManager getInstance() {
        if (null == mCookieManagerInstance) {
            mCookieManagerInstance = new CookieManager();
        }
        return mCookieManagerInstance;
    }

    public void buildCookie(String cookieName, String cookieValue) {
          buildCookie(cookieName, cookieValue, false);
    }

    public void buildCookie(String cookieName, String cookieValue, boolean rebuild) {
        if (rebuild) {
            clearCurrentCookie();
        }
        mCookie.put(cookieName, cookieValue);
    }

    public void commitCookie2Store(String path) {
        mCookieStore.put(path, mCookie);
    }

    public void clearCookieStore(String path) {
        mCookieStore.put(path, null);
    }

    public void clearCurrentCookie() {
        mCookie.clear();
    }

    public HashMap<String, String> getCookie(String path) {
        return mCookieStore.get(path);
    }

}
