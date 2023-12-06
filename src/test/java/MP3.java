

//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.util.*;
//import javax.swing.*;
//import javax.media.bean.playerbean.*; //这个包要用到JMF

import javax.media.bean.playerbean.MediaPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.Vector;

public class MP3 extends JFrame implements Runnable {
    public JLabel time;

    private JSlider sldDiameter;

    public MediaPlayer player;

    public JButton buttonPlay, buttonLoop, buttonStop;

    public JButton buttonAdd, buttonDel, buttonSave, buttonRead;

    public JPanel jp1, jp2, jp3, jp4;

    public JList playList;

    int zongmiao = 0;

    public Vector vector, names;

    boolean fo = false, geshi = false;

    JLabel jl1, jl2, sj1, sj2;

    JTextField jt1, jt2;

    JButton
//            queding,
            xiugai;

//    int zong = 0;

    int a = 0, b = 0, you = 1, mm = 0;

    int minutes, seconds;

    public MP3() {
        super("java简单音乐播放器--严楷");
        player = new MediaPlayer();
        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        names = new Vector();
        jp1 = new JPanel();
        time = new JLabel();
        jp1.add(time);
        c.add(jp1);
        buttonPlay = new JButton("开始播放");
        buttonLoop = new JButton("循环播放");
        buttonStop = new JButton("停止播放");
        jp2 = new JPanel();
        jp2.add(buttonPlay);
        jp2.add(buttonLoop);
        jp2.add(buttonStop);
        c.add(jp2);
        jp4 = new JPanel();
        sj1 = new JLabel();
        sj2 = new JLabel();
        sldDiameter = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
        sldDiameter.setMajorTickSpacing(1);
        sldDiameter.setPaintTicks(true);
        jp4.add(sj1);
        jp4.add(sldDiameter);
        jp4.add(sj2);
        c.add(jp4);
        vector = new Vector();
        playList = new JList(names);
        playList.setVisibleRowCount(5);
        playList.setFixedCellHeight(40);
        playList.setFixedCellWidth(265);
        playList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        c.add(new JScrollPane(playList));
        buttonAdd = new JButton("添加");
        buttonDel = new JButton("删除");
        buttonRead = new JButton("读取");
        buttonSave = new JButton("保存");
        jp3 = new JPanel();
        jp3.add(buttonAdd);
        jp3.add(buttonDel);
        jp3.add(buttonSave);
        jp3.add(buttonRead);
        c.add(jp3);

        try {
//            String s = " C:/Users/Administrator/Music/卓依婷 - 萍聚.mp3";
//            ObjectInputStream input = new ObjectInputStream(
//                    new FileInputStream(s));
//            lujin a1 = (lujin) input.readObject();
//            names = a1.b;
//            vector = a1.a;
            playList.setListData(names);
        } catch (Exception e) {
            System.out.println("c盘没有保存音乐文件");
            //e.printStackTrace();
        }

        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser(); // 实例化文件选择器
                fileChooser
                        .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 设置文件选择模式,此处为文件和目录均可
                if (fileChooser.showSaveDialog(MP3.this) == JFileChooser.APPROVE_OPTION) { // 弹出文件选择器,并判断是否点击了打开按钮
                    String fileName = fileChooser.getSelectedFile()
                            .getAbsolutePath(); // 得到选择文件或目录的绝对路径
//                    mmm(vector, names, fileName);
                }
            }
        });

        buttonRead.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser(); // 实例化文件选择器
                fileChooser
                        .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 设置文件选择模式,此处为文件和目录均可
                if (fileChooser.showOpenDialog(MP3.this) == JFileChooser.APPROVE_OPTION) { // 弹出文件选择器,并判断是否点击了打开按钮
                    String fileName = fileChooser.getSelectedFile()
                            .getAbsolutePath(); // 得到选择文件或目录的绝对路径
                    try {
                        ObjectInputStream input = new ObjectInputStream(
                                new FileInputStream(fileName));
//                        lujin a1 = (lujin) input.readObject();
//                        names = a1.b;
//                        vector = a1.a;
                        playList.setListData(names);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (playList.getSelectedIndex() >= 0) {
                    String yy = (String) vector.get(playList.getSelectedIndex());
                    File ff = new File(yy);
                    if (ff.exists()) {
                        if (yy.matches("[\\S\\s]*.mp3") || yy.matches("[\\S\\s]*.MP3")) {
                            if (player != null) {
                                a = 0;
                                b = 0;
                                you = 0;
                                player.stop();
                            }
                            player.setMediaLocation("file:/" + yy);
                            fo = true;
                            player.start();
                            geshi = true;
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException eee) {
                            }
                            zongmiao = (int) player.getDuration().getSeconds();
                            if (zongmiao > 10000) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ew) {
                                }
                                zongmiao = (int) player.getDuration().getSeconds();
                            }
                            zongmiao = (int) player.getDuration().getSeconds();
                            String aa = fen(zongmiao);
                            sj2.setText(aa);
                        } else
                            JOptionPane.showMessageDialog(null,
                                    "播放文件格式的有错,无法播放 建议删除");
                    } else
                        JOptionPane.showMessageDialog(null,
                                "此歌曲文件已经不存在,建议删除");
                } else
                    JOptionPane.showMessageDialog(null, "请选择音乐文件");
            }
        });
        buttonLoop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (playList.getSelectedIndex() >= 0) {
                    String yy = (String) vector.get(playList.getSelectedIndex());
                    File ff = new File(yy);
                    if (ff.exists()) {
                        if (yy.matches("[\\S\\s]*.mp3") || yy.matches("[\\S\\s]*.MP3")) {
                            if (player != null) {
                                a = 0;
                                b = 0;
                                you = 0;
                                player.stop();
                            }
                            player.setMediaLocation("file:/" + yy);
                            fo = true;
                            player.start();
                            geshi = true;
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException we) {
                            }
                            zongmiao = (int) player.getDuration().getSeconds();
                            if (zongmiao > 10000) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException we) {
                                }
                                zongmiao = (int) player.getDuration().getSeconds();
                            }
                            zongmiao = (int) player.getDuration().getSeconds();
                            String aa = fen(zongmiao);
                            sj2.setText(aa);
                        } else
                            JOptionPane.showMessageDialog(null,
                                    "播放文件格式的有错,无法播放 建议删除");
                    } else
                        JOptionPane.showMessageDialog(null,
                                "此歌曲文件已经不存在,建议删除");

                } else
                    JOptionPane.showMessageDialog(null, "请选择音乐文件");
            }
        });
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                a = 0;
                b = 0;
                you = 0;
                mm = 0;
                geshi = false;
                fo = false;
                sldDiameter.setMaximum(100);
                sldDiameter.setValue(0);
                sj1.setText(null);
                sj2.setText(null);
                if (playList.getSelectedIndex() >= 0)
                    player.stop();

            }
        });
        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileChooser = new JFileChooser(); // 实例化文件选择器
                fileChooser
                        .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 设置文件选择模式,此处为文件和目录均可
                fileChooser.setCurrentDirectory(new File(".")); // 设置文件选择器当前目录
                fileChooser
                        .setFileFilter(new javax.swing.filechooser.FileFilter() {
                            public boolean accept(File file) { // 可接受的文件类型
                                String name = file.getName().toLowerCase();
                                return name.endsWith(".mp3")
                                        || file.isDirectory();
                            }

                            public String getDescription() { // 文件描述
                                return "音乐文件(*.mp3)";
                            }
                        });
                if (fileChooser.showOpenDialog(MP3.this) == JFileChooser.APPROVE_OPTION) { // 弹出文件选择器,并判断是否点击了打开按钮
                    String fileName = fileChooser.getSelectedFile()
                            .getAbsolutePath(); // 得到选择文件或目录的绝对路径
                    vector.add(fileName);
                    StringBuffer buffer = daoxu(fileName);
                    names.add(buffer);
                }
            }
        });
        buttonDel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (playList.getSelectedIndex() >= 0) {
                    names.removeElementAt(playList.getSelectedIndex());
                    vector.removeElementAt(playList.getSelectedIndex());
                    playList.setListData(names);
                }
            }
        });
        playList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    if (playList.getSelectedIndex() >= 0) {
                        String yy = (String) vector.get(playList.getSelectedIndex());
                        File ff = new File(yy);
                        if (ff.exists()) {
                            if (yy.matches("[\\S\\s]*.mp3") || yy.matches("[\\S\\s]*.MP3")) {
                                if (player != null) {
                                    a = 0;
                                    b = 0;
                                    you = 0;
                                    player.stop();
                                }
                                player.setMediaLocation("file:/" + yy);
                                fo = true;
                                player.start();
                                geshi = true;
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                }
                                zongmiao = (int) player.getDuration().getSeconds();
                                if (zongmiao > 10000) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                    }
                                    zongmiao = (int) player.getDuration().getSeconds();
                                }
                                String aa = fen(zongmiao);
                                sj2.setText(aa);
                            } else
                                JOptionPane.showMessageDialog(null,
                                        "播放文件格式的有错,无法播放 建议删除");
                        } else
                            JOptionPane.showMessageDialog(null,
                                    "此歌曲文件已经不存在,建议删除");

                    }
                }
                if (event.isMetaDown()) {
                    if (playList.getSelectedIndex() >= 0) {
                        int a = playList.getSelectedIndex();
                        xiugai x = new xiugai();
                        jt1.setText(names.get(playList.getSelectedIndex()) + "");
                        jt2.setText(vector.get(playList.getSelectedIndex()) + "");
                    }
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String s = "c:\\music.txt";
//                mmm(vector, names, s);
                System.exit(0);
            }
        });
        setSize(300, 400);
        setVisible(true);
    }

    public void run() {
        while (true) {
            Date now = new Date();
            //shijian.setText("当前时间: " + now.getYear()+"年"+now.getMonth()+"月"+now.getDate()+"日"+now.getTime());
            time.setText("当前时间：" + (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + "   严楷");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            if (geshi) {
                you = (int) player.getMediaTime().getSeconds();
                mm = you % 60;
                if (you != 0)
                    if (you % 60 == 0) {
                        b++;
                    }
                sj1.setText(b + ":" + mm);
                sldDiameter.setMaximum(zongmiao);
                if (you != zongmiao)
                    sldDiameter.setValue(you);
                else {
                    sldDiameter.setValue(0);
                    mm = 0;
                    b = 0;
                }
            }
        }
    }

    public StringBuffer daoxu(String fileName) {
        String mc = "";
        for (int i = fileName.length(); i >= 1; i--) {
            if (fileName.charAt(i - 1) == '\\')
                break;
            mc += fileName.charAt(i - 1);
        }
        StringBuffer buffer = new StringBuffer(mc);
        StringBuffer mm = buffer.reverse();
        return mm;
    }

//    public void mmm(Vector vector, Vector mingcheng, String lujin) {
//        lujin a = new lujin(vector, mingcheng);
//        try {
//            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(lujin));
//            output.writeObject(a);
//            output.flush();
//            output.close();
//        } catch (Exception e) {
//
//        }
//    }

    public String fen(int yy) {
        minutes = (int) yy / 60;
        seconds = (int) yy % 60;
        String sss = minutes + ":" + seconds;
        return sss;
    }

    public static void main(String agrs[]) {
        MP3 s = new MP3();
        Thread t1 = new Thread(s);
        t1.start();
        s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class xiugai extends JFrame {
        public xiugai() {
            jl1 = new JLabel("文件名");
            jt1 = new JTextField(20);
            jl2 = new JLabel("文件路径");
            jt2 = new JTextField(20);
            xiugai = new JButton("修改");
            Container c = getContentPane();
            c.setLayout(new GridLayout(3, 1));
            JPanel j1 = new JPanel();
            JPanel j2 = new JPanel();
            JPanel j3 = new JPanel();
            j1.add(jl1);
            j1.add(jt1);
            j2.add(jl2);
            j2.add(jt2);
            j3.add(xiugai);
            c.add(j1);
            c.add(j2);
            c.add(j3);
            xiugai.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    names.setElementAt(jt1.getText(), playList.getSelectedIndex());
                    vector.setElementAt(jt2.getText(), playList.getSelectedIndex());
                    playList.setListData(names);
                    dispose();
                }
            });
            setSize(300, 120);
            setVisible(true);
        }
    }
}

//class lujin implements Serializable {
//
//    Vector a = new Vector();
//
//    Vector b = new Vector();
//
//    public lujin(Vector vector, Vector mingcheng) {
//        a = vector;
//        b = mingcheng;
//    }
//
//}