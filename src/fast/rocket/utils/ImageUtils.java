package fast.rocket.utils;

import fast.rocket.builder.ImageViewBuilder.ScaleMode;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class ImageUtils {
	public Bitmap resize(Bitmap b, int resizeWidth, int resizeHeight,
			ScaleMode scaleMode) {
		Bitmap bitmap = Bitmap.createBitmap(resizeWidth, resizeHeight, b.getConfig());
		Canvas canvas = new Canvas(bitmap);

		RectF destination = new RectF(0, 0, resizeWidth, resizeHeight);
		if (scaleMode != ScaleMode.FitXY) {
			float ratio;
			float xratio = (float) resizeWidth / (float) b.getWidth();
			float yratio = (float) resizeHeight / (float) b.getHeight();
			if (scaleMode == ScaleMode.CenterCrop)
				ratio = Math.max(xratio, yratio);
			else
				ratio = Math.min(xratio, yratio);

			float postWidth = b.getWidth() * ratio;
			float postHeight = b.getHeight() * ratio;
			float transx = (resizeWidth - postWidth) / 2;
			float transy = (resizeHeight - postHeight) / 2;
			destination.set(transx, transy, resizeWidth - transx, resizeHeight - transy);
		}

		canvas.drawBitmap(b, null, destination, null);
		return bitmap;
	}
	
}
