import javax.sound.sampled.*;
import java.io.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Musicrun {

    public static void main(String[] args) {
        new Musicrun();
    }

    JFrame frame = new JFrame("音乐播放器");

    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel p3 = new JPanel();

    Border etched = BorderFactory.createEtchedBorder();
    Border border = BorderFactory.createTitledBorder(etched, "");

    JButton buttonStart = new JButton("Start");
    JButton buttonStop = new JButton("Stop");
    JButton buttonContinue = new JButton("Continue");

    boolean isPlaying = false;

    JLabel yinliang = new JLabel();
    JLabel musicImage = new JLabel();

    float ylforce = 0;

    double atime = 0;
    double bftime = 0;

    public String musicPath = "./src/main/resources/audio/花海.wav";
    public AudioPlayer music = null;

    JSlider slider1 = new JSlider();

    JProgressBar progressBar = new JProgressBar();

    ImageIcon icon[] = { new ImageIcon("D:\\zuoye\\java\\resource\\b1.png"),
            new ImageIcon("D:\\zuoye\\java\\resource\\b2.png"),
            new ImageIcon("D:\\zuoye\\java\\resource\\b3.png"),
            new ImageIcon("D:\\zuoye\\java\\resource\\b4.png"),
            new ImageIcon("D:\\zuoye\\java\\resource\\b5.png") };

    public Musicrun() {
        frame.setSize(800, 800);
        frame.setLayout(new GridLayout(3, 1));

        border = BorderFactory.createTitledBorder("播放进度");
        p2.setBorder(border);

        border = BorderFactory.createTitledBorder("音量控制");
        p3.setBorder(border);

        init();

        frame.getContentPane().add(p1);
        frame.getContentPane().add(p2);
        frame.getContentPane().add(p3);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private void init() {
        // p1部分
        musicImage.setIcon(icon[0]);
        p1.add(musicImage);

        Timer timer = new Timer();
        // timer.schedule(new MusicTask(), 500,1000);
        timer.schedule(new MusicTask(), 500, 2000);

        // p2部分
        music = new AudioPlayer(musicPath);

        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(false);
        progressBar.setPreferredSize(new Dimension(780, 30));


        p2.add(progressBar);

        Timer timer2 = new Timer();
        timer2.schedule(new MusicTask2(), 0, 100);

        buttonStart.setSize(80, 40);
        buttonStop.setSize(80, 40);
        buttonContinue.setSize(80, 40);
        p2.add(buttonStart);
        p2.add(buttonStop);
        p2.add(buttonContinue);
        buttonStart.addActionListener(new ButtonHandler());
        buttonStop.addActionListener(new ButtonHandler1());
        buttonContinue.addActionListener(new ButtonHandler2());

        // p3部分
        yinliang.setText("当前音量为：" + slider1.getValue());
        // yinliang.setBounds(0,630,100,20);
        p3.setLayout(new BorderLayout());
        p3.add(yinliang, BorderLayout.WEST);

        slider1.setValue(100);
        slider1.setPaintTicks(true);// setPaintTicks()方法是设置是否在JSlider加上刻度，若为true则下面两行才有作用。
        slider1.setMajorTickSpacing(20);
        slider1.setMinorTickSpacing(5);
        slider1.setPaintLabels(true);// setPaintLabels()方法为设置是否数字标记，若设为true，则JSlider刻度上就会有数值出现。
        slider1.setPaintTrack(true);// setPaintTrack()方法表示是否出现滑动杆的横杆。默认值为true.
        slider1.setSnapToTicks(true);// setSnapToTicks()方法表示一次移动一个小刻度，而不再是一次移动一个单位刻度。
        // JLabel label1 = new JLabel("目前刻度：" + );
        slider1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                yinliang.setText("当前音量：" + slider1.getValue());
                ylforce = (float) ((0.86) * slider1.getValue() - 80);
                music.setVol(ylforce);

            }
        });
        p3.add(slider1, BorderLayout.SOUTH);

    }

    public class MusicTask extends TimerTask {
        // TODO 自动生成的方法存根
        int i = 0;

        @Override
        public void run() {
            musicImage.setIcon(icon[i]);
            i++;
            if (i == 5) {
                i = 0;
            }
        }
    }

    public class MusicTask2 extends TimerTask {
        // TODO 自动生成的方法存根
        // double progressValues;
        int progressBarValues;

        @Override
        public void run() {

            if (isPlaying == true) {
                bftime += 0.1;
                progressBarValues = (int) ((bftime / atime) * 100);
                progressBar.setValue(progressBarValues);
            }
        }
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isPlaying == false) {
                isPlaying = true;
                bftime = 0;
                music.start(isPlaying);

            } else {
                music.start(isPlaying);
            }
        }
    }

    private class ButtonHandler1 implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isPlaying == true) {
                isPlaying = false;
                music.stop();
            } else {
                music.stop();
            }
        }
    }

    private class ButtonHandler2 implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isPlaying == false) {
                isPlaying = true;
                music.continues();
            } else {
                music.continues();
            }
        }
    }

    public class AudioPlayer {
        private String musicPath; // 音频文件
        private volatile boolean run = true; // 记录音频是否播放
        private Thread mainThread; // 播放音频的任务线程
        private float newVolumn = 7;

        private AudioInputStream audioStream;
        private AudioFormat audioFormat;
        private SourceDataLine sourceDataLine;

        public AudioPlayer(String musicPath) {
            this.musicPath = musicPath;
            prefetch();
        }

        // 数据准备
        private void prefetch() {
            try {
                // 获取音频输入流
                audioStream = AudioSystem.getAudioInputStream(new File(musicPath));
                // 获取音频的编码对象
                audioFormat = audioStream.getFormat();
                // 包装音频信息
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
                        AudioSystem.NOT_SPECIFIED);
                // 使用包装音频信息后的Info类创建源数据行，充当混频器的源
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                // 获得音频的总时长
                atime = audioStream.getFrameLength() / audioFormat.getSampleRate();

                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

            } catch (UnsupportedAudioFileException ex) {
                ex.printStackTrace();
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        // 析构函数:关闭音频读取流和数据行
        protected void finalize() throws Throwable {
            super.finalize();
            sourceDataLine.drain();
            sourceDataLine.close();
            audioStream.close();
        }

        // 播放音频:通过loop参数设置是否循环播放
        private void playMusic(boolean loop) throws InterruptedException {

            try {
                if (loop) {
                    while (true) {
                        playMusic();
                        bftime = 0;
                    }
                } else {
                    playMusic();
                    // 清空数据行并关闭
                    sourceDataLine.drain();
                    sourceDataLine.close();
                    audioStream.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        private void playMusic() {
            try {
                synchronized (this) {
                    run = true;
                }
                // 通过数据行读取音频数据流，发送到混音器;
                // 数据流传输过程：AudioInputStream -> SourceDataLine;
                audioStream = AudioSystem.getAudioInputStream(new File(musicPath));
                int count;
                byte tempBuff[] = new byte[1024];

                while ((count = audioStream.read(tempBuff, 0, tempBuff.length)) != -1) {
                    synchronized (this) {
                        while (!run)
                            wait();
                    }
                    sourceDataLine.write(tempBuff, 0, count);
                }

            } catch (UnsupportedAudioFileException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        // 暂停播放音频
        private void stopMusic() {
            synchronized (this) {
                run = false;
                notifyAll();
            }
        }

        // 继续播放音乐
        private void continueMusic() {
            synchronized (this) {
                run = true;
                notifyAll();
            }
        }

        // 外部调用控制方法:生成音频主线程；
        public void start(boolean loop) {
            mainThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        playMusic(loop);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            mainThread.start();
        }

        // 外部调用控制方法：暂停音频线程
        public void stop() {
            new Thread(new Runnable() {
                public void run() {
                    stopMusic();
                }
            }).start();
        }

        // 外部调用控制方法：继续音频线程
        public void continues() {
            new Thread(new Runnable() {
                public void run() {
                    continueMusic();
                }
            }).start();
        }

        // 播放器的状态
        public boolean isPlaying() {
            return run;
        }

        // 设置音频音量
        // https://zhidao.baidu.com/question/269020584.html
        public void setVol(float value) {
            newVolumn = value;
            // 必须open之后
            if (newVolumn != 7) {
                FloatControl control = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                // System.out.println(control.getMaximum());
                // System.out.println(control.getMinimum());
                control.setValue(newVolumn);
            }
        }

        // 销毁
        public void destroy() {
            try {
                finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}




