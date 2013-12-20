package fast.rocket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
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
	 * @param imageView
	 *            the image view
	 * @param animation
	 *            the animation
	 * @param animationResource
	 *            the animation resource
	 */
	public static void loadAnimation(ImageView imageView, Animation animation,
			int animationResource) {
		if (imageView == null)
			return;
		if (animation == null && animationResource != 0)
			animation = AnimationUtils.loadAnimation(imageView.getContext(),
					animationResource);
		if (animation == null) {
			imageView.setAnimation(null);
			return;
		}

		imageView.startAnimation(animation);
	}

	public static SSLSocketFactory getSSLSocketFactory(Context context, int res){

		// 証明書の規定の設定
		CertificateFactory certificateFactory = null;
		try {
			certificateFactory = CertificateFactory
					.getInstance("X.509");
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// サーバー証明書の読み込み用
		InputStream inputStream = context.getResources().openRawResource(res);

		// 証明書の読み込み(生成)
		Certificate certificate = null;
		try {
			certificate = certificateFactory
					.generateCertificate(inputStream);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String keyType = KeyStore.getDefaultType();

		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(keyType);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyStore.load(null, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 証明書の指定
		try {
			keyStore.setCertificateEntry("ca", certificate);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TrustManagerFactory trustManagerFactory = null;
		try {
			trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			trustManagerFactory.init(keyStore);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SSLのプロトコルを指定
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sslContext.getSocketFactory();
	}
}
