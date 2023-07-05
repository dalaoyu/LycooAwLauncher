package com.lycoo.commons.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

	private static final String TAG = ImageUtils.class.getSimpleName();

	/**
	 * 将Bitmap转换为byte[]
	 *
	 * @param bitmap
	 * @return
	 * @author michaellancy ------ 2013-6-2------------
	 */
	public static byte[] flattenBitmap(Bitmap bitmap) {
		// Try go guesstimate how much space the icon will take when serialized to avoid unnecessary allocations/copies during the write.
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			Log.w("Favorite", "Could not write icon");
			return null;
		}
	}

	/**
	 * 获取圆角图片
	 * 
	 * @param bitmap
	 * @param cornerSize
	 * @return
	 * @author lancy ------------------ 2016年3月10日 ------------------
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float cornerSize) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		// 得到画布
		Canvas canvas = new Canvas(output);

		// 将画布的四角圆化
		final int color = Color.RED;
		final Paint paint = new Paint();
		// 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// 值越大角度越明显

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
		canvas.drawRoundRect(rectF, cornerSize, cornerSize, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// --------------------------- 后面的方法没有实际验证 --------------------------------------------------------- //
	// --------------------------- 后面的方法没有实际验证 --------------------------------------------------------- //
	public static Bitmap zoomBitmapFromImagePath(String path, int reqWidth, int reqHeight) {
		Bitmap bitmap;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		options.inJustDecodeBounds = false;

		int desiredWidth = getResizedDimension(reqWidth, reqHeight, imageWidth, imageHeight);
		int desiredHeight = getResizedDimension(reqHeight, reqWidth, imageHeight, imageWidth);

		options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, desiredWidth, desiredHeight);

		Bitmap destBitmap = BitmapFactory.decodeFile(path, options);
		if (destBitmap.getWidth() > desiredWidth || destBitmap.getHeight() > desiredHeight) {
			bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth, desiredHeight, true);
			destBitmap.recycle();
		} else {
			bitmap = destBitmap;
		}

		return bitmap;
	}

	public static Bitmap zoomBitmapFromImageId(Resources resources, int resId, int reqWidth, int reqHeight) {
		Bitmap bitmap;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		options.inJustDecodeBounds = false;

		int desiredWidth = getResizedDimension(reqWidth, reqHeight, imageWidth, imageHeight);
		int desiredHeight = getResizedDimension(reqHeight, reqWidth, imageHeight, imageWidth);

		options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, desiredWidth, desiredHeight);

		Bitmap destBitmap = BitmapFactory.decodeResource(resources, resId, options);
		if (destBitmap.getWidth() > desiredWidth || destBitmap.getHeight() > desiredHeight) {
			bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth, desiredHeight, true);
			destBitmap.recycle();
		} else {
			bitmap = destBitmap;
		}

		return bitmap;
	}

	private static int calculateInSampleSize(int imageWidth, int imageHeight, int desiredWidth, int desiredHeight) {
		float inSampleSize = 1.0f;
		if (imageWidth > desiredWidth || imageHeight > desiredHeight) {
			int widthRatio = Math.round((float) imageWidth / (float) desiredWidth);
			int heightRatio = Math.round((float) imageHeight / (float) desiredHeight);

			double ratio = Math.min(widthRatio, heightRatio);
			while ((inSampleSize * 2) <= ratio) {
				inSampleSize *= 2;
			}
		}

		return (int) inSampleSize;
	}

	private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * 对于有些图片例如：1280 * 720 不能很好的压缩, 所以弃用
	 * 
	 * michaellancy ======================== 2015年8月10日 ==============================
	 */
	public static int calculateInSampleSize_01(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		// get image width and height
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		LogUtils.debug(TAG, "imageWidth = " + imageWidth + ", imageHeight = " + imageHeight);

		if (imageWidth > reqWidth || imageHeight > reqHeight) {
			// calculate actrual width and height ratio
			int widthRatio = Math.round((float) imageWidth / (float) reqWidth);
			int heightRatio = Math.round((float) imageHeight / (float) reqHeight);

			// select samllest ration of widthRatio and heightRatio
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 获取图片的实际尺寸，好处是图片没有加载到内存
	 * 
	 * michaellancy ======================== 2015年4月25日 ==============================
	 */
	public static int[] getImageSize(String path) {
		int[] size = { -1, -1 };
		if (path == null) {
			return size;
		}

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			size[0] = options.outWidth;
			size[1] = options.outHeight;
			options.inJustDecodeBounds = false;
		} catch (Exception e) {
			Log.w(TAG, "getImageWH Exception.", e);
		}
		return size;
	}

}
