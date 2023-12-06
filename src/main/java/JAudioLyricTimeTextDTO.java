import lombok.Data;

/**
 * 单句歌词实体类
 */
@Data
public class JAudioLyricTimeTextDTO {

	private String lyricTime;
	private String lyricText;

	private double lyricTimeDouble; // 歌词所在时间转换为 double 方便计算
	private boolean focusOn; // 是否正在播放本句

	public JAudioLyricTimeTextDTO(String lyricTime, String lyricText) {
		this.lyricTime = lyricTime;
		this.lyricText = lyricText;
	}

}
