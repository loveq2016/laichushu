package com.laichushu.book.retrofit;


import com.laichushu.book.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * retrofit 回调方法
 */
public abstract class ApiCallback<M> extends Subscriber<M> {

    public abstract void onSuccess(M model);

    public abstract void onFailure(int code, String msg);

    public abstract void onFinish();


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //httpException.response().errorBody().string()
            int code = httpException.code();
            String msg = httpException.getMessage();
            Logger.e("code: ", code);
            if (code == 504) {
                msg = "网络不给力";
                ToastUtil.showToast("网络错误，请检查网络！");
            }
            if (code == 502 || code == 404) {
                msg = "服务器异常，请稍后再试";
                ToastUtil.showToast("服务器异常，请稍后再试！");
            }
            onFailure(code, msg);
        } else {
            ToastUtil.showToast("网络错误，请检查网络！");
            onFailure(0, e.getMessage());
        }
        onFinish();

    }

    @Override
    public void onNext(M model) {
        onSuccess(model);
    }

    @Override
    public void onCompleted() {
        onFinish();
    }
}
