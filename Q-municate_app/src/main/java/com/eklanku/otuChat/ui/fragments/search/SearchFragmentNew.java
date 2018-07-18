package com.eklanku.otuChat.ui.fragments.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.eklanku.otuChat.ui.adapters.search.SearchViewPagerAdapter;
import com.eklanku.otuChat.ui.fragments.base.BaseFragment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.adapters.search.SearchViewPagerAdapter;
import com.eklanku.otuChat.ui.fragments.base.BaseFragment;
import com.eklanku.otuChat.utils.KeyboardUtils;

import butterknife.Bind;


public class SearchFragmentNew extends BaseFragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    @Bind(R.id.search_viewpager)
    ViewPager searchViewPager;

    @Bind(R.id.search_radiogroup)
    RadioGroup searchRadioGroup;

    private SearchViewPagerAdapter searchViewPagerAdapter;

    public SearchFragmentNew() {
    }

    public static SearchFragmentNew newInstance() {
        SearchFragmentNew fragment = new SearchFragmentNew();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);

        activateButterKnife(rootView);

        initViewPagerAdapter();
        initCustomListeners();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu_new, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(getActivity());
        searchView.setQueryHint(getString(R.string.action_bar_search_hint));
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchItem.setActionView(searchView);

    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        KeyboardUtils.hideKeyboard(baseActivity);
        search(searchQuery);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        search(searchQuery);
        return true;
    }

    private void initViewPagerAdapter() {
        searchViewPagerAdapter = new SearchViewPagerAdapter(getChildFragmentManager());
        searchViewPager.setAdapter(searchViewPagerAdapter);
        searchViewPager.setOnPageChangeListener(new PageChangeListener());
        searchRadioGroup.check(R.id.local_search_radiobutton);
    }

    private void initCustomListeners() {
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroupListener());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        cancelSearch();
        baseActivity.getSupportFragmentManager().popBackStack();
        return false;
    }

    private void search(String searchQuery) {
        if (searchViewPagerAdapter != null && searchViewPager != null) {
            searchViewPagerAdapter.search(searchViewPager.getCurrentItem(), searchQuery);
        }
    }

    private void cancelSearch() {
        if (searchViewPagerAdapter != null && searchViewPager != null) {
            searchViewPagerAdapter.cancelSearch(searchViewPager.getCurrentItem());
        }
    }

    private class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.local_search_radiobutton:
                    searchViewPager.setCurrentItem(SearchViewPagerAdapter.LOCAl_SEARCH);
                    searchViewPagerAdapter.prepareSearch(SearchViewPagerAdapter.LOCAl_SEARCH);
                    break;
                case R.id.global_search_radiobutton:
                    searchViewPager.setCurrentItem(SearchViewPagerAdapter.GLOBAL_SEARCH);
                    searchViewPagerAdapter.prepareSearch(SearchViewPagerAdapter.GLOBAL_SEARCH);
                    break;
            }
        }
    }

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case SearchViewPagerAdapter.LOCAl_SEARCH:
                    searchRadioGroup.check(R.id.local_search_radiobutton);
                    break;
                case SearchViewPagerAdapter.GLOBAL_SEARCH:
                    searchRadioGroup.check(R.id.global_search_radiobutton);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardUtils.hideKeyboard(getActivity());
    }
}
