package com.example.practico2

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Color

class Snake(initialSize: Int) {  // Start size
    private val snakeBody: MutableList<Point> = mutableListOf()
    private val blockSize = 50

    init {
        // Initialize the snake with the specified size, starting at (5, 5)
        for (i in 0 until initialSize) {
            snakeBody.add(Point(5 - i, 5))  // Start snake horizontally at (5, 5)
        }
    }

    fun move(direction: Direction) {
        val head = snakeBody.first()
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }
        snakeBody.add(0, newHead)  // Add new head to the front
        snakeBody.removeLast()     // Remove the last segment to simulate movement
    }

    fun grow() {
        val tail = snakeBody.last()
        snakeBody.add(Point(tail.x, tail.y))  // Add a new segment at the tail's position
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
        for (segment in snakeBody) {
            canvas.drawRect(
                segment.x * blockSize.toFloat(),
                segment.y * blockSize.toFloat(),
                (segment.x + 1) * blockSize.toFloat(),
                (segment.y + 1) * blockSize.toFloat(),
                paint
            )
        }
    }

    fun getHead(): Point {
        return snakeBody.first()  // Head is the first segment in the list
    }

    fun hasSelfCollision(): Boolean {
        val head = getHead()
        return snakeBody.drop(1).contains(head)  // Check if head collides with any other body part
    }
}


enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
