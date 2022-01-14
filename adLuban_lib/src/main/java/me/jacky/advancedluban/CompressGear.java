package me.jacky.advancedluban;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IntDef;

import static me.jacky.advancedluban.CompressGear.CUSTOM_GEAR;
import static me.jacky.advancedluban.CompressGear.FAST_GEAR;
import static me.jacky.advancedluban.CompressGear.LUBAN_GEAR;


/**
 * @author:Hzj
 * @date :2022/1/13
 * desc  ：图片压缩模式
 * record：
 */
@IntDef({FAST_GEAR, LUBAN_GEAR, CUSTOM_GEAR})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface CompressGear {
    /**
     * 极速模式，图片体积小，质量差
     */
    int FAST_GEAR = 1;
    /**
     * 默认鲁班模式
     */
    int LUBAN_GEAR = 3;
    /**
     * 自定义模式，可以限制图片宽度、高度、最大尺寸
     */
    int CUSTOM_GEAR = 4;
}
