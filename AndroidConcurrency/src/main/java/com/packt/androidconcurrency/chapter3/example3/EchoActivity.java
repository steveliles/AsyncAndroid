package com.packt.androidconcurrency.chapter3.example3;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.packt.androidconcurrency.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Illustrates sending messages from a background thread for
 * handling by the main thread.
 *
 * Notice that we prevent memory leaks by attaching and
 * detaching during lifecycle callbacks so that we can release
 * references to the view hierarchy.
 *
 * Notice also that we are using blocking operations to
 * accept a socket connection and read lines of text from
 * the socket, so we MUST do this in a background thread.
 *
 * These blocking operations would at best make our user-
 * interface unresponsive, and probably cause ANR's if we
 * use them from the main thread (actually if we try to
 * use these particular blocking operations the system will
 * immediately raise a NetworkOnMainThreadException if we're
 * running Honeycomb or newer!)
 *
 * To see the example in action, first start the activity on
 * your device, then open a terminal/command-prompt on your
 * development computer.
 *
 * On your device screen you should see text something like:
 *
 *   "telnet 192.168.0.4 4444"
 *
 * Enter this text at the command-prompt on your desktop machine.
 * The command prompt should then display text something like:
 *
 *   Trying 192.168.0.4...
 *   Connected to 192.168.0.4.
 *   Escape character is '^]'.
 *
 * ... and your device screen should now say "hello"
 *
 * Now any text you enter at the command-prompt should appear
 * on your device screen. Enter "bye" to disconnect.
 */
public class EchoActivity extends Activity {

    private static final String TAG = "androidconcurrency";
    private static final int PORT = 4444;

    // SpeakHandler is a static class, which means it does NOT
    // create an implicit reference to the enclosing Activity.
    private static class SpeakHandler extends Handler {
        public static final int SAY_HELLO = 0;
        public static final int SAY_BYE = 1;
        public static final int SAY_WORD = 2;

        private TextView view;

        // attach a view to "speak" through
        public void attach(TextView view) {
            this.view = view;
        }

        // detach the view so that we don't leak
        public void detach() {
            // prevent memory leaks by clearing
            // our reference to the view
            view = null;
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SAY_HELLO:
                    sayWord("hello"); break;
                case SAY_BYE:
                    sayWord("goodbye"); break;
                case SAY_WORD:
                    sayWord((String) msg.obj); break;
                default:
                    super.handleMessage(msg);
            }
        }

        private void sayWord(String aWord) {
            // check if view is non-null, in case
            // we haven't been attached
            if (view != null)
                view.setText(aWord);
        }
    }

    // Parrot is a static class, which means it does NOT
    // create an implicit reference to the enclosing Activity.
    static class Parrot extends Thread {
        private Handler handler;
        private InetAddress address;
        private ServerSocket server;

        public Parrot(InetAddress address, Handler handler) {
            this.handler = handler;
            this.address = address;
            setPriority(Thread.MIN_PRIORITY);
        }

        public void run() {
            try {
                // bind a server-socket to the given address and port
                server = new ServerSocket(PORT, 1, address);
                while (true) {
                    // wait for a client to connect
                    Socket client = server.accept();

                    // hurrah, a client has connected, lets ask the main
                    // thread to echo "hello" to the screen
                    handler.sendMessage(
                        Message.obtain(handler, SpeakHandler.SAY_HELLO));

                    // buffer input from the socket into lines of text
                    BufferedReader in =
                        new BufferedReader(
                            new InputStreamReader(client.getInputStream()));

                    String word;

                    // loop until we receive "bye"
                    while (!"bye".equals(word = in.readLine())) {
                        // received some text, tell the main thread to
                        // echo it to the screen
                        handler.sendMessage(
                            Message.obtain(handler, SpeakHandler.SAY_WORD, word));
                    }

                    // we received "bye", so lets close the connection
                    client.close();

                    // tell the main thread to echo goodbye to the screen!
                    handler.sendMessage(
                        Message.obtain(handler, SpeakHandler.SAY_BYE));
                }
            } catch (Exception exc) {
                Log.e(TAG, exc.getMessage(), exc);
            }
        }

        public void disconnect() {
            if (server != null) {
                try {
                    if (server.isBound()) {
                        // when we close the socket any clients will
                        // also be closed and an exception thrown, which
                        // will break the run() method out of its while
                        // loops and terminate the thread.
                        server.close();
                    }
                } catch (Exception exc) {
                    Log.e(TAG, "disconnecting", exc);
                }
            }
        }
    }

    private static SpeakHandler handler = new SpeakHandler();
    private static Parrot parrot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch3_example3_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView view = (TextView) findViewById(R.id.speak);
        handler.attach(view);

        // pause/resume get called in a variety of ways - for
        // example if we start another Activity via an Intent -
        // so we need to check to see if we already have a parrot
        // and only create one if we need to...
        if (parrot == null) {
            InetAddress address = getAddress();

            parrot = new Parrot(address, handler);
            parrot.start();

            // update the screen to let the user know how
            // to connect ... we could do this by sending
            // a SAY_WORD message, but its overkill since
            // we're on the main thread and have the view...
            view.setText("telnet " + address.getHostAddress() + " " + PORT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // remove any pending SAY_WORD messages from the queue
        handler.removeMessages(SpeakHandler.SAY_WORD);
        handler.detach();

        if (isFinishing()) {
            // the app is exiting, so we want
            // to free up resources properly
            // by closing the socket and allowing
            // the Parrot's run method to exit
            parrot.disconnect();
            parrot = null;
        }
    }

    private InetAddress getAddress() {
        WifiManager m = (WifiManager) getSystemService(WIFI_SERVICE);
        return asInetAddress(m.getConnectionInfo().getIpAddress());
    }

    private InetAddress asInetAddress(int ip) {
        try {
            return InetAddress.getByAddress(new byte[]{
                (byte)(0xff & ip),
                (byte)(0xff & (ip >> 8)),
                (byte)(0xff & (ip >> 16)),
                (byte)(0xff & (ip >> 24)) });
        } catch (UnknownHostException exc) {
            throw new RuntimeException(exc);
        }
    }
}
