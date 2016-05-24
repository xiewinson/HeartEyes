package io.github.winsontse.hearteyes.page.base;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import javax.annotation.Resource;

import io.github.winsontse.hearteyes.app.HeartEyesApplication;

/**
 * Created by hao.xie on 16/5/9.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    private ActivityComponent activityComponent;
    private ProgressDialog progressDialog;
    private Resources resources;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        setupComponent(getActivityComponent());
    }

    protected ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent
                    .builder()
                    .appComponent(HeartEyesApplication.get(this).getAppComponent())
                    .activityModule(new ActivityModule(this))
                    .build();
        }
        return activityComponent;
    }

    protected abstract void setupComponent(ActivityComponent activityComponent);

    protected abstract BasePresenter getPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.unsubscribe();
        }
    }

    @Override
    public void showProgressDialog(boolean cancelable, String msg) {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (TextUtils.isEmpty(msg)) {
            progressDialog.setMessage("");
        } else {
            progressDialog.setMessage(msg);
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void showToast(String msg) {
        if (!isFinishing()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public String getStringById(int strId) {
        return resources.getString(strId);
    }

    @Override
    public String[] getStringArrayById(int arrayId) {
        return resources.getStringArray(arrayId);
    }

    @Override
    public Drawable getDrawableById(int drawableId) {
        return resources.getDrawable(drawableId);
    }

    @Override
    public int getColorById(int colorId) {
        return resources.getColor(colorId);
    }
}