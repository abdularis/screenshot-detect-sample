package com.aar.screenshotdetect.ssdetect;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

public class SsDetect {

    private static final String TAG = "SsDetect";

    private static final String PATTERN_NAME_PREFIX = "screenshot.*";
    private static final String PATTERN_PATH = ".*/screenshots/.*";
    private static final String PATTERN_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/[0-9]+";

    public interface OnScreenshotTakenListener {
        void onScreenshotTaken(String path);
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private HandlerThread mHandlerThread = new HandlerThread("ss-detect");
    private Handler mHandler;
    private OnScreenshotTakenListener mOnScreenshotTakenListener;
    private ContentResolver mContentResolver;
    private ContentObserver mContentObserver;

    public SsDetect(@NonNull ContentResolver contentResolver,
                    @NonNull OnScreenshotTakenListener onScreenshotTakenListener) {
        mContentResolver = contentResolver;
        mOnScreenshotTakenListener = onScreenshotTakenListener;

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mContentObserver = new ContentObserverImpl(mHandler);
    }

    public void startDetecting() {
        mContentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mContentObserver);
    }

    public void stopDetecting() {
        mContentResolver.unregisterContentObserver(mContentObserver);
    }

    private class ContentObserverImpl extends ContentObserver {

        public ContentObserverImpl(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (!uri.toString().matches(PATTERN_URI)) {
                return;
            }

            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(uri, new String[] {
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA
                }, null, null, null);
            } catch (SecurityException ext) {
                Log.d(TAG, "Failed to read file name & path: needs read external storage permission");
            }

            if (cursor != null && cursor.moveToFirst()) {
                final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();

                if (isFilenameSs(fileName) && isPathSs(path)) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnScreenshotTakenListener.onScreenshotTaken(path);
                        }
                    });
                }
            }
        }

        private boolean isFilenameSs(String fileName) {
            return fileName.toLowerCase().matches(PATTERN_NAME_PREFIX);
        }

        private boolean isPathSs(String path) {
            return path.toLowerCase().matches(PATTERN_PATH);
        }
    }
}
