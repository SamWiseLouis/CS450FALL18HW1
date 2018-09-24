package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_stop = null;
    private Button bt_reset = null;

    private Timer t = null;
    private Counter ctr = null;  // TimerTask

    public AudioAttributes  aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_stop = findViewById(R.id.bt_stop);
        this.bt_reset = findViewById(R.id.bt_reset);





        bt_start.setBackgroundColor(getResources().getColor(R.color.green));
        bt_stop.setEnabled(false); // should not be able to hit stop button when nothing is running
        bt_stop.setBackgroundColor(getResources().getColor(R.color.grey));



        bt_reset.setBackgroundColor(getResources().getColor(R.color.green));

        //when start button clicked
        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                bt_start.setEnabled(false);
                bt_start.setBackgroundColor(getResources().getColor(R.color.grey));
                bt_stop.setEnabled(true);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.red));
                bt_reset.setEnabled(false);
                bt_reset.setBackgroundColor(getResources().getColor(R.color.grey));

                    t = new Timer();
                    ctr = new Counter();
                    t.scheduleAtFixedRate(ctr, 0, 100);

            }
        });

        //when stop button clicked
        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_stop.setEnabled(false);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.grey));
                bt_start.setEnabled(true);
                bt_start.setBackgroundColor(getResources().getColor(R.color.green));
                bt_reset.setEnabled(true);
                bt_reset.setBackgroundColor(getResources().getColor(R.color.green));

                // remove the scheduled task and stop counter
                t.cancel();
                ctr.cancel(); //clear everything running in timer and counter
                t.purge();


            }
        });
        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_start.setEnabled(true);
                bt_stop.setEnabled(false);
                bt_stop.setBackgroundColor(getResources().getColor(R.color.grey));
                bt_reset.setEnabled(false);
                bt_reset.setBackgroundColor(getResources().getColor(R.color.grey));


                tv_count.setText("00:00:0");
                    getPreferences(MODE_PRIVATE)
                            .edit()
                            .putInt("Count", 0)
                            .apply();
                   // change the stored value to 0 resetting the time in reality

            }
        });


        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int color = 0xFF000000 | rnd.nextInt(0xFFFFFF);


                tv_count.setTextColor(color);
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        int count = getPreferences(MODE_PRIVATE)
              .getInt("Count", 0);
        this.tv_count.setText(String.format("%02d",(count / 600))+":"+ String.format("%02d", (count / 10) % 60)+":"+ Integer.toString(count%10));

        //Toast.makeText(this, "Stopwatch is started",
          //      Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {  // when destroyed save what was on screen as the time for the next run
        super.onDestroy();
        if(t!=null) { // just in case someone closes app without stopping the timer
            t.cancel();
        }
      //  if (ctr!=null) {
            //getPreferences(MODE_PRIVATE)
              //      .edit()
                //    .putInt("Count", ctr.count)
          //          .apply();
        //}
    }

    class Counter extends TimerTask {
        // get the count from the memory
       private int count = getPreferences(MODE_PRIVATE)
                .getInt("Count", 0);

        @Override
        public void run() {
                MainActivity.this.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {

                                MainActivity.this.tv_count.setText(
                                        String.format("%02d",(count / 600))+":"+ String.format("%02d", (count / 10) % 60)+":"+ Integer.toString(count%10));
                                count++;

                                // save every time the updated count// has to be a better way
                                getPreferences(MODE_PRIVATE)
                                        .edit()
                                        .putInt("Count", ctr.count)
                                        .apply();


                            }
                        }
                );
        }

    }
}