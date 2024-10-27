package com.example.planlekcji.fragments.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.utils.BoyerMooreSearch;
import com.example.planlekcji.utils.DelayedSearchTextWatcher;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementsFragment extends Fragment {
    private View view;
    private List<String> replacements;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_replacements, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        observeAndHandleReplacementsLiveData();
        mainViewModel.fetchReplacements();

        setEventListenerToSearchBar();

        return view;
    }

    private void observeAndHandleReplacementsLiveData() {
        mainViewModel.getReplacementsLiveData().observe(getViewLifecycleOwner(), newReplacements -> {
            replacements = newReplacements;

            setReplacements();
        });
    }

    private void setEventListenerToSearchBar() {
        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        TextView textView_noResults = view.findViewById(R.id.textView_noResults);

        searchBar.addTextChangedListener(new DelayedSearchTextWatcher(query -> {
            if(query.isEmpty()) {
                setReplacements();
            } else {
                String foundData = searchReplacements(query);
                if(foundData.isEmpty()) {
                    textView_noResults.setVisibility(View.VISIBLE);
                    setReplacements();
                } else {
                    textView_noResults.setVisibility(View.GONE);
                    setReplacements(foundData);
                }
            }
        }));
    }

    private void setReplacements() {
        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        EditText searchBar = view.findViewById(R.id.editText_searchBar);

        if(replacements == null || replacements.isEmpty()) {
            searchBar.setVisibility(View.GONE);
            textFieldReplacements.setText(getString(R.string.no_replacements));
        } else {
            searchBar.setVisibility(View.VISIBLE);
            textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", replacements), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private void setReplacements(String data) {
        if (data == null) return;

        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        textFieldReplacements.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
    }

    private String searchReplacements(String searchingKey) {
        if(searchingKey.isEmpty()) return "";

        // in case user types '4ptn' instead of '4 ptn'
        searchingKey = handleOtherUserInput(searchingKey).toLowerCase();

        BoyerMooreSearch boyerMooreSearch = new BoyerMooreSearch();

        // searching
        StringBuilder foundResults = new StringBuilder();
        for (int i = 1; i < replacements.size(); i++) {
            String singleReplacement = replacements.get(i);

            String text = singleReplacement.toLowerCase();

            if(boyerMooreSearch.search(text, searchingKey)) {
                if(foundResults.length() == 0) foundResults.append(singleReplacement);
                else foundResults.append("<br><br>").append(singleReplacement);
            }
        }
        return foundResults.toString();
    }

    private String handleOtherUserInput(String searchingKey) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(searchingKey.substring(0, 1));

        if(!searchingKey.contains(" ") && searchingKey.length() > 1 && matcher.find()) {
            searchingKey = searchingKey.charAt(0)+" "+searchingKey.substring(1);
        }

        return searchingKey;
    }


    private String updateSearchBar() {
        SharedPreferences sharedPref = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);
        String searchKey = sharedPref.getString(getString(R.string.searchKey), "");

        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        searchBar.setText(searchKey);

        return searchKey;
    }
}