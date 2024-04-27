package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.Note
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.helper.ViewModelFactory
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var viewAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var currentImageUri: Uri? = null

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            startCrop(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewManager = LinearLayoutManager(this)
        viewAdapter = NewsAdapter(listOf())
        val article = Article(binding.recyclerView, this, viewAdapter)
        article.getNews()

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        setSupportActionBar(binding.toolbar)

        val factory = ViewModelFactory.getInstance(this.application)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        imageClassifierHelper = ImageClassifierHelper(this, object : ImageClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
                showToast(error)
            }

            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                results?.let { classifications ->
                    currentImageUri?.let { uri ->
                        moveToResult(classifications)
                        autosaveResult(uri, classifications)
                    }
                }
            }
        })

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { uri ->
                analyzeImage(uri)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                val historyIntent = Intent(this, HistoryActivity::class.java)
                startActivity(historyIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun startCrop(uri: Uri) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_$timestamp"))
        val options = UCrop.Options().apply {
            setToolbarTitle("Edit Image")
            setFreeStyleCropEnabled(true)
        }
        cropActivityResultLauncher.launch(
            UCrop.of(uri, destinationUri)
                .withOptions(options)
                .getIntent(this@MainActivity)
        )
    }

    private val cropActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                currentImageUri = it
                showImage(it)
            } ?: showToast("Error retrieving cropped image")
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            showToast(cropError?.message ?: "Unknown error")
        }
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(null)
        binding.previewImageView.setImageURI(uri)
    }

    private fun analyzeImage(uri: Uri) {
        imageClassifierHelper.classifyStaticImage(uri)
    }

    private fun moveToResult(classifications: List<Classifications>) {
        val resultIntent = Intent(this, ResultActivity::class.java).apply {
            val classification = classifications.first()
            val label = classification.categories.first().label
            val confidence = classification.categories.first().score
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_PREDICTION, label)
            putExtra(ResultActivity.EXTRA_CONFIDENCE, confidence)
        }
        startActivity(resultIntent)
    }

    private fun autosaveResult(uri: Uri, classifications: List<Classifications>) {
        val classification = classifications.first()
        val label = classification.categories.first().label
        val confidence = classification.categories.first().score
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val uriString = uri.toString()
        val note = Note(imageUri = uriString, prediction = label, confidenceScore = confidence, timestamp = timestamp)
        historyViewModel.insert(note)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
