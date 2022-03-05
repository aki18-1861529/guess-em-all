package edu.us.ischool.guessemall

import android.app.Application
import android.util.Log

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("QuizApp", "QuizApp is loaded and running!")
    }


    companion object {
        val data = DataRepository()
    }
}