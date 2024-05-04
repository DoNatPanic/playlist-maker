package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class SearchActivity : AppCompatActivity() {

    private val trackList: MutableList<Track> = mutableListOf()

    private val trackAdapter = TrackAdapter(trackList)

    private lateinit var backBtn: com.google.android.material.appbar.MaterialToolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var wentWrongImage: ImageView
    private lateinit var notFoundImage: ImageView
    private lateinit var updateBtn: MaterialButton

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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        backBtn = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        notFoundImage = findViewById(R.id.notFoundImage)
        wentWrongImage = findViewById(R.id.wentWrongImage)
        updateBtn = findViewById(R.id.updateBtn)

        // восстановленное значение глобальной переменной выводим в inputEditText
        inputEditText.setText(searchText)

        // переход на главный экран
        backBtn.setNavigationOnClickListener {
            finish()
        }

        // нажатие на кнопку Done на клавиатуре
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                enterSearch()
                true
            }
            false
        }

        // нажатие на кнопку обновить
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
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = inputEditText.text.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
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

    private fun clearAllPlaceholders(){
        placeholderMessage.visibility = View.GONE
        wentWrongImage.visibility = View.GONE
        notFoundImage.visibility = View.GONE
        updateBtn.visibility = View.GONE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}