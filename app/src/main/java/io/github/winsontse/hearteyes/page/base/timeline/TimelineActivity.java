package io.github.winsontse.hearteyes.page.base.timeline;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.winsontse.hearteyes.page.adapter.base.BaseRecyclerAdapter;
import io.github.winsontse.hearteyes.page.adapter.base.OnRecyclerViewScrollListener;
import io.github.winsontse.hearteyes.page.base.BaseActivity;

/**
 * Created by winson on 2016/11/4.
 */

public abstract class TimelineActivity<T> extends BaseActivity implements TimelineView<T> {
    private TimelineViewPartImpl<T> viewImpl = new TimelineViewPartImpl<>(this);

    @Override
    public void installTimelineView(RecyclerView rv, LinearLayoutManager layoutManager, BaseRecyclerAdapter<T> adapter, SwipeRefreshLayout srl, View vEmpty, View vLoading) {
        viewImpl.installTimelineView(rv, layoutManager, adapter, srl, vEmpty, vLoading);
    }

    @Override
    public final void onRequestCompleted() {
        viewImpl.onRequestCompleted();
    }

    @Override
    public void onRefreshStart() {
        viewImpl.onRefreshStart();
    }

    @Override
    public void onRefreshCompleted(List<T> data) {
        viewImpl.onRefreshCompleted(data);
    }

    @Override
    public void onLoadMoreCompleted(List<T> data) {
        viewImpl.onLoadMoreCompleted(data);
    }

    @Override
    public void updateItem(int position) {
        viewImpl.updateItem(position);
    }

    @Override
    public void replaceItem(int position, T t) {
        viewImpl.replaceItem(position, t);
    }

    @Override
    public void setLoadMoreEnable(boolean enable) {
        viewImpl.setLoadMoreEnable(enable);
    }

    @Override
    public void showLoadMoreView() {
        viewImpl.showLoadMoreView();
    }

    @Override
    public void hideLoadMoreView() {
        viewImpl.hideLoadMoreView();
    }

    @Override
    public void showRefreshView() {
        viewImpl.showRefreshView();
    }

    @Override
    public void hideRefreshView() {
        viewImpl.hideRefreshView();
    }
}
