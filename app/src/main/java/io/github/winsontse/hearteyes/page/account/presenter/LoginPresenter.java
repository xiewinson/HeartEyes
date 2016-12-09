package io.github.winsontse.hearteyes.page.account.presenter;

import android.os.Bundle;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.util.HashMap;

import javax.inject.Inject;

import io.github.winsontse.hearteyes.util.rxbus.event.PushEvent;
import io.github.winsontse.hearteyes.data.model.leancloud.UserContract;
import io.github.winsontse.hearteyes.data.model.weibo.WeiboUser;
import io.github.winsontse.hearteyes.data.remote.WeiboApi;
import io.github.winsontse.hearteyes.page.account.contract.LoginContract;
import io.github.winsontse.hearteyes.page.base.BasePresenterImpl;
import io.github.winsontse.hearteyes.util.HeartEyesSubscriber;
import io.github.winsontse.hearteyes.util.RxUtil;
import io.github.winsontse.hearteyes.util.rxbus.RxBus;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class LoginPresenter extends BasePresenterImpl implements LoginContract.Presenter {
    private LoginContract.View view;
    private WeiboApi weiboApi;

    @Inject
    public LoginPresenter(LoginContract.View view, WeiboApi weiboApi) {
        this.view = view;
        this.weiboApi = weiboApi;
    }


    @Override
    public void authirize(final SsoHandler ssoHandler) {
        initAuthListener(ssoHandler);
    }

    private void initAuthListener(SsoHandler ssoHandler) {
        ssoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                handleWeiboToken(bundle);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                view.showToast(e.getMessage());
                view.showEnterFab();
            }

            @Override
            public void onCancel() {
                view.showEnterFab();
            }
        });
    }

    private void handleWeiboToken(Bundle bundle) {
        final Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(bundle);
        AVUser.AVThirdPartyUserAuth userAuth = new AVUser.AVThirdPartyUserAuth(token.getToken(), token.getExpiresTime() + "", "weibo", token.getUid());//此处snsType 可以是"qq","weibo"等字符串
        AVUser.loginWithAuthData(userAuth, new LogInCallback<AVUser>() {
            @Override
            public void done(final AVUser avUser, AVException e) {
                if (e != null) {
                    view.showToast(e.getMessage());
                } else {
                    keepWeiboUserInfo(avUser, token);
                }
            }
        });
    }

    private void keepWeiboUserInfo(final AVUser avUser, Oauth2AccessToken token) {
        addSubscription(weiboApi.getUserByUid(token.getToken(), token.getUid())
                .flatMap(new Func1<WeiboUser, Observable<AVUser>>() {
                    @Override
                    public Observable<AVUser> call(final WeiboUser weiboUser) {
                        return Observable.create(new Observable.OnSubscribe<AVUser>() {
                            @Override
                            public void call(Subscriber<? super AVUser> subscriber) {
                                AVFile avFile = new AVFile(weiboUser.getId() + "_" + System.currentTimeMillis() + ".jpg", weiboUser.getAvatar_large(), new HashMap<String, Object>());
                                try {
                                    avFile.save();
                                    avUser.put(UserContract.NICKNAME, weiboUser.getName());
                                    avUser.put(UserContract.AVATAR, avFile);
                                    avUser.save();
                                    subscriber.onNext(avUser);
                                    subscriber.onCompleted();
                                } catch (AVException e1) {
                                    subscriber.onError(e1);
                                }
                            }
                        });
                    }
                })
                .compose(RxUtil.rxSchedulerHelper(AVUser.class))
                .subscribe(new HeartEyesSubscriber<AVUser>(view) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void handleError(Throwable e) {

                    }

                    @Override
                    public void onNext(AVUser avUser) {
                        Log.d("winson", avUser.getString(UserContract.NICKNAME) + "   " + avUser.getAVFile(UserContract.AVATAR).getName());
                        RxBus.getInstance().post(new PushEvent(PushEvent.RESTART_INIT_PAGE));
                    }
                }));
    }
}
