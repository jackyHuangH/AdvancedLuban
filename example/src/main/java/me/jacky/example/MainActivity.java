package me.jacky.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.functions.Consumer;
import me.jacky.advancedluban.AdLuban;
import me.jacky.advancedluban.AdOnCompressListener;
import me.jacky.advancedluban.AdOnMultiCompressListener;
import me.jacky.advancedluban.CompressGear;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 *
 * @author hzj
 */
@SuppressLint("CheckResult")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AdLubanExample";

    private static final int REQUEST_CODE = 1;

    private List<File> mFileList;

    private List<ImageView> mImageViews;

    private RadioGroup mMethodGroup;

    private RadioGroup mGearGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileList = new ArrayList<>();

        mImageViews = new ArrayList<>();
        mImageViews.add((ImageView) findViewById(R.id.image_1));
        mImageViews.add((ImageView) findViewById(R.id.image_2));
        mImageViews.add((ImageView) findViewById(R.id.image_3));
        mImageViews.add((ImageView) findViewById(R.id.image_4));
        mImageViews.add((ImageView) findViewById(R.id.image_5));
        mImageViews.add((ImageView) findViewById(R.id.image_6));
        mImageViews.add((ImageView) findViewById(R.id.image_7));
        mImageViews.add((ImageView) findViewById(R.id.image_8));
        mImageViews.add((ImageView) findViewById(R.id.image_9));

        mMethodGroup = (RadioGroup) findViewById(R.id.method_group);
        mGearGroup = (RadioGroup) findViewById(R.id.gear_group);

        findViewById(R.id.select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelector.create().start(MainActivity.this, REQUEST_CODE);
            }
        });
        findViewById(R.id.compress_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressImage();
            }
        });
    }

    private void compressImage() {
        int gear;
        int checkedRadioButtonId = mGearGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.third_gear) {
            gear = CompressGear.LUBAN_GEAR;
        } else if (checkedRadioButtonId == R.id.first_gear) {
            gear = CompressGear.FAST_GEAR;
        } else {
            gear = CompressGear.LUBAN_GEAR;
        }
        int radioButtonId = mMethodGroup.getCheckedRadioButtonId();
        if (radioButtonId == R.id.method_listener) {
            if (mFileList.size() == 1) {
                compressSingleListener(gear);
            } else {
                compressMultiListener(gear);
            }
        } else if (radioButtonId == R.id.method_rxjava) {
            if (mFileList.size() == 1) {
                compressSingleRxJava(gear);
            } else {
                compressMultiRxJava(gear);
            }
        }
    }

    private void compressSingleRxJava(int gear) {
        if (mFileList.isEmpty()) {
            return;
        }
        AdLuban.compress(this,mFileList.get(0))
                .putGear(gear)
                .setMaxSize(300)
                .asObservable()
                .subscribe(file -> {
                    Log.i("TAG", file.getAbsolutePath());
                    mImageViews.get(0).setImageURI(Uri.fromFile(file));
                }, throwable -> throwable.printStackTrace());
    }

    private void compressMultiRxJava(int gear) {
        if (mFileList.isEmpty()) {
            return;
        }
        AdLuban.compress(this, mFileList)
                .putGear(gear)
                .asListObservable()
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        int size = files.size();
                        while (size-- > 0) {
                            mImageViews.get(size).setImageURI(Uri.fromFile(files.get(size)));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void compressSingleListener(int gear) {
        if (mFileList.isEmpty()) {
            return;
        }
        AdLuban.compress(this,mFileList.get(0))
                .putGear(gear)
                .setMaxSize(300)
                .launch(new AdOnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.i(TAG, "start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("TAG", file.getAbsolutePath());
                        mImageViews.get(0).setImageURI(Uri.fromFile(file));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void compressMultiListener(int gear) {
        if (mFileList.isEmpty()) {
            return;
        }
        AdLuban.compress(this, mFileList)
                .putGear(gear)
                .launch(new AdOnMultiCompressListener() {
                    @Override
                    public void onStart() {
                        Log.i(TAG, "start");
                    }

                    @Override
                    public void onSuccess(List<File> fileList) {
                        int size = fileList.size();
                        while (size-- > 0) {
                            mImageViews.get(size).setImageURI(Uri.fromFile(fileList.get(size)));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && data != null) {
            mFileList.clear();
            List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            for (String str : path) {
                mFileList.add(new File(str));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
