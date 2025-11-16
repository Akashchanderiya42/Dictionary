package com.example.dictionary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionary.databinding.ActivityDictionaryBinding
import com.example.dictionary.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import java.util.Locale

class Dictionary : AppCompatActivity() {
    private lateinit var binding: ActivityDictionaryBinding
    private lateinit var apiInterface: DictionaryApi
    lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter


    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if(response.body()==null){
                    throw (Exception())
                }
                runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }
            }catch (e : Exception){
                runOnUiThread{
                    setInProgress(false)
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.inImage.visibility = View.VISIBLE
        } else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.inImage.visibility = View.INVISIBLE

        }
    }
}