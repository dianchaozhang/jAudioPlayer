import java.io.File;

/**
 * 项目主程序入口
 */
public class JAudioApp {

	/**
	 * 项目主程序入口
	 *
	 * @param args
	 */
	public static void main(String[] args) {

//		JAudioGUI gui = new JAudioGUI(new JAudioPlayer());
//
//		JAudioConst.AUDIO_FILE_PATH_LIST.forEach(x -> gui.addAudioFile(x));
		System.out.println(new File("pom.xml").exists());
	}

}
