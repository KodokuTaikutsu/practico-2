package com.example.practico2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var snakeGameView: SnakeGameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snakeGameView = findViewById(R.id.snakeGameView)
    }

    override fun onPause() {
        super.onPause()
        snakeGameView.pause()
    }

    override fun onResume() {
        super.onResume()
        snakeGameView.resume()
    }
}
