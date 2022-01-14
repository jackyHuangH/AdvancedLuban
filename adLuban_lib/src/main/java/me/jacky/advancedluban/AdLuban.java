/*Copyright 2016 Zheng Zibin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package me.jacky.advancedluban;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author hzj
 */
public class AdLuban {

    private static final String TAG = "Luban";

    private static String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private File mFile;

    private List<File> mFileList;

    private AdLubanBuilder mBuilder;

    private AdLuban(File cacheDir) {
        mBuilder = new AdLubanBuilder(cacheDir);
    }

    public static AdLuban compress(Context context, File file) {
        AdLuban luban = new AdLuban(AdLuban.getPhotoCacheDir(context));
        luban.mFile = file;
        luban.mFileList = Collections.singletonList(file);
        return luban;
    }

    public static AdLuban compress(Context context, List<File> files) {
        AdLuban luban = new AdLuban(AdLuban.getPhotoCacheDir(context));
        luban.mFileList = new ArrayList<>(files);
        luban.mFile = files.get(0);
        return luban;
    }

    /**
     * @param file     要压缩的单个文件
     * @param cacheDir 压缩完文件的存储路径
     */
    public static AdLuban compress(File file, File cacheDir) {
        if (!isCacheDirValid(cacheDir)) {
            throw new IllegalArgumentException("The cacheDir must be Directory");
        }
        AdLuban luban = new AdLuban(cacheDir);
        luban.mFile = file;
        luban.mFileList = Collections.singletonList(file);
        return luban;
    }

    public static AdLuban compress(List<File> files, File cacheDir) {
        if (!isCacheDirValid(cacheDir)) {
            throw new IllegalArgumentException("The cacheDir must be Directory");
        }
        AdLuban luban = new AdLuban(cacheDir);
        luban.mFile = files.get(0);
        luban.mFileList = new ArrayList<>(files);
        return luban;
    }

    private static boolean isCacheDirValid(File cacheDir) {
        return cacheDir.isDirectory() && (cacheDir.exists() || cacheDir.mkdirs());
    }

    /**
     * 自定义压缩模式 FAST_GEAR、LUBAN_GEAR、CUSTOM_GEAR
     */
    public AdLuban putGear(@CompressGear int gear) {
        mBuilder.gear = gear;
        return this;
    }

    /**
     * 自定义图片压缩格式
     */
    public AdLuban setCompressFormat(Bitmap.CompressFormat compressFormat) {
        mBuilder.compressFormat = compressFormat;
        return this;
    }

    /**
     * CUSTOM_GEAR 指定目标图片的最大体积,限制最终图片大小（单位：Kb）
     */
    public AdLuban setMaxSize(int size) {
        mBuilder.maxSize = size;
        return this;
    }

    /**
     * CUSTOM_GEAR 指定目标图片的最大宽度
     *
     * @param width 最大宽度
     */
    public AdLuban setMaxWidth(int width) {
        mBuilder.maxWidth = width;
        return this;
    }

    /**
     * CUSTOM_GEAR 指定目标图片的最大高度
     *
     * @param height 最大高度
     */
    public AdLuban setMaxHeight(int height) {
        mBuilder.maxHeight = height;
        return this;
    }

    /**
     * listener调用方式，在主线程订阅并将返回结果通过 listener 通知调用方
     *
     * @param listener 接收回调结果
     */
    public void launch(final AdOnCompressListener listener) {
        asObservable().observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(
                new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStart();
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        listener.onSuccess(file);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onError(throwable);
                    }
                });
    }

    /**
     * listener调用方式，在主线程订阅并将返回结果通过 listener 通知调用方
     *
     * @param listener 接收回调结果
     */
    public void launch(final AdOnMultiCompressListener listener) {
        asListObservable().observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStart();
                    }
                })
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        listener.onSuccess(files);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onError(throwable);
                    }
                });
    }

    /**
     * 返回File Observable
     */
    public Observable<File> asObservable() {
        AdLubanCompressor compresser = new AdLubanCompressor(mBuilder);
        return compresser.singleAction(mFile);
    }

    /**
     * 返回fileList Observable
     */
    public Observable<List<File>> asListObservable() {
        AdLubanCompressor compresser = new AdLubanCompressor(mBuilder);
        return compresser.multiAction(mFileList);
    }

    // Utils

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store
     * retrieved media and thumbnails.
     *
     * @param context A context.
     * @see #getPhotoCacheDir(Context, String)
     */
    private static File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, AdLuban.DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store
     * retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getPhotoCacheDir(Context)
     */
    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    /**
     * 清空Luban所产生的缓存
     * Clears the cache generated by Luban
     */
    public AdLuban clearCache() {
        if (mBuilder.cacheDir.exists()) {
            deleteFile(mBuilder.cacheDir);
        }
        return this;
    }

    /**
     * 清空目标文件或文件夹
     * Empty the target file or folder
     */
    private void deleteFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File file : fileOrDirectory.listFiles()) {
                deleteFile(file);
            }
        }
        fileOrDirectory.delete();
    }
}