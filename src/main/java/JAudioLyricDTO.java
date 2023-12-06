import java.util.List;

import lombok.Data;

/**
 * 歌词实体类
 */
@Data
public class JAudioLyricDTO {

	private String title; // 标题
	private String author; // 作者
	private String album; // 专辑
	private String by; // 这不知道是啥
	private String offset; // 这不知道是啥

	// 歌词 时间 和 文字
	private List<JAudioLyricTimeTextDTO> lyricTimeTextList;

}
