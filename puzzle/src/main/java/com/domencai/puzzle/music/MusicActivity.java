package com.domencai.puzzle.music;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.domencai.puzzle.R;


/**
 * Created by Domen、on 2017/9/2.
 */

public class MusicActivity extends AppCompatActivity {

    private static final float VISUALIZER_HEIGHT_DIP = 100f;//频谱View高度

    private MediaPlayer mMediaPlayer;//音频
    private Visualizer mVisualizer;//频谱器

    private LinearLayout mLayout;//代码布局
    MusicView mBaseVisualizerView;

    Button play;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);//设置音频流 - STREAM_MUSIC：音乐回放即媒体音量

        mLayout = new LinearLayout(this);//代码创建布局
        mLayout.setOrientation(LinearLayout.VERTICAL);//设置为线性布局-上下排列
        mLayout.setGravity(Gravity.CENTER);
        setContentView(mLayout);//将布局添加到 Activity

        mMediaPlayer = MediaPlayer.create(this, R.raw.a);//实例化 MediaPlayer 并添加音频

        setupVisualizerFxAndUi();//添加频谱到界面
        setupPlayButton();//添加按钮到界面


        mVisualizer.setEnabled(true);//false 则不显示

        mMediaPlayer.start();//开始播放
        mMediaPlayer.setLooping(true);//循环播放

    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {
        mBaseVisualizerView = new MusicView(this);
        mBaseVisualizerView.setBackgroundColor(Color.BLUE);

        mBaseVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,//宽度
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)//高度
        ));
        //将频谱View添加到布局
        mLayout.addView(mBaseVisualizerView);
        //实例化Visualizer，参数SessionId可以通过MediaPlayer的对象获得
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        //采样 - 参数内必须是2的位数 - 如64,128,256,512,1024
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        //设置允许波形表示，并且捕获它
        mBaseVisualizerView.setVisualizer(mVisualizer);
    }

    //播放按钮
    private void setupPlayButton() {
        play = new Button(this);
        play.setText("Stop");
        play.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    play.setText("Stop");
                } else {
                    play.setText("Start");
                    mMediaPlayer.start();
                }
            }
        });

        mLayout.addView(play);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
