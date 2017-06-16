package com.duckduckgo.mobile.android.duckduckgo.ui.browser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.duckduckgo.Injector;
import com.duckduckgo.mobile.android.duckduckgo.R;
import com.duckduckgo.mobile.android.duckduckgo.ui.omnibar.Omnibar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fgei on 5/15/17.
 */

public class BrowserFragment extends Fragment {

    public static final String TAG = BrowserFragment.class.getSimpleName();

    public static BrowserFragment newInstance() {
        return new BrowserFragment();
    }

    @BindView(R.id.omnibar)
    Omnibar omnibar;

    @BindView(R.id.browser)
    Browser browser;

    private Unbinder unbinder;
    private BrowserPresenter browserPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        browserPresenter = Injector.injectBrowserPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browser, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        browserPresenter.attachBrowserView(browser);

        browserPresenter.attachOmnibarView(omnibar);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();

        if (savedInstanceState != null) {
            browser.restoreState(savedInstanceState);
        }

        browserPresenter.loadTabs(savedInstanceState != null);
    }

    @Override
    public void onResume() {
        super.onResume();
        browser.resume();
    }

    @Override
    public void onPause() {
        browser.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        browserPresenter.detachBrowserView();
        browserPresenter.detachOmnibarView();
        browser.destroy();
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        browserPresenter.saveSession();
        browser.saveState(outState);
    }

    private void initUI() {
        initOmnibar(omnibar);
    }

    private void initOmnibar(Omnibar omnibar) {
        omnibar.inflateMenu(R.menu.browser);
        omnibar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_go_back:
                        browserPresenter.navigateHistoryBackward();
                        return true;
                    case R.id.action_go_forward:
                        browserPresenter.navigateHistoryForward();
                        return true;
                    case R.id.action_refresh:
                        browserPresenter.refreshCurrentPage();
                        return true;
                    case R.id.action_bookmarks:
                        browserPresenter.viewBookmarks();
                        return true;
                    case R.id.action_save_bookmark:
                        browserPresenter.requestSaveCurrentPageAsBookmark();
                        return true;
                    case R.id.action_open_tab_switcher:
                        browserPresenter.openTabSwitcher();
                        return true;
                }
                return false;
            }
        });
        omnibar.setOnSearchListener(new Omnibar.OnSearchListener() {
            @Override
            public void onTextSearched(@NonNull String text) {
                browserPresenter.requestSearchInCurrentTab(text);
            }
        });
    }
}
