import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class JTextPaneStyleTest {

	public static final String text = "Attributes, Styles and Style Contexts\n"
			+ "The simple PlainDocument class that you saw in the previous \n"
			+ "chapter is only capable of holding text. The more complex text \n"
			+ "components use a more sophisticated model that implements the \n"
			+ "StyledDocument interface. StyledDocument is a sub-interface of \n"
			+ "Document that contains methods for manipulating attributes that \n"
			+ "control the way in which the text in the document is displayed. \n"
			+ "The Swing text package contains a concrete implementation of \n"
			+ "StyledDocument called DefaultStyledDocument that is used as the \n"
			+ "default model for JTextPane and is also the base class from which \n"
			+ "more specific models, such as the HTMLDocument class that handles \n"
			+ "input in HTML format, can be created. In order to make use of \n"
			+ "DefaultStyledDocument and JTextPane, you need to understand how \n"
			+ "Swing represents and uses attributes.\n";

	public JTextPaneStyleTest() throws BadLocationException {

		JFrame frame = new JFrame();
		DefaultStyledDocument document = new DefaultStyledDocument();
		JTextPane pane = new JTextPane(document);
		JPanel mainPanel = new JPanel();
		JButton button = new JButton("Button");
		button.setPreferredSize(new Dimension(100, 40));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane.setPreferredSize(new Dimension(200, 200));
		mainPanel.add(button);
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.getContentPane().add(mainPanel, BorderLayout.WEST);
		StyleContext context = new StyleContext();
		// build a style
		Style style = context.addStyle("test", null);
		// set some style properties
		StyleConstants.setForeground(style, Color.BLACK);
		document.insertString(0, "Four: success \n", style);
		StyleConstants.setForeground(style, Color.RED);
		document.insertString(0, "Three: error \n ", style);
		document.insertString(0, "Two: error \n ", style);

		StyleConstants.setForeground(style, Color.BLACK);
		// add some data to the document
		document.insertString(0, "One: success \n", style);

		// StyleConstants.setForeground(style, Color.blue);

		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) throws BadLocationException {
//		new Test();
		test111();
	}

	public static void test111() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception evt) {
		}

		JFrame f = new JFrame("Styles Example 3");

		StyleContext sc = new StyleContext();
		final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
		JTextPane pane = new JTextPane(doc);

		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		final Style mainStyle = sc.addStyle("MainStyle", defaultStyle);
//		StyleConstants.setLeftIndent(mainStyle, 16);
//		StyleConstants.setRightIndent(mainStyle, 16);
//		StyleConstants.setFirstLineIndent(mainStyle, 16);
//		StyleConstants.setFontFamily(mainStyle, "serif");
//		StyleConstants.setFontSize(mainStyle, 12);

		final Style boldStyle = sc.addStyle("MainStyle", defaultStyle);
		StyleConstants.setBold(boldStyle, true);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						doc.setLogicalStyle(0, mainStyle);

						doc.insertString(0, text, null);
					} catch (BadLocationException e) {
					}
					doc.dump(System.out);
				}
			});
		} catch (Exception e) {
			System.out.println("Exception when constructing document: " + e);
			System.exit(1);
		}

		JButton button = new JButton("apply");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 设置多少字体加粗
				doc.setCharacterAttributes(50, 250, boldStyle, true);
			}

		});

		JScrollPane scrollPane = new JScrollPane(pane);

		JPanel panel = new JPanel();
		panel.add(button, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		f.getContentPane().add(scrollPane, BorderLayout.CENTER);
		f.getContentPane().add(button, BorderLayout.SOUTH);
		f.setSize(400, 300);
		f.setVisible(true);
	}

}
