package com.example.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.entity.SearchResult
import com.example.playlistmaker.domain.search.entity.Track
import com.example.playlistmaker.domain.search.entity.TrackSearchHistory
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private var searchText: String = TEXT_VALUE

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, TEXT_VALUE)
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TEXT_VALUE = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private val searchRunnable = Runnable { enterSearch() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // восстанавливаем сохраненное значение
        searchText = savedInstanceState.getString(INPUT_TEXT, TEXT_VALUE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)

        trackAdapter = TrackAdapter { track -> viewModel.onSearchTrackClicked(track) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = trackAdapter

        trackHistoryAdapter = TrackAdapter { track -> viewModel.onHistoryTrackClicked(track) }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = trackHistoryAdapter

        viewModel.getOpenMediaPlayerTrigger().observe(this) { track ->
            openAudioPlayer(track)
        }
        viewModel.searchResultLiveData()
            .observe(this) { searchResult -> renderSearchResult(searchResult) }
        viewModel.searchHistoryLiveData().observe(this) { trackHistory ->
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

        // переход на главный экран
        binding.toolbar.setNavigationOnClickListener {
            finish()
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
            val view: View? = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
            binding.inputEditText.clearFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                if (!s.isNullOrEmpty()) {
                    showHistory(false)
                }
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
        if (clickDebounce()) {
            val displayIntent = Intent(this, AudioPlayerActivity::class.java)
            displayIntent.putExtra("trackId", track.trackId)
            startActivity(displayIntent)
        }
    }


    private fun enterSearch() {
        viewModel.searchTracks(binding.inputEditText.text.toString())
    }

    private fun renderSearchResult(result: SearchResult) {
        when (result) {
            is SearchResult.Error -> {
                val message = getString(R.string.something_went_wrong)
                setMessage(message)
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.updateBtn.visibility = View.VISIBLE
                binding.notFoundImage.visibility = View.GONE
                binding.wentWrongImage.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.VISIBLE
            }

            is SearchResult.Content -> {
                setMessage("")
                trackAdapter.setItems(result.results)
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.visibility = View.GONE
                binding.notFoundImage.visibility = View.GONE
                binding.wentWrongImage.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.VISIBLE
            }

            SearchResult.Loading -> {
                setMessage("")

                binding.updateBtn.visibility = View.GONE
                binding.notFoundImage.visibility = View.GONE
                binding.wentWrongImage.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.historyRecyclerView.visibility = View.GONE
            }

            SearchResult.NotFound -> {
                setMessage(getString(R.string.nothing_found))
                trackAdapter.setItems(listOf())
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.visibility = View.GONE
                binding.notFoundImage.visibility = View.VISIBLE
                binding.wentWrongImage.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.VISIBLE
            }

            SearchResult.Empty -> {
                setMessage("")
                trackAdapter.setItems(listOf())
                trackAdapter.notifyDataSetChanged()

                binding.updateBtn.visibility = View.GONE
                binding.notFoundImage.visibility = View.GONE
                binding.wentWrongImage.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setMessage(text: String) {
        if (text.isNotEmpty()) {
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.placeholderMessage.text = text
        } else {
            binding.placeholderMessage.visibility = View.GONE
        }
    }

    private fun showHistory(show: Boolean) {
        binding.historyMessage.visibility = uiElementsVisibility(show)
        binding.historyScrollView.visibility = uiElementsVisibility(show)
    }

    private fun uiElementsVisibility(s: Boolean): Int {
        return if (s) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}