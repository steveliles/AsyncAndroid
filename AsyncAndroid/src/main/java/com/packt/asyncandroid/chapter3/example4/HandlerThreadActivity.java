package com.packt.asyncandroid.chapter3.example4;

import android.app.Activity;
import android.os.*;
import android.os.Process;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.packt.asyncandroid.R;

import java.math.BigInteger;
import java.util.Random;

/**
 * Illustrates coordinating operations between a background thread
 * and the main thread using Handlers.
 *
 * The background thread has its own Looper because it is a HandlerThread,
 * so we can easily attach a Handler to it and post Runnable's or send
 * Messages. In this example we only send Messages.
 *
 * In order to update the user-interface with the result of work done
 * on the background HandlerThread we give it a reference to the main
 * thread's Handler, and send message's to the main thread as required.
 *
 * Note that the background HandlerThread is kept as a static reference
 * which means that unless we shut it down and nullify it in a lifecycle
 * method it will survive across Activity restarts. We're using that
 * deliberately here, so that work continues in the background during
 * a device rotation (try tapping the button repeatedly and then rotating).
 *
 * In onPause we check to see if the Activity is finishing and explicitly
 * clean up.
 */
public class HandlerThreadActivity extends Activity {

    private static final int CALCULATE_PRIME = "calculate_prime".hashCode();
    private static final int DISPLAY_PRIME = "display_prime".hashCode();

    private static class CalculatorCallback implements Handler.Callback {
        private Handler displayHandler;

        public void setDisplayHandler(Handler displayHandler) {
            this.displayHandler = displayHandler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == CALCULATE_PRIME) {
                BigInteger prime = new BigInteger("2");
                int primeToFind = (Integer) msg.obj;
                for (int i=0; i<primeToFind; i++) {
                    prime = prime.nextProbablePrime();
                }
                displayHandler.sendMessage(
                     Message.obtain(displayHandler, DISPLAY_PRIME, prime));
                return true;
            }
            return false;
        }
    }

    private static class DisplayCallback implements Handler.Callback {
        private LinearLayout resultsView;

        public DisplayCallback(LinearLayout resultsView) {
            this.resultsView = resultsView;
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == DISPLAY_PRIME) {
                // create a new text view, set its text, and add it to
                // the list of results so far.
                TextView resultView = new TextView(resultsView.getContext());
                resultView.setText(msg.obj.toString());
                resultView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                resultsView.addView(resultView, 0);
            }
            return false;
        }
    }

    private static Random random = new Random();
    private static HandlerThread background;
    private static Handler calculator;
    private static CalculatorCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch3_example4_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinearLayout resultsView = (LinearLayout) findViewById(R.id.results);

        // create a Handler with the main thread's Looper (so that all
        // work submitted to this handler executes on the main thread)
        // and the display callback that knows how to handle DISPLAY_PRIME
        // messages.
        Handler displayHandler = new Handler(new DisplayCallback(resultsView));

        if (background == null) {
            // create a background HandlerThread with a callback that knows
            // how to handle CALCULATE_PRIME messages.
            background = new HandlerThread("background",Process.THREAD_PRIORITY_BACKGROUND);

            // if we find our thread isn't getting enough cpu time and things
            // are running slowly we might be falling victim to the 5% throttle
            // imposed by the bg_non_interactive cgroup when threads in other
            // cgroups are busy. We could improve the situation by setting the
            // priority to just slightly more favorable, which gets us out of
            // the bg_non_interactive cgroup.
            //
            // background = new HandlerThread("background",
            //     Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);

            background.start();

            callback = new CalculatorCallback();
            calculator = new Handler(background.getLooper(), callback);
        }

        // Give the callback an up-to-date reference to the displayHandler
        // so that it can send message's back to the main thread to update
        // the user-interface.
        callback.setDisplayHandler(displayHandler);

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int primeToFind = random.nextInt(500) + 250;
                calculator.sendMessage(
                    Message.obtain(calculator, CALCULATE_PRIME, primeToFind));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            calculator.removeMessages(CALCULATE_PRIME);
            background.quit();
            calculator = null;
            background = null;
        }
    }
}
