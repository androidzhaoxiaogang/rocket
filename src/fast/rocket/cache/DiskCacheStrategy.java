package fast.rocket.cache;

import java.util.Calendar;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: haozi
 * Date: 13-11-25
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
/**
 * @author hp
 *
 */
public class DiskCacheStrategy {
	
	/** The Constant EXPIRE_MINUTES. */
	private static final int EXPIRE_MINUTES = 5;
	
	/** The Constant ONE_DAY. */
	private static final int ONE_DAY = 1;
    
    /** The m cache expires. */
    private long mCacheExpires; //ms
    
    /** The m cache type. */
    private int mCacheType;
    
    /**
     * The Interface Strategy.
     */
    public interface Strategy {
        
        /** The days interval. */
        int DAYS_INTERVAL = -3;
        
        /** The millis interval. */
        int HOURS_INTERVAL = -2;
        
        /** The exactly time. */
        int MILLIS_INTERVAL = -1;
    }

    /**
     * Gets the cache type.
     *
     * @return the cache type
     */
    public int getCacheType() {
        return mCacheType;
    }
    
    /**
     * Instantiates a new disk cache strategy.
     */
    public DiskCacheStrategy() {
    	this(Strategy.DAYS_INTERVAL, ONE_DAY);
    }

    /**
     * Sets the expires. just like 1 or 24 * 3600 * 7 * 1000
     *
     * @param cacheType the cache type
     * @param expires the expires
     * @return the APICacheStrategy
     */
    public DiskCacheStrategy(int cacheType, long expires) {
    	if(expires != Strategy.DAYS_INTERVAL && expires != Strategy.DAYS_INTERVAL 
    			&& expires != Strategy.DAYS_INTERVAL && (expires >> 16) < EXPIRE_MINUTES  
    			) {
    		throw new IllegalArgumentException("Unreasonable expire value set.");
    	}
    	
        switch (mCacheType) {
            case Strategy.DAYS_INTERVAL:
                mCacheExpires = getExpiresLong(expires, 24, 60);
                break;
            case Strategy.HOURS_INTERVAL:
                mCacheExpires = getExpiresLong(ONE_DAY, expires, 60);
                break;
            case Strategy.MILLIS_INTERVAL:
                mCacheExpires = getExpiresLong(ONE_DAY, 1, expires);
                break;
        }
    }

    /**
     * Gets the expires.
     *
     * @return the expires
     */
    public long getExpires() {
        return mCacheExpires;
    }

    /**
     * Checks if is cache expired.
     *
     * @return true, if is cache expired
     */
    public boolean isCacheExpired() {
        return (System.currentTimeMillis() - mCacheExpires) >= 0;
    }
    
    /**
     * Gets the expires long.
     *
     * @param day the day
     * @param hour the hour
     * @param minute the minute
     * @return the expires long
     */
    private long getExpiresLong(long day, long hour, long  minute) {
    	 Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         calendar.setTimeInMillis(calendar.getTimeInMillis() 
        		 + day * hour * minute * 60 * 1000);

         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.MILLISECOND, 0);

         return calendar.getTimeInMillis();
    }

}
