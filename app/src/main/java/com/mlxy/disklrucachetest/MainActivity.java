package com.mlxy.disklrucachetest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mlxy.disklrucachetest.util.Digester;
import com.mlxy.disklrucachetest.util.DiskLruCacheHelper;
import com.mlxy.disklrucachetest.util.NetworkAdministrator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private static final String IMAGE_URL = "http://img0.ph.126.net/UIzxLtsQiRa5F0pDmxYHPQ==/3361374171980166522.jpg";

    private android.widget.ImageView imageView;
    private android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        DiskLruCacheHelper.openCache(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImage();
                hideProgressBar();
            }
        }).start();
    }

    /** 初始化视图。 */
    private void initViews() {
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.imageView = (ImageView) findViewById(R.id.imageView);
    }

    /** 读取图片。 */
    private void loadImage() {
        try {
            // 使用url的md5作为缓存键。
            String keyCache = Digester.hashUp(IMAGE_URL);

            // 读缓存。
            InputStream inputStream = DiskLruCacheHelper.load(keyCache);

            if (inputStream == null) {
                // 没缓存就联网找。
                inputStream = NetworkAdministrator.openUrlInputStream(IMAGE_URL);
                inputStream = new BufferedInputStream(inputStream);

                // 写缓存。
                dumpCache(keyCache, inputStream);

                toast("Image read from Internet.");
            } else {
                toast("Image read from disk cache.");
            }

            // 显示图片。
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            showImage(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 写出缓存。 */
    private void dumpCache(String keyCache, InputStream inputStream) throws IOException {
        // 标记位置留待恢复指针位置。
        inputStream.mark(Integer.MAX_VALUE);

        DiskLruCacheHelper.dump(inputStream, keyCache);

        // 恢复指针位置以备再次读取。
        inputStream.reset();
    }

    /** 显示图片。 */
    private void showImage(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    /** 隐藏进度条。 */
    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
