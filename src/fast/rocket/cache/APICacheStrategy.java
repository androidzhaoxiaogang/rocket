package fast.rocket.cache;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: haozi
 * Date: 13-11-25
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class APICacheStrategy {
    private long mCacheExpires; //ms
    private int mCacheType;
    public static final int CACHE_TYPE_DAYS_INTERVAL = 1001;
    public static final int CACHE_TYPE_MILLIS_INTERVAL = 1002;
    public static final int CACHE_TYPE_EXACTLY_TIME = 1003;

    private APICacheStrategy() {

    }

    public APICacheStrategy(int cacheType) {
        if (cacheType != CACHE_TYPE_DAYS_INTERVAL && cacheType != CACHE_TYPE_MILLIS_INTERVAL) {
            throw new IllegalArgumentException("Cache Type must be in (CACHE_TYPE_DAYS_INTERVAL(1001), CACHE_TYPE_MILLIS_INTERVAL(1002), CACHE_TYPE_EXACTLY_TIME(1003)");
        }
        mCacheType = cacheType;
    }

    public int getCacheType() {
        return mCacheType;
    }


    /**
     * Sets the expires. just like 1 or 24 * 3600 * 7 * 1000
     *
     * @param expires the expires
     * @return the APICacheStrategy
     */
    public APICacheStrategy setExpires(long expires) {
        switch (mCacheType) {
            case CACHE_TYPE_DAYS_INTERVAL:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ((long) expires) * 24 * 3600 * 1000);

                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                mCacheExpires = calendar.getTimeInMillis();
                break;
            case CACHE_TYPE_MILLIS_INTERVAL:
                mCacheExpires = expires + System.currentTimeMillis();
                break;

            case CACHE_TYPE_EXACTLY_TIME:
                mCacheExpires = expires;
                break;
        }
        return this;
    }

    public long getExpires() {
        return mCacheExpires;
    }

    public boolean isCacheExpired() {
        return (System.currentTimeMillis() - mCacheExpires) >= 0;
    }


    /**
     * get the interval between two days
     *
     * @param dateStart the start day
     * @param dateEnd the end day
     * @return the interval
     */
    private static int getIntervalDays(Date dateStart, Date dateEnd){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(dateStart);
        end.setTime(dateEnd);

        int interval = 0;
        while(start.before(end)){
            interval++;
            end.add(Calendar.DAY_OF_YEAR, 1);
        }
        return interval;
    }
}
