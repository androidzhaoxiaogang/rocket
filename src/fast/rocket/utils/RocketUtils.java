package fast.rocket.utils;

import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * The Class RocketUtils.
 */
public class RocketUtils {
	
	/**
	 * Checks the current building sdk is honeycomb or not.
	 *
	 * @return true, if successful
	 */
	public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
	
	/**
	 * Load animation for the imageview.
	 *
	 * @param imageView the image view
	 * @param animation the animation
	 * @param animationResource the animation resource
	 */
	public static void loadAnimation(ImageView imageView, Animation animation, int animationResource) {
        if (imageView == null)
            return;
        if (animation == null && animationResource != 0)
            animation = AnimationUtils.loadAnimation(imageView.getContext(), animationResource);
        if (animation == null) {
            imageView.setAnimation(null);
            return;
        }

        imageView.startAnimation(animation);
    }
}
