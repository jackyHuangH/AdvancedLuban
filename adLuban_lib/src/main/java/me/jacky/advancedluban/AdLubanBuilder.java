package me.jacky.advancedluban;

import android.graphics.Bitmap;
import java.io.File;

/**
 * Created by shaohui on 2016/12/17.
 */

class AdLubanBuilder {

    /** 压缩后的图片大小（单位：Kb）*/
    int maxSize;
    /** 压缩后的图片最大宽度，单位像素*/
    int maxWidth;
    /** 压缩后的图片最大高度，单位像素*/
    int maxHeight;
    /** 压缩后的图片存储目录*/
    File cacheDir;
    /** 压缩后的图片格式，默认JPEG，目前只支持：JEPG和WEBP，因为png不支持压缩图片品质*/
    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    /** 压缩图片档位，默认第三档*/
    int gear = CompressGear.LUBAN_GEAR;

    AdLubanBuilder(File cacheDir) {
        this.cacheDir = cacheDir;
    }

}
