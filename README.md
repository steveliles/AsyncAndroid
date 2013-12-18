Asynchronous Android
====================

This project contains the example code for the book "Asynchronous Android" (ISBN 978-1-78328-687-4).

Check out [the book](http://steveliles.github.io/asynchronous_android.html) for detailed coverage and explanation of all of the constructs presented in these examples. You can easily run the examples on your own device - either build this project or get [the app](https://play.google.com/store/apps/details?id=com.packt.asyncandroid) from Google Play.

This project has been developed with Android Studio with the Gradle build system and the Android 4.3 platform. All of the examples should run fine on Android devices running 2.1 upwards.

The examples are arranged in a single app, and grouped by chapter as follows:

Chapter 1 - The Process Model
-----------------------------
In chapter 1 we learn about Android's single threaded event loop and the main/ui thread. The examples show some of the bad things that can happen when you do too much work on the main thread.

Chapter 2 - Staying responsive with AsyncTask
---------------------
Covers the poster-child of concurrent programming in Android. We learn how AsyncTask works, how to use it correctly, and how to avoid the common pitfalls that catch out even experienced developers.
 
Chapter 3 - Distributing work with Handler, HandlerThread, and Looper
----------------------------------------------
Details the fundamental and related topics of Handler, HandlerThread, and Looper, and illustrates their use to schedule tasks on the main thread, and to coordinate and communicate work between cooperating background threads.
 
Chapter 4 - Asynchronous IO with Loader
---------------------------------------
Introduces the Loader framework and tackles the important task of loading data asynchronously to keep the user-interface responsive and glitch-free.
 
Chapter 5 - Queuing work with IntentService
-------------------------------------------
Gives us the means to perform background operations beyond the scope of a single Activity lifecycle, and to ensure that our work is completed even if the user leaves the application.
 
Chapter 6 - Long-running tasks with Service
-------------------------------------------
Extends the capabilities we discovered with IntentService, and gives us control over the level of concurrency applied to our long-running background tasks.
 
Chapter 7 - Scheduling work with AlarmManager
---------------------------------------------
Completes our toolkit, enabling us to arrange for work to be done far in the future and on repeating schedules, to build apps that alert users to new content and start instantly with fresh data.
