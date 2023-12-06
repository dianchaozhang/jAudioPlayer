import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class Test {

	public static void main(String[] args) {
//		System.out.println(Double.valueOf("00.00"));
//		System.out.println(new File(JAudioConst.GIF_CAT).exists());

		gifTest();
	}

	public static void gifTest() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
				}

				try {
					JLabel background = new JLabel(
//							new ImageIcon(ImageIO.read(
//									getClass().getResource(JAudioConst.IMAGE_BACKGROUND)))
							new ImageIcon(JAudioConst.IMAGE_BACKGROUND));
					background.setLayout(new GridBagLayout());

//					background.add(new WaitPane() );
					JPanel jPanel = new JPanel() {
						@Override
						protected void paintComponent(Graphics graphics) {
							super.paintComponent(graphics);
							graphics.setColor(getBackground());
							graphics.fillRect(0, 0, super.getWidth(), super.getHeight());
						}
					};
					jPanel.setLayout(new GridBagLayout());
					jPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
					// This is very important
					jPanel.setOpaque(false);
					jPanel.setBackground(new Color(0, 0, 0, 150));

					JLabel loadBar = new JLabel(new ImageIcon(JAudioConst.GIF_PATH));
					loadBar.setHorizontalAlignment(JLabel.CENTER);
					loadBar.setVerticalAlignment(JLabel.CENTER);
					jPanel.add(loadBar);

					background.add(jPanel);

					JFrame frame = new JFrame("Testing");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.add(background);
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
	}

}