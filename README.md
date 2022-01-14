# AdvancedLuban
基于Luban压缩算法，可限制文件宽、高和最大size，原项目：https://github.com/shaohui10086/AdvancedLuban

## 引入App build.gradle
	implementation 'me.shaohui.advancedluban:library:1.3.5'
## 使用

### `Listener` mode

`Advanced Luban` internal` Computation` thread for image compression, external calls simply set the Listener can be:

    Luban.compress(context, file)
        .putGear(Luban.THIRD_GEAR)      // set the compress mode, default is : THIRD_GEAR
        .launch(listener);              // start compression and set the listener

### `RxJava` mode

`RxJava` call the same default` Computation` thread to compress, you can also define any thread, can be observed in any thread:

    Luban.compress(context, file)                          
            .putGear(Luban.CUSTOM_GEAR)                 
            .asObservable()                             // generate Observable
            .subscribe(successAction, errorAction)      // subscribe the compress result

### Compression mode

    
#### 1. CUSTOM_GEAR

compress image file according to the restrictions you set, you can limit: the width, height or file size of the image file 
    
        Luban.compress(context, file)
                .setMaxSize(500)                // limit the final image size（unit：Kb）
                .setMaxHeight(1920)             // limit image height
                .setMaxWidth(1080)              // limit image width
                .putGear(Luban.CUSTOM_GEAR)     // use CUSTOM GEAR compression mode
                .asObservable()

#### 2. THIRD_GEAR 

Using custom algorithms, according to the picture aspect ratio, the picture is compressed quickly, the resulting image size is about 100Kb, for general compression, no file size limit and picture width limit

#### 3. FIRST_GEAR

The simplified version of `THIRD GEAR`, the compressed image resolution is less than 1280 x 720, the final file is less than 60Kb. suitable for fast compression, regardless of the final picture quality

## Multi-Image synchronous compression

If you choose to call the way `Listener`:

        Luban.get(this)
                .putGear(Luban.CUSTOM_GEAR)             
                .load(fileList)                     // load all images
                .launch(multiCompressListener);     // passing an OnMultiCompress Listener

or the `RxJava` way to use:

        Luban.get(this)
                .putGear(Luban.CUSTOM_GEAR)             
                .load(fileList)                     // load all images
                .asListObservable()                 // Generates Observable <List<File>. Returns the result of all the images compressed successfully

## About OOM

If you use a multi-map compression, we must take into account the risk of OOM, recommend you use CUSTOM_GEAR, and then customize the compression index, to a large extent reduce the risk of OOM, the current test did not find the problem of OOM

## Thanks For
- https://github.com/Curzibn/Luban
- https://github.com/ReactiveX/Rxjava
- https://github.com/ReactiveX/RxAndroid
