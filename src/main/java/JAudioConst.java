import java.util.Arrays;
import java.util.List;

/**
 * 常量类
 */
public interface JAudioConst {

	/** 默认的音频文件 */
	List<String> AUDIO_FILE_PATH_LIST = Arrays.asList( //
			"audio/千里之外.wav", //
			"audio/晴天.wav", //
			"audio/稻香.wav", //
			"audio/简单爱.wav", //
			"audio/花海.wav", //
			"audio/青花瓷.wav" //
	);

	/** 图片路径：背景图 */
	String IMAGE_BACKGROUND = "image/background_pink.jpg";

	/** 图片路径：播放按钮 */
	String IMAGE_PLAY = "image/play.png";

	/** 图片路径：播放按钮 */
	String IMAGE_PAUSE = "image/pause.png";

//	/** 图片路径：进度条 */
//	String IMAGE_PROGRESS = "image/progress.png";

	/** gif 动图路径 */
//	String GIF_PATH = "image/gif_flower.gif";
	String GIF_PATH = "image/gif_bongoCat.gif";

}
