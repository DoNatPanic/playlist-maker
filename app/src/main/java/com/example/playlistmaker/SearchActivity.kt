package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACKS_HISTORY = "tracks_history"

class SearchActivity : AppCompatActivity() {

    private val trackList: MutableList<Track> = mutableListOf()
    private var trackHistoryList: MutableList<Track> = mutableListOf()

    private val trackAdapter = TrackAdapter(trackList, this)
    private val trackHistoryAdapter = TrackAdapter(trackHistoryList, this)
    private lateinit var searchHistory: SearchHistory

    private lateinit var backBtn: MaterialToolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var wentWrongImage: ImageView
    private lateinit var notFoundImage: ImageView
    private lateinit var updateBtn: MaterialButton
    private lateinit var historyMessage: TextView
    private lateinit var clearHistoryBtn: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyScrollView: NestedScrollView

    private var searchText: String = TEXT_VALUE

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesSearchAPI::class.java)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, TEXT_VALUE)
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TEXT_VALUE = ""
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // восстанавливаем сохраненное значение
        searchText = savedInstanceState.getString(INPUT_TEXT, TEXT_VALUE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backBtn = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        notFoundImage = findViewById(R.id.notFoundImage)
        wentWrongImage = findViewById(R.id.wentWrongImage)
        updateBtn = findViewById(R.id.updateBtn)
        historyMessage = findViewById(R.id.historyMessage)
        clearHistoryBtn = findViewById(R.id.clearHistoryBtn)
        recyclerView = findViewById(R.id.recyclerView)
        historyScrollView = findViewById(R.id.historyScrollView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = trackHistoryAdapter

        val sharedPreferences = getSharedPreferences(TRACKS_HISTORY, MODE_PRIVATE)

        searchHistory = SearchHistory(sharedPreferences)

        // восстановленное значение глобальной переменной выводим в inputEditText
        inputEditText.setText(searchText)

        // переход на главный экран
        backBtn.setNavigationOnClickListener {
            finish()
        }

        // очистить историю
        clearHistoryBtn.setOnClickListener {
            trackHistoryList.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistory.saveToShared(trackHistoryList)
            showHistory(false)
        }

        // считать историю поиска
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isNullOrEmpty()) {

                // читаем историю из SP если trackHistoryList пуст
                if (trackHistoryList.isEmpty()) {
                    trackHistoryList = searchHistory.readHistory()
                    trackHistoryAdapter.tracks = trackHistoryList
                    trackHistoryAdapter.notifyDataSetChanged()
                }

                // список не пуст? - показать историю
                showHistory(trackHistoryList.isNotEmpty())
            } else {
                //строка ввода не в фокусе? - скрыть историю
                showHistory(false)
            }
        }

        // нажатие на кнопку Done на клавиатуре
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputEditText.clearFocus()
                enterSearch()
                true
            }
            false
        }

        // нажатие на кнопку Обновить
        updateBtn.setOnClickListener {
            enterSearch()
        }

        // очистить строку поиска
        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            clearAllPlaceholders()
            val view: View? = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
            inputEditText.clearFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (!s.isNullOrEmpty()) {
                    showHistory(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = inputEditText.text.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    // меняем список истории, когда пользователь нажимает на какой-либо элемент
    fun rebuildTrackHistoryList(position: Int) {
        if (!inputEditText.text.isNullOrEmpty()) {
            iTunesListSelection(position)
            openAudioPlayer(trackList[position])
        } else {
            historyListSelection(position)
            openAudioPlayer(trackHistoryList[position])
        }
    }

    // перейти на экран аудиоплеера
    private fun openAudioPlayer(track: Track) {
        val displayIntent = Intent(this, AudioPlayerActivity::class.java)
        displayIntent.putExtra("trackName", track.trackName)
        displayIntent.putExtra("artistName", track.artistName)
        displayIntent.putExtra("trackTimeMillis", track.trackTime)
        displayIntent.putExtra("artworkUrl100", track.artworkUrl100)
        displayIntent.putExtra("collectionName", track.collectionName)
        displayIntent.putExtra("releaseDate", track.releaseDate)
        displayIntent.putExtra("primaryGenreName", track.primaryGenreName)
        displayIntent.putExtra("country", track.country)
        startActivity(displayIntent)
    }

    // пользователь выбрал песню из списка
    private fun iTunesListSelection(position: Int) {
        if (trackHistoryList.size > 0) {
            var isExist = false
            var existElement: Track? = null
            trackHistoryList.forEach { element ->
                // выбранный трек уже есть в истории - замоминаем его
                if (element.trackId == trackList[position].trackId) {
                    isExist = true
                    existElement = element
                }
            }
            // удаляем его из  списка истории
            if (isExist) {
                trackHistoryList.remove(existElement)
            }
            if (trackHistoryList.size >= 10) {
                trackHistoryList.removeLast()
            }
        }
        // и добавляем его в начало списка
        trackHistoryList.add(0, trackList[position])
        trackHistoryAdapter.notifyDataSetChanged()
        searchHistory.saveToShared(trackHistoryList)
    }

    // отображается история и пользователь нажал на трек истории
    private fun historyListSelection(position: Int) {
        if (trackHistoryList.size > 1) {
            // перестроили список
            val element = trackHistoryList[position]
            trackHistoryList.removeAt(position)
            trackHistoryList.add(0, element)
            trackHistoryAdapter.notifyDataSetChanged()
            // сохранили его в SP
            searchHistory.saveToShared(trackHistoryList)
        }
    }

    private fun enterSearch() {
        if (inputEditText.text.isNotEmpty()) {
            iTunesService.search(inputEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        wentWrongImage.visibility = View.GONE
                        updateBtn.visibility = View.GONE
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found))
                            notFoundImage.visibility = View.VISIBLE
                        } else {
                            showMessage("")
                            notFoundImage.visibility = View.GONE
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong))
                        wentWrongImage.visibility = View.VISIBLE
                        updateBtn.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showMessage(
                        getString(R.string.something_went_wrong),
                    )
                    wentWrongImage.visibility = View.VISIBLE
                    updateBtn.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text

        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    private fun showHistory(show: Boolean) {
        historyMessage.visibility = uiElementsVisibility(show)
        historyScrollView.visibility = uiElementsVisibility(show)
    }

    private fun clearAllPlaceholders() {
        placeholderMessage.visibility = View.GONE
        wentWrongImage.visibility = View.GONE
        notFoundImage.visibility = View.GONE
        updateBtn.visibility = View.GONE
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