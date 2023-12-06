import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * 音频播放器
 */
public class JAudioPlayer {

	/** 播放器 */
	private Clip clip;

	/** 文件是否加载标志 */
	private volatile boolean isAudioLoad;

	/** 是否正在播放标志 */
	private volatile boolean isPlaying;

	/** 已播放时长 */
	private double playTime;

	/** 音频总时长 */
	private double audioFullTime;

	/** 歌词实体类 */
	private JAudioLyricDTO audioLyricDTO;

	public JAudioPlayer() {
		// 记录已播放时长
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isPlaying()) {
					playTime += 0.01;
				}
			}
		}, 0, 10); // 每10毫秒刷新一次
	}

//	/**
//	 * 测试播放的主方法
//	 *
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		String audioFilePath = JAudioConst.AUDIO_FILE_PATH_LIST.get(0);
//		System.out.println(audioFullTime(audioFilePath));
//		testPlay(audioFilePath);
//	}
//
//	private static void testPlay(String audioFilePath) {
//
//		// 睡眠 n 秒
//		Consumer<Integer> sleep = (seconds) -> {
//			try {
//				Thread.sleep(seconds * 1000L);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		};
//
//		/*
//		 * 尝试播放一首歌，暂停后再播放，然后停止
//		 */
//		JAudioPlayer player = new JAudioPlayer();
//		try {
//			player.load(audioFilePath);
//		} catch (Exception e) {
//			System.err.println("加载文件出错！" + audioFilePath + "， error message：" + e.getMessage());
//		}
//
//		player.play(); // 播放
//
//		sleep.accept(5); // 等待几秒
//		player.pause(); // 暂停
//
//		sleep.accept(5); // 等待几秒
//		player.play(); // 继续播放
//
//		sleep.accept(5); // 等待几秒
//		player.stop(); // 停止
//
//		player.play(); // stop() 后再调用此方法无效！
//	}

	/**
	 * 获取音频总时长
	 *
	 * @param audioFilePath
	 * @return
	 */
	public static double audioFullTime(String audioFilePath) {
		try (InputStream in = JAudioUtils.loadFileAsStream(audioFilePath);
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(in)) {

			AudioFormat audioFormat = audioStream.getFormat();

			return audioStream.getFrameLength() / audioFormat.getSampleRate();

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "解析音频时长出错：" + audioFilePath //
					+ " error message:" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "解析音频时长出错：" + audioFilePath //
					+ " error message:" + e.getMessage());
		}

		return 0;
	}

	/**
	 * 加载文件，有问题时抛出异常
	 *
	 * @param audioFilePath
	 * @throws Exception
	 */
	public void load(String audioFilePath) throws Exception {
//        try {
//        } catch (UnsupportedAudioFileException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (LineUnavailableException e) {
//            throw new RuntimeException(e);
//        }

		// 先清空资源
		this.stop();

		// 重新加载资源
//		File audioFile = new File(audioFilePath);
//		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

//		AudioInputStream audioStream = AudioSystem //
//				.getAudioInputStream(JAudioPlayer.class.getResourceAsStream(audioFilePath));

		AudioInputStream audioStream = AudioSystem //
				.getAudioInputStream(JAudioUtils.loadFileAsStream(audioFilePath));

		AudioFormat audioFormat = audioStream.getFormat();

		// 获取音频总时长
		this.audioFullTime = audioStream.getFrameLength() / audioFormat.getSampleRate();

		DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
		this.clip = (Clip) AudioSystem.getLine(info);
		this.clip.open(audioStream);

		this.isAudioLoad = true;

		// 加载歌词文件

		String lyricFilePath = audioFilePath //
				.replace("audio/", "audioLyric/") //
				.replace(".wav", ".txt");
		try {
			this.audioLyricDTO = JAudioUtils.readAudioLyricFile(lyricFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放，检测到未加载文件时抛出异常。
	 */
	public void play() {
		if (clip == null) {
			JOptionPane.showMessageDialog(null, "请先加载音乐文件！");
			throw new RuntimeException("请先加载音乐文件！");
		}

		clip.start();
		isPlaying = true;
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
		isPlaying = false;
	}

	/**
	 * 停止，停止后不能继续播放。除非重新加载文件。
	 */
	public void stop() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			clip.close();
		}

		// restore
		isAudioLoad = false;
		isPlaying = false;
		playTime = 0.0;
		audioFullTime = 0.0;

		audioLyricDTO = null;
	}

	/**
	 * 音频文件是否已加载
	 * 
	 * @return
	 */
	public boolean isAudioLoad() {
		return isAudioLoad;
	}

	/**
	 * 是否正在播放
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * 获取已播放时间
	 * 
	 * @return
	 */
	public double getPlayTime() {
		return playTime;
	}

	/**
	 * 获取音频总时长
	 * 
	 * @return
	 */
	public double getAudioFullTime() {
		return audioFullTime;
	}

	/**
	 * 获取歌词实体类
	 * 
	 * @return
	 */
	public JAudioLyricDTO getAudioLyricDTO() {
		return audioLyricDTO;
	}

}