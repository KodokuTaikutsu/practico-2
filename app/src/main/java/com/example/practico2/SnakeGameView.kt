package com.example.practico2

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

class SnakeGameView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), Runnable {

    private var thread: Thread? = null
    private var running = false
    private var snake: Snake = Snake(5) // Snake starts with size 3
    private var direction: Direction = Direction.RIGHT
    private var food: Point? = null  // Initialize later after screen size is set
    private var screenWidth = 0
    private var screenHeight = 0
    private val blockSize = 50 // Size of snake body part and food
    private val delay = 150L // 40% slower (150 ms delay between frames)

    override fun run() {
        while (running) {
            if (holder.surface.isValid) {
                val canvas = holder.lockCanvas()
                screenWidth = canvas.width
                screenHeight = canvas.height

                // Generate food once screenWidth and screenHeight are set
                if (food == null) {
                    food = generateRandomFood()
                }

                update()
                drawGame(canvas)
                holder.unlockCanvasAndPost(canvas)

                // Add delay to slow down the snake's movement (40% slower)
                try {
                    Thread.sleep(delay)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun update() {
        snake.move(direction)

        // Check if the snake goes off-screen and wrap around the borders
        wrapSnakeAroundBorders()

        // Check for collisions
        checkFoodCollision()
        checkSelfCollision()
    }

    private fun wrapSnakeAroundBorders() {
        val head = snake.getHead()

        // Wrap around logic
        if (head.x < 0) head.x = screenWidth / blockSize - 1 // Left border to right
        if (head.x >= screenWidth / blockSize) head.x = 0 // Right border to left
        if (head.y < 0) head.y = screenHeight / blockSize - 1 // Top border to bottom
        if (head.y >= screenHeight / blockSize) head.y = 0 // Bottom border to top
    }

    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        snake.draw(canvas)
        drawFood(canvas)
    }

    private fun drawFood(canvas: Canvas) {
        food?.let {
            val paint = Paint().apply {
                color = Color.RED
                style = Paint.Style.FILL
            }
            canvas.drawRect(
                it.x * blockSize.toFloat(),
                it.y * blockSize.toFloat(),
                (it.x + 1) * blockSize.toFloat(),
                (it.y + 1) * blockSize.toFloat(),
                paint
            )
        }
    }

    private fun checkFoodCollision() {
        if (snake.getHead() == food) {
            snake.grow()  // Snake grows when it eats food
            food = generateRandomFood()  // Generate new food at a random position
        }
    }

    private fun checkSelfCollision() {
        if (snake.hasSelfCollision()) {
            // Stop the game and show Game Over popup
            running = false
            (context as AppCompatActivity).runOnUiThread {
                showGameOverDialog()
            }
        }
    }

    private fun generateRandomFood(): Point {
        val maxX = if (screenWidth > 0) screenWidth / blockSize else 1
        val maxY = if (screenHeight > 0) screenHeight / blockSize else 1
        return Point((0 until maxX).random(), (0 until maxY).random())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                // Prevent reversing the direction
                when {
                    y < screenHeight / 4 && direction != Direction.DOWN -> direction = Direction.UP
                    y > screenHeight * 3 / 4 && direction != Direction.UP -> direction = Direction.DOWN
                    x < screenWidth / 2 && direction != Direction.RIGHT -> direction = Direction.LEFT
                    x > screenWidth / 2 && direction != Direction.LEFT -> direction = Direction.RIGHT
                }
            }
        }
        return true
    }

    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Game Over")
        builder.setMessage("You collided with yourself! Would you like to restart the game?")
        builder.setPositiveButton("Restart") { _, _ ->
            resetGame()
        }
        builder.setNegativeButton("Exit") { _, _ ->
            (context as AppCompatActivity).finish()  // Exit the activity
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun resetGame() {
        // Reset snake and other game variables
        snake = Snake(3)
        direction = Direction.RIGHT
        food = generateRandomFood()

        // Resume the game
        resume()
    }

    fun pause() {
        running = false
        thread?.join()
    }

    fun resume() {
        running = true
        thread = Thread(this)
        thread?.start()
    }
}
