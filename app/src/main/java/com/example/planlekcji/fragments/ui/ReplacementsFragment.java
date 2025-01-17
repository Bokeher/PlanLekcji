package com.example.planlekcji.fragments.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.util.Log;
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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ReplacementsFragment extends Fragment {
    private View view;
    private List<String> replacements;
    private MainViewModel mainViewModel;

    // used for searching
    private List<String> replacementsWithoutHtml;
    private HashSet<Integer> replacementIds;

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

            // Prepare replacements without html for searching
            replacementsWithoutHtml = replacements.stream()
                    .map(s -> s.replaceAll("<[^>]*>", "").replaceAll("\\s+", " "))
                    .collect(Collectors.toList());

            setReplacements();
        });
    }

    private void setEventListenerToSearchBar() {
        EditText searchBar = view.findViewById(R.id.editText_searchBar);


        searchBar.addTextChangedListener(new DelayedSearchTextWatcher(query -> {
            replacementIds = searchReplacements(query.toLowerCase());

            setReplacements();
        }));
    }

    private void setReplacements() {
        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        View divider = view.findViewById(R.id.divider);
        TextView textView_noResults = view.findViewById(R.id.textView_noResults);

        if(replacements == null || replacements.isEmpty()) {
            searchBar.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);

            textFieldReplacements.setText(getString(R.string.no_replacements));
        } else {
            searchBar.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);

            if (replacementIds == null || replacementIds.isEmpty()) {
                textView_noResults.setVisibility(replacementIds == null ? View.GONE : View.VISIBLE);
                textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", replacements), Html.FROM_HTML_MODE_LEGACY));
                return;
            }

            List<String> filteredReplacements = replacements.stream()
                    .filter(rep -> replacementIds.contains(replacements.indexOf(rep)))
                    .collect(Collectors.toList());

            textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", filteredReplacements), Html.FROM_HTML_MODE_LEGACY));
            textView_noResults.setVisibility(View.GONE);

        }
    }

    private void setReplacements(String data) {
        if (data == null) return;

        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        textFieldReplacements.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
    }

    private HashSet<Integer> searchReplacements(String searchingKey) {
        if (searchingKey.isEmpty()) return null;

        BoyerMooreSearch boyerMooreSearch = new BoyerMooreSearch();

        // searching
        HashSet<Integer> replacementIds = new HashSet<>();
        for (int i = 0; i < replacementsWithoutHtml.size(); i++) {
            String replacement = replacementsWithoutHtml.get(i);

            if (boyerMooreSearch.search(replacement.toLowerCase(), searchingKey)) {
                replacementIds.add(i);
            }

        }

        return replacementIds;
    }

    private String updateSearchBar() {
        SharedPreferences sharedPref = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);
        String searchKey = sharedPref.getString(getString(R.string.searchKey), "");

        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        searchBar.setText(searchKey);

        return searchKey;
    }
}