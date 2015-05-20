package com.mlxy.disklrucachetest.util;

import android.content.Context;
import android.os.Environment;

import com.mlxy.disklrucachetest.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruCacheHelper {
    private static DiskLruCache mCache;

    /** 打开DiskLruCache。 */
    public static void openCache(Context context) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                mCache = DiskLruCache.open(context.getExternalCacheDir(), 0, 1, 10 * 1024 * 1024);
            } else {
                mCache = DiskLruCache.open(context.getCacheDir(), 0, 1, 10 * 1024 * 1024);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 写出缓存。 */
    public static void dump(InputStream inputStream, String keyCache) throws IOException {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");

        DiskLruCache.Editor mEditor = mCache.edit(keyCache);

        OutputStream outputStream = mEditor.newOutputStream(0);

        BufferedInputStream bin = new BufferedInputStream(inputStream);
        BufferedOutputStream bout = new BufferedOutputStream(outputStream);

        byte[] buf = new byte[1024];
        int len;
        while ((len = bin.read(buf)) != -1) {
            bout.write(buf, 0, len);
        }

        bout.close();
        outputStream.close();

        mEditor.commit();
    }

    /** 读取缓存。 */
    public static InputStream load(String keyCache) throws IOException {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");

        DiskLruCache.Snapshot snapshot = mCache.get(keyCache);

        if (snapshot == null) return null;
        else return snapshot.getInputStream(0);
    }

    /** 同步日志。 */
    public static void syncJournal() {
        try {
            mCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 关闭缓存。 */
    public static void closeCache() {
        if (!mCache.isClosed()) {
            try {
                mCache.close();
                mCache = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
