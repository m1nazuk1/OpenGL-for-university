package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class SnakeGame {

    private long window;
    private float x = 0, y = 0;
    private float dx = 0, dy = 0;

    private float foodX, foodY;
    private int score = 0;
    private float speed = 0.002f;
    private float snakeSize = 0.03f;


    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("инициализация не удалась");
        }

        window = glfwCreateWindow(800, 600, "Snake Game", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("создать окно не удалось");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_W -> { dy = speed; dx = 0; }
                    case GLFW_KEY_S -> { dy = -speed; dx = 0; }
                    case GLFW_KEY_A -> { dx = -speed; dy = 0; }
                    case GLFW_KEY_D -> { dx = speed; dy = 0; }
                }
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        generateFood();
    }

    private void loop() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            x += dx;
            y += dy;

            if (x > 1) x = -1;
            else if (x < -1) x = 1;
            if (y > 1) y = -1;
            else if (y < -1) y = 1;

            drawSnake();
            drawFood();
            checkFoodCollision();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }

    private void generateFood() {
        foodX = (float) Math.random() * 2 - 1;
        foodY = (float) Math.random() * 2 - 1;
    }

    private void checkFoodCollision() {
        if (Math.abs(x - foodX) < snakeSize && Math.abs(y - foodY) < snakeSize) {
            score += 10;
            speed += 0.0005f;
            snakeSize += 0.01f;
            generateFood();
        }
    }

    private void drawSnake() {
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_QUADS);
        glVertex2f(x - snakeSize, y - snakeSize);
        glVertex2f(x + snakeSize, y - snakeSize);
        glVertex2f(x + snakeSize, y + snakeSize);
        glVertex2f(x - snakeSize, y + snakeSize);
        glEnd();
    }


    private void drawFood() {
        glColor3f(1.0f, 0.0f, 0.0f);
        drawCircle(foodX, foodY, 0.03f);
    }

    private void drawCircle(float cx, float cy, float radius) {
        glBegin(GL_TRIANGLE_FAN);
        for (int i = 0; i <= 360; i++) {
            float angle = (float) (i * Math.PI / 180);
            glVertex2f((float) Math.cos(angle) * radius + cx, (float) Math.sin(angle) * radius + cy);
        }
        glEnd();
    }


    public static void main(String[] args) {
        new SnakeGame().run();
    }
}
