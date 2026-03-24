package com.example.dictionary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionary.databinding.ActivityDictionaryBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class Dictionary : AppCompatActivity() {
    private lateinit var binding: ActivityDictionaryBinding
    private lateinit var adapter: MeaningAdapter

    private val speechRecognizerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultData = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val word = resultData?.get(0) ?: ""
                binding.searchInput.setText(word)
                getMeaning(word)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        binding.mic1.setOnClickListener {
            startVoiceSearch()
        }

        // When touching search bar, reset UI components
        binding.searchInput.setOnClickListener {
            binding.meaningRecyclerView.visibility = View.GONE
            binding.wordTextview.visibility = View.GONE
            binding.phoneticTextview.visibility = View.GONE
            binding.inImage.visibility = View.VISIBLE
            binding.searchBtn.visibility = View.VISIBLE
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter
    }

    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the word to search meaning")

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMeaning(word: String) {
        if (word.isEmpty()) return
        
        setInProgress(true)
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                runOnUiThread {
                    setInProgress(false)
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.first()?.let {
                            setUI(it)
                        }
                    } else {
                        Toast.makeText(applicationContext, "Word not found", Toast.LENGTH_SHORT).show()
                        // Reset to initial state if word not found
                        binding.inImage.visibility = View.VISIBLE
                        binding.searchBtn.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    setInProgress(false)
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    binding.inImage.visibility = View.VISIBLE
                    binding.searchBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
        
        // After search completed: RecyclerView visible, Image hidden, SearchBtn visible
        binding.wordTextview.visibility = View.VISIBLE
        binding.phoneticTextview.visibility = View.VISIBLE
        binding.meaningRecyclerView.visibility = View.VISIBLE
        binding.inImage.visibility = View.GONE
        binding.searchBtn.visibility = View.GONE
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            // During progress: SearchBtn hidden, ProgressBar visible, Image visible, Recycler hidden
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.inImage.visibility = View.VISIBLE
            binding.meaningRecyclerView.visibility = View.GONE
            binding.wordTextview.visibility = View.GONE
            binding.phoneticTextview.visibility = View.GONE
        } else {
            // Progress finished: ProgressBar hidden
            binding.progressBar.visibility = View.GONE


        }
    }
}
