package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.common.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TrackSearchHistory
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerFragment
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TEXT_VALUE = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private var searchText: String = TEXT_VALUE

    private var searchJob: Job? = null
    private var job: Job? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, TEXT_VALUE)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onReload()
    }

    private fun clickDebounce() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
        }
    }


    private fun searchDebounce() {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            enterSearch()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // восстанавливаем сохраненное значение
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(
                INPUT_TEXT,
                TEXT_VALUE
            )
        }

        var owner = getViewLifecycleOwner()

        val onSearchTrackClick: (Track) -> Unit =
            { track: Track -> viewModel.onSearchTrackClicked(track) }
        trackAdapter = TrackAdapter(onSearchTrackClick)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = trackAdapter

        val onHistoryTrackClick: (Track) -> Unit =
            { track: Track -> viewModel.onHistoryTrackClicked(track) }
        trackHistoryAdapter = TrackAdapter(onHistoryTrackClick)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = trackHistoryAdapter

        viewModel.getOpenMediaPlayerTrigger().observe(owner) { track ->
            openAudioPlayer(track)
        }
        viewModel.searchResultLiveData()
            .observe(owner) { searchResult: SearchResult -> renderSearchResult(searchResult) }
        viewModel.searchHistoryLiveData().observe(owner) { trackHistory ->
            when (trackHistory) {
                is TrackSearchHistory.Empty -> {
                    trackHistoryAdapter.setItems(listOf())
                    showHistory(false)
                }

                is TrackSearchHistory.Content -> {
                    trackHistoryAdapter.setItems(trackHistory.results)
                }
            }
        }

        // восстановленное значение глобальной переменной выводим в inputEditText
        binding.inputEditText.setText(searchText)

        // очистить историю
        binding.clearHistoryBtn.setOnClickListener {
            viewModel.onClearHistory()
        }

        // считать историю поиска
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isNullOrEmpty()) {
                // список не пуст? - показать историю
                showHistory(trackHistoryAdapter.itemCount != 0)
            } else {
                //строка ввода не в фокусе? - скрыть историю
                showHistory(false)
            }
        }

        // нажатие на кнопку Done на клавиатуре
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.inputEditText.clearFocus()
                enterSearch()
                true
            }
            false
        }

        // нажатие на кнопку Обновить
        binding.updateBtn.setOnClickListener {
            enterSearch()
        }

        // очистить строку поиска
        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            viewModel.clearSearchResults()
            val view: View? = requireActivity().currentFocus

            if (view != null) {
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
            binding.inputEditText.clearFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = clearButtonIsVisible(s)
                if (!s.isNullOrEmpty()) {
                    showHistory(false)
                } else showHistory(true)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = binding.inputEditText.text.toString()
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    // перейти на экран аудиоплеера
    private fun openAudioPlayer(track: Track) {
        clickDebounce()
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    private fun enterSearch() {
        viewModel.searchTracks(binding.inputEditText.text.toString())
    }

    private fun renderSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.Error -> {
                val message = getString(R.string.something_went_wrong)
                setMessage(message)
                binding.placeholderMessage.isVisible = true
                binding.updateBtn.isVisible = true
                binding.notFoundImage.isVisible = false
                binding.wentWrongImage.isVisible = true
                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = true
                binding.historyRecyclerView.isVisible = true
            }

            is SearchResult.TrackContent -> {
                setMessage("")
                trackAdapter.setItems(result.results)
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.isVisible = false
                binding.notFoundImage.isVisible = false
                binding.wentWrongImage.isVisible = false
                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = true
                binding.historyRecyclerView.isVisible = true
            }

            SearchResult.Loading -> {
                setMessage("")

                binding.updateBtn.isVisible = false
                binding.notFoundImage.isVisible = false
                binding.wentWrongImage.isVisible = false
                binding.progressBar.isVisible = true
                binding.recyclerView.isVisible = false
                binding.historyRecyclerView.isVisible = false
            }

            SearchResult.NotFound -> {
                setMessage(getString(R.string.nothing_found))
                trackAdapter.setItems(listOf())
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.isVisible = false
                binding.notFoundImage.isVisible = true
                binding.wentWrongImage.isVisible = false
                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = true
                binding.historyRecyclerView.isVisible = true
            }

            SearchResult.Empty -> {
                setMessage("")
                trackAdapter.setItems(listOf())
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.isVisible = false
                binding.notFoundImage.isVisible = false
                binding.wentWrongImage.isVisible = false
                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = true
                binding.historyRecyclerView.isVisible = true
            }

            else -> {}
        }
    }

    private fun setMessage(text: String) {
        if (text.isNotEmpty()) {
            binding.placeholderMessage.isVisible = true
            binding.placeholderMessage.text = text
        } else {
            binding.placeholderMessage.isVisible = false
        }
    }

    private fun showHistory(show: Boolean) {
        binding.historyMessage.isVisible = uiElementsisVisible(show)
        binding.historyScrollView.isVisible = uiElementsisVisible(show)
    }

    private fun uiElementsisVisible(s: Boolean): Boolean {
        return s
    }

    private fun clearButtonIsVisible(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }
}
