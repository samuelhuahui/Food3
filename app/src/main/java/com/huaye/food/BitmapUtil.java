package com.huaye.food;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class BitmapUtil {
	public static final int IMAGE_MAX_SIZE_LIMIT = 100;
	
	private static final int IMAGE_COMPRESSION_QUALITY = 90;
	private static final int NUMBER_OF_RESIZE_ATTEMPTS = 100;
	private static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 50;
	//private int mWidth;
	//private int mHeight;
	
	public static String compressImage(String path) {

		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, op);
		
		int outWidth = op.outWidth;
		int outHeight = op.outHeight;
		
		int widthLimit = op.outWidth;
		int heightLimit = op.outHeight;
		
		int byteLimit = IMAGE_MAX_SIZE_LIMIT * 1024;

		float scaleFactor = 1.F;
		while ((outWidth * scaleFactor > widthLimit)
				|| (outHeight * scaleFactor > heightLimit)) {
			scaleFactor *= .75F;
		}
		try {
			ByteArrayOutputStream os = null;
			int attempts = 1;
			int sampleSize = 1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			int quality = IMAGE_COMPRESSION_QUALITY;
			Bitmap b = null;
			do {
				options.inSampleSize = sampleSize;
				try {
					b = BitmapFactory.decodeFile(path, options);
					if (b == null) {
						return null; // Couldn't decode and it wasn't because of
					}
				} catch (OutOfMemoryError e) {
					sampleSize *= 2; // works best as a power of two
					attempts++;
					continue;
				}
			} while (b == null && attempts < NUMBER_OF_RESIZE_ATTEMPTS);

			if (b == null) {
				return null;
			}
			boolean resultTooBig = true;
			attempts = 1; // reset count for second loop
			do {
				try {
					if (options.outWidth > widthLimit
							|| options.outHeight > heightLimit
							|| (os != null && os.size() > byteLimit)) {
						int scaledWidth = (int) (outWidth * scaleFactor);
						int scaledHeight = (int) (outHeight * scaleFactor);

						b = Bitmap.createScaledBitmap(b, scaledWidth,
								scaledHeight, false);
						if (b == null) {
							return null;
						}
					}
					os = new ByteArrayOutputStream();
					b.compress(CompressFormat.JPEG, quality, os);
					int jpgFileSize = os.size();
					if (jpgFileSize > byteLimit) {
						quality = (quality * byteLimit) / jpgFileSize; // watch
						if (quality < MINIMUM_IMAGE_COMPRESSION_QUALITY) {
							quality = MINIMUM_IMAGE_COMPRESSION_QUALITY;
						}
						os = new ByteArrayOutputStream();
						b.compress(CompressFormat.JPEG, quality, os);
					}
				} catch (java.lang.OutOfMemoryError e) {
					// Log.w(TAG,
				}
				scaleFactor *= .75F;
				attempts++;
				resultTooBig = os == null || os.size() > byteLimit;
			} while (resultTooBig && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
			b.recycle(); // done with the bitmap, release the memory
			if(resultTooBig){
				return path;
			}
			FileOutputStream out = null;
			try {
				String p = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "huaye" + File.separator + System.currentTimeMillis() + ".jpg";
				out = new FileOutputStream(p);
				out.write(os.toByteArray());
				out.close();
				return p;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return path;
			
		} catch (java.lang.OutOfMemoryError e) {
			
			return path;
		}
	}


}
