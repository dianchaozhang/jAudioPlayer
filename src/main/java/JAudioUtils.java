import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * 工具类
 */
public class JAudioUtils {

	/**
	 * 将秒数转换为时分秒 HH:mm:ss
	 *
	 * @param seconds
	 * @return
	 */
	public static String seconds2TimeStr(int seconds) {
		if (seconds < 0) {
			return "";
		}

		// 计算
		int second = seconds % 60;
		int minutes = seconds / 60;
		int minute = minutes % 60;
		int hours = minutes / 60;

		// 拼接
		StringBuilder sb = new StringBuilder();
		if (hours > 0) {
			sb.append(hours).append(":");
		}

		sb.append(String.format("%02d", minute)).append(":");
		sb.append(String.format("%02d", second));

		return sb.toString();
	}

	/**
	 * 读取歌词文件，并封装成实体类
	 *
	 * @param lyricFilePath
	 * @return
	 */
	public static JAudioLyricDTO readAudioLyricFile(String lyricFilePath) {
		List<String> lines = readTextFile(lyricFilePath);
		return analyzeLyricLines(lines);
	}

	/**
	 * 按行读取文本文件
	 *
	 * @param textFilePath
	 * @return
	 */
	public static List<String> readTextFile(String textFilePath) {
		List<String> lines = new ArrayList<>();

		// 按行读取歌词文件
		try (InputStreamReader isr = new InputStreamReader(JAudioUtils.loadFileAsStream(textFilePath), "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "读取文本文件出错：" + textFilePath //
					+ " error message:" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "读取文本文件出错：" + textFilePath //
					+ " error message:" + e.getMessage());
		}

		return lines;
	}

	/**
	 * 解析歌词
	 *
	 * @param lyricLines
	 * @return
	 */
	public static JAudioLyricDTO analyzeLyricLines(List<String> lyricLines) {
//		[ti:千里之外]
//		[ar:周杰伦/费玉清]
//		[al:依然范特西]
//		[by:]
//		[offset:0]
//		[00:00.00]千里之外 - 周杰伦 (Jay Chou)/费玉清 (Yu-Ching Fei)
//		[00:04.91]词：方文山
//		[00:09.82]曲：周杰伦
//		[00:14.73]编曲：林迈可
//		[00:19.64]周：
//		[00:24.66]屋檐如悬崖
//		[00:26.75]风铃如沧海
//		[00:28.76]我等燕归来

		// 正则表达式，用于提取 歌词内容
		String regexTitle = "^\\[ti:(.*)\\]$";
		String regexAuthor = "^\\[ar:(.*)\\]$";
		String regexAlbum = "^\\[al:(.*)\\]$";
		String regexBy = "^\\[by:(.*)\\]$";
		String regexOffset = "^\\[offset:(.*)\\]$";
		String regexLyricTimeText = "^\\[(\\d{2}:\\d{2}.\\d{2})\\](.*)$";

		// 用于提取 title author 等字段的 function
		BiFunction<String, List<String>, String> matchFunc = (regex, strList) -> {
			Pattern pattern = Pattern.compile(regex);
			for (String str : strList) {
				Matcher matcher = pattern.matcher(str);
				if (matcher.matches()) {
					return matcher.group(1);
				}
			}
			return "";
		};

		// 用于提取 歌词时间 歌词内容的 function
		BiFunction<String, List<String>, List<JAudioLyricTimeTextDTO>> matchFunc2 = (regex, strList) -> {
			List<JAudioLyricTimeTextDTO> lyricTimeTextList = new ArrayList<>();
			Pattern pattern = Pattern.compile(regex);
			for (String str : strList) {
				Matcher matcher = pattern.matcher(str);
				if (matcher.matches()) {
					String lyricTime = matcher.group(1);
					String lyricText = matcher.group(2);
					lyricTimeTextList.add(new JAudioLyricTimeTextDTO(lyricTime, lyricText));
				}
			}
			return lyricTimeTextList;
		};

		// 提取歌词文件的内容，并封装对象
		JAudioLyricDTO dto = new JAudioLyricDTO();
		dto.setTitle(matchFunc.apply(regexTitle, lyricLines));
		dto.setAuthor(matchFunc.apply(regexAuthor, lyricLines));
		dto.setAlbum(matchFunc.apply(regexAlbum, lyricLines));
		dto.setBy(matchFunc.apply(regexBy, lyricLines));
		dto.setOffset(matchFunc.apply(regexOffset, lyricLines));
		dto.setLyricTimeTextList(matchFunc2.apply(regexLyricTimeText, lyricLines));

		// 将歌词所在时间 转换为 double
		dto.getLyricTimeTextList().forEach(x -> {

			// [01:13.24]我送你离开千里之外你无声黑白
			String lyricTimeStr = x.getLyricTime();
			int minutes = Integer.valueOf(lyricTimeStr.substring(0, 2));
			double seconds = Double.valueOf(lyricTimeStr.substring(3));

			double lyricTimeDouble = minutes * 60 + seconds;
			x.setLyricTimeDouble(lyricTimeDouble);
		});

		// 修正歌词后期不准的问题，根据百分比，修正 0-2 秒
//		double lastLyricTimeDouble = // 最后一句歌词
//				dto.getLyricTimeTextList().get(dto.getLyricTimeTextList().size() - 1).getLyricTimeDouble();

//		dto.getLyricTimeTextList().forEach(x -> {
////			double fixValue = x.getLyricTimeDouble() / lastLyricTimeDouble * 1.5;
//			x.setLyricTimeDouble(x.getLyricTimeDouble() * 0.95);
//		});

		return dto;
	}

	/**
	 * 通过流的方式读取 image文件
	 * 
	 * @param relativePath
	 * @return
	 */
	public static ImageIcon readImageIcon(String relativePath) {
//		BufferedImage image = ImageIO.read(JAudioUtils.class.getResourceAsStream(relativePath));
		return new ImageIcon(JAudioUtils.class.getResource(relativePath));
	}

	/**
	 * 将文件加载为 BufferedInputStream <br>
	 * 注意本方法不会关闭流
	 * 
	 * @param relativePath
	 * @return
	 */
	public static BufferedInputStream loadFileAsStream(String relativePath) {
		return new BufferedInputStream(JAudioUtils.class.getResourceAsStream(relativePath));
	}

	public static void main(String[] args) {
//		System.out.println(seconds2TimeStr(261));
//		System.out.println(seconds2TimeStr(3861));
//		testRegex();

		JAudioLyricDTO dto = readAudioLyricFile("./src/main/resources/audioLyric/千里之外.txt");
		System.out.println(dto);
	}

//	private static void testRegex() {
//		String regexTitle = "^\\[ti:(.*)\\]$";
//		String regexAuthor = "^\\[ar:(.*)\\]$";
//		String regexAlbum = "^\\[al:(.*)\\]$";
//		String regexBy = "^\\[by:(.*)\\]$";
//		String regexOffset = "^\\[offset:(.*)\\]$";
//		String regexLyric = "^\\[(\\d{2}:\\d{2}.\\d{2})\\](.*)$";
//
//		String title = "[ti:千里之外]";
//		String author = "[ar:周杰伦/费玉清]";
//		String album = "[al:依然范特西]";
//		String by = "[by:]";
//		String offset = "[offset:0]";
//		String lyric = "[00:00.00]千里之外 - 周杰伦 (Jay Chou)/费玉清 (Yu-Ching Fei)";
//
//		Matcher matcher = Pattern.compile(regexLyric).matcher(lyric);
//		System.out.println(matcher.matches());
//		System.out.println(matcher.group(1));
//		System.out.println(matcher.group(2));
//	}

}
