import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class JAudioGUI extends JFrame {

	/** 播放器 */
	private JAudioPlayer player;

	/** 全局背景 */
	private JLabel background;
	/** 播放按钮 */
	private JButton buttonPlay;
	/** 歌词模块 */
	private JTextPane paneLyric;

	/** 歌词模块 - 歌词文本对象 */
	private DefaultStyledDocument lyricDoc;
	/** 歌词模块 - 默认歌词格式 */
	private Style styleLyricDefault;
	/** 歌词模块 - 正在播放行的歌词格式 */
	private Style styleLyricFocus;

	/** 播放列表 */
	private JList<String> playList;
//	/** 进度条 */
//	private JLabel playTime;
	/** 进度条 */
	private JProgressBar progressBar;

	/** 播放列表中的音频文件 */
	private List<String> audioFilePathList = new ArrayList<>();
	/** 播放列表显示的名称 */
	private Vector<String> audioFileNames = new Vector<>();

//	public JAudioGUI() {
//		this(null);
//	}

	public JAudioGUI(JAudioPlayer player) {
		this.player = player;

		super.setTitle("播放器");
		super.setBounds(160, 100, 710, 430);
		super.setLayout(null);

		this.initBackground(); // 初始化全局背景
		this.initButtonPlay(); // 初始化播放按钮
		this.initPaneLyric(); // 初始化歌词模块
		this.initPlayList(); // 初始化播放列表
		this.initPlayProgress(); // 初始化进度条

		this.initGif(); // 初始化GIF

		super.setVisible(true);// 放在添加组件后面执行
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// 进度条 歌词滚动等
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (player == null) {
					return;
				}

				// 进度条 start
				double playTimeDouble = player.getPlayTime();

				double progress = (playTimeDouble / player.getAudioFullTime()) * 1000;
				progressBar.setValue((int) progress);

				String playTimeStr = JAudioUtils.seconds2TimeStr((int) player.getPlayTime());
				progressBar.setString(playTimeStr);
				// 进度条 end

				// 设置歌词滚动显示 start
				JAudioLyricDTO lyricDTO = player.getAudioLyricDTO();
				if (lyricDTO == null || lyricDTO.getLyricTimeTextList().isEmpty()) {
					return;
				}
				// 取出歌词实体类
				List<JAudioLyricTimeTextDTO> lyricTimeTextDTOList = lyricDTO.getLyricTimeTextList();
				// 取出需要展示的部分歌词
				List<JAudioLyricTimeTextDTO> lyric4Show = lyric4Show(lyricTimeTextDTOList, playTimeDouble);

				try {
					lyricDoc.remove(0, lyricDoc.getLength()); // 清除现有内容
					for (JAudioLyricTimeTextDTO timeTextDTO : lyric4Show) {
						lyricDoc.insertString(lyricDoc.getLength(), timeTextDTO.getLyricText() + "\n",
								timeTextDTO.isFocusOn() ? styleLyricFocus : styleLyricDefault);
					}
//					lyricDoc.setCharacterAttributes(10, 10, styleLyricFocus, true);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				// 设置歌词滚动显示 end
			}
		}, 0, 100); // 每 100毫秒（0.1秒）刷新一次
	}

	private List<JAudioLyricTimeTextDTO> lyric4Show(List<JAudioLyricTimeTextDTO> lyricTimeTextDTOList,
			double playTimeDouble) {

		lyricTimeTextDTOList.forEach(x -> x.setFocusOn(false)); // 重置 focusOn 的值

		List<JAudioLyricTimeTextDTO> dtoList = new ArrayList<>();

		// 找到正在播放的歌词所在的行
		int targetIndex = -99;
		for (int i = 0; i < lyricTimeTextDTOList.size(); i++) {
			JAudioLyricTimeTextDTO timeTextDTO = lyricTimeTextDTOList.get(i);
			double lyricTimeDouble = timeTextDTO.getLyricTimeDouble();

			if (lyricTimeDouble >= playTimeDouble) {
//			if (playTimeDouble - lyricTimeDouble <= 0.1) { // 提前 0.1 秒加载
				targetIndex = i;
				timeTextDTO.setFocusOn(true);
				break;
			}
		}

		// 取前2行 ~ 后3行，共6行数据
		int startIndex = targetIndex - 2;
		int endIndex = targetIndex + 3;

		for (int i = startIndex; i <= endIndex; i++) {

			if (i < 0 || i >= lyricTimeTextDTOList.size()) {
				dtoList.add(new JAudioLyricTimeTextDTO(null, ""));
				continue;
			}
			dtoList.add(lyricTimeTextDTOList.get(i));
		}

		return dtoList;
	}

	/**
	 * 添加音频文件
	 */
	public void addAudioFile(String filePath) {
		audioFilePathList.add(filePath);

		double audioFullTime = JAudioPlayer.audioFullTime(filePath);
		String audioFullTimeStr = JAudioUtils.seconds2TimeStr((int) audioFullTime);

		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		audioFileNames.add(fileName + "  " + audioFullTimeStr);
	}

	/**
	 * 初始化全局背景
	 */
	private void initBackground() {
		this.background = new JLabel();
//		background.setIcon(new ImageIcon(JAudioConst.IMAGE_BACKGROUND)); // 设置背景图片
		background.setIcon(JAudioUtils.readImageIcon(JAudioConst.IMAGE_BACKGROUND)); // 设置背景图片
		background.setBounds(0, 0, 700, 400); // 设置背景控件大小
		super.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE)); // 背景图片控件置于最底层
		((JPanel) super.getContentPane()).setOpaque(false); // 控件透明
	}

	/**
	 * 初始化播放按钮
	 */
	private void initButtonPlay() {
		this.buttonPlay = new JButton();
		super.add(buttonPlay);

		buttonPlay.setBounds(322, 335, 40, 40);
		buttonPlay.setIcon(JAudioUtils.readImageIcon(JAudioConst.IMAGE_PLAY)); // 设置图标
//        buttonPlay.setText("播放");
		buttonPlay.setOpaque(false); // 背景透明

		// 添加按钮响应
		buttonPlay.addActionListener(actionEvent -> {
			if (player == null || !player.isAudioLoad()) {
				return;
			}

			if (player.isPlaying()) {
				player.pause(); // 正在播放则暂停
				buttonPlay.setIcon(JAudioUtils.readImageIcon(JAudioConst.IMAGE_PLAY)); // 设置图标
			} else {
				player.play(); // 不在播放则播放
				buttonPlay.setIcon(JAudioUtils.readImageIcon(JAudioConst.IMAGE_PAUSE)); // 设置图标
			}
		});
	}

	/**
	 * 初始化歌词模块
	 */
	private void initPaneLyric() {
		// 初始化 歌词格式对象
		StyleContext styleContext = new StyleContext();
		this.styleLyricDefault = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setForeground(styleLyricDefault, Color.WHITE);

		this.styleLyricFocus = styleContext.addStyle("styleLyricFocus", null);
		StyleConstants.setForeground(styleLyricFocus, Color.RED);
		StyleConstants.setBold(styleLyricFocus, true);

		// 初始化歌词模块
		this.lyricDoc = new DefaultStyledDocument();
		this.paneLyric = new JTextPane(lyricDoc);
		super.add(paneLyric);

		paneLyric.setBounds(20, 100, 200, 100);
		paneLyric.setForeground(Color.white);// 字体白色
		paneLyric.setEditable(false);
		paneLyric.setOpaque(false);// 背景透明
//		paneLyric.setText("芙蓉花又栖满了枝头 \n" + "奈何蝶难留\n" + "漂泊如江水向东流\n" + "望断门前隔岸的杨柳 \n");
		paneLyric.setText("------歌词------\n" //
				+ "------歌词------\n" //
				+ "------歌词------\n" //
				+ "------歌词------\n" //
				+ "------歌词------\n" //
				+ "------歌词------" //
		);

//		try {
//			// 添加文本时设置默认的格式
//			lyricDoc.insertString(lyricDoc.getLength(), "line1: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line2: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line3: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line4: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line5: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line6: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line7: text text \n", styleLyricDefault);
//			lyricDoc.insertString(lyricDoc.getLength(), "line8: text text \n", styleLyricFocus);
//			lyricDoc.insertString(lyricDoc.getLength(), "line9: text text \n", styleLyricDefault);
//
//			// 修改已有文本的格式
//			lyricDoc.setCharacterAttributes(10, 10, styleLyricFocus, true);
//			// 删除文本
//			lyricDoc.remove(0, 3);
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}

	}

	/**
	 * 初始化播放列表
	 */
	private void initPlayList() {
		this.playList = new JList(); // 创建播放列表
		super.add(playList); // 添加播放列表至窗口中

		playList.setBounds(500, 100, 150, 150); // 设置播放列表大小
		playList.setOpaque(false);// 设置播放列表透明
		playList.setBackground(new Color(0, 0, 0, 0));// 设置播放列表背景颜色
		playList.setForeground(Color.white);// 设置播放列表字体颜色

//		playFiles.forEach(
//				x -> vertor.add(x.getName() + "\t" + Math.floor(JAudioPlayer.audioFullTime(x.getAbsolutePath()))));
//
//		Vector<String> vertor = new Vector<>();
//		vertor.add("花海");
//		vertor.add("珊瑚海");
//		vertor.add("七里香");
//		vertor.add("浪漫手机");
//		playList.setListData(vertor);

		playList.setListData(audioFileNames);

		playList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				// 双击
				if (mouseEvent.getClickCount() == 2) {
					int jlistIndex = playList.getSelectedIndex();

					// 停止播放当前的音乐
					player.stop();

					// 加载双击选中的文件
					String audioFilePath = audioFilePathList.get(jlistIndex);
					try {
						player.load(audioFilePath);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "加载音频文件出错：" + audioFilePath);

//						JOptionPane.showMessageDialog(null, "加载音频文件出错："
////								+ e.getMessage() + "\n"
//								 + e.getStackTrace()
////								+ e.getCause()
//						);
					}

					// 播放双击选中的文件
					player.play();

					// 更改播放按钮的图标
					buttonPlay.setIcon(JAudioUtils.readImageIcon(JAudioConst.IMAGE_PAUSE)); // 设置图标
				}
			}
		});
	}

	/**
	 * 初始化进度条
	 */
	private void initPlayProgress() {
//		this.playTime = new JLabel(); // 创建播放进度条对象
//      playTime.setIcon(new ImageIcon(imageFilePath_timeFlag));
//      playTime.setBounds(0,324,0,3); // 设置播放进度条对象大小
//		super.add(playTime); // 添加播放进度条至窗口中

//		progressBar.setForeground(Color.BLACK);
//		progressBar.setStringPainted(true);
//		progressBar.setOpaque(false);
//		progressBar.setBounds(73, 103, 434, 24);
//		this.getContentPane().add(progressBar);

		this.progressBar = new JProgressBar();
		super.add(progressBar);

		progressBar.setMaximum(1000);
		progressBar.setMinimum(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(false);
		progressBar.setBounds(0, 314, 700, 20);
		progressBar.setOpaque(false); // 透明
//		progressBar.setPreferredSize(new Dimension(780, 30));

//		progressBar.setValue(50); // 进度百分值
//		progressBar.setString("02:19"); // 显示文本
	}

	private void initGif() {
		// 创建面板
		JPanel jPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				graphics.setColor(getBackground());
				graphics.fillRect(0, 0, super.getWidth(), super.getHeight());
			}
		};
		jPanel.setBounds(240, 100, 200, 100);
		jPanel.setLayout(new GridBagLayout());
		jPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
		jPanel.setOpaque(false);
		jPanel.setBackground(new Color(0, 0, 0, 150));

		JLabel loadBar = new JLabel(JAudioUtils.readImageIcon(JAudioConst.GIF_PATH));
		loadBar.setHorizontalAlignment(JLabel.CENTER);
		loadBar.setVerticalAlignment(JLabel.CENTER);
		jPanel.add(loadBar);

		this.background.add(jPanel);
	}

}