package com.example.networkapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest

import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    companion object {
        private const val STATE_COMIC_TITLE = "comic_title"
        private const val STATE_COMIC_DESCRIPTION = "comic_description"
        private const val STATE_COMIC_IMAGE_URL = "comic_image_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        /*if (savedInstanceState != null) {
            titleTextView.text = savedInstanceState.getString(STATE_COMIC_TITLE)
            descriptionTextView.text = savedInstanceState.getString(STATE_COMIC_DESCRIPTION)
            val imageUrl = savedInstanceState.getString(STATE_COMIC_IMAGE_URL)
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(comicImageView)
                comicImageView.tag = imageUrl
            }
        }else{
            loadComicDataFromPreferences()
        }*/


        if(intent?.action == Intent.ACTION_VIEW){
            intent.data?.path?.run {
                Log.d("Comic Number",split("/")[0])
            }
        }

        findViewById<Button>(R.id.button).setOnClickListener{
            val intent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:${packageName}")
            )
            startActivity()



        }
    }

    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url, { showComic(it) }, {
            })
        )
    }



    private fun showComic(comicObject: JSONObject) {
        val title = comicObject.getString("title")
        val description = comicObject.getString("alt")
        val imageUrl = comicObject.getString("img")

        titleTextView.text = title
        descriptionTextView.text = description
        Picasso.get().load(imageUrl).into(comicImageView)
        comicImageView.tag = imageUrl

        saveComicDataToPreferences(title, description, imageUrl)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_COMIC_TITLE, titleTextView.text.toString())
        outState.putString(STATE_COMIC_DESCRIPTION, descriptionTextView.text.toString())
        outState.putString(STATE_COMIC_IMAGE_URL, comicImageView.tag as? String)
    }

    private fun saveComicDataToPreferences(title: String, description: String, imageUrl: String) {
        val sharedPreferences = getSharedPreferences("com.example.networkapp", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(STATE_COMIC_TITLE, title)
            putString(STATE_COMIC_DESCRIPTION, description)
            putString(STATE_COMIC_IMAGE_URL, imageUrl)
            apply()
        }
    }

    private fun loadComicDataFromPreferences() {
        val sharedPreferences = getSharedPreferences("com.example.networkapp", MODE_PRIVATE)
        titleTextView.text = sharedPreferences.getString(STATE_COMIC_TITLE, "")
        descriptionTextView.text = sharedPreferences.getString(STATE_COMIC_DESCRIPTION, "")
        val imageUrl = sharedPreferences.getString(STATE_COMIC_IMAGE_URL, null)
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(comicImageView)
            comicImageView.tag = imageUrl
        }
    }


}
