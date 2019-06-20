package GUI;

import Logic.RunMusic;
import Logic.Save;
import com.mpatric.mp3agic.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class CentralPanel extends JPanel {
    private static String path;
    private static Thread thread = null;
    private InteractivePart interactivePart;

    public CentralPanel(SongInfo songInfo) throws IOException, InvalidDataException, UnsupportedTagException, JavaLayerException {
        setLayout(new BorderLayout());

        TitleBar titleBar = new TitleBar();
        interactivePart = new InteractivePart(songInfo);

        add(titleBar, BorderLayout.NORTH);
        add(interactivePart, BorderLayout.CENTER);

    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        CentralPanel.path = path;
    }

    public static Thread getThread() {
        return thread;
    }

    public static void setThread(Thread thread) {
        CentralPanel.thread = thread;
    }

    public InteractivePart getInteractivePart() {
        return interactivePart;
    }
}

class TitleBar extends JPanel implements MouseListener {
    private JPanel searchBar;
    private JPanel emptySpace;
    private JPanel id;
    private JLabel line;
    private JButton previousBtn;
    private JButton nextBtn;
    private JTextField searchField;
    private JLabel idLabel;

    public TitleBar() throws IOException, JavaLayerException {
        super();
        setOpaque(true);
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(700, 40));

        searchBar = new JPanel();
        emptySpace = new JPanel();
        id = new JPanel();
        line = new JLabel();

        setLayout(new BorderLayout());

        setSearchBarGUI();
        setIdGUI();
        setEmptySpaceGUI();
//        setLineGUI();

        add(searchBar, BorderLayout.WEST);
        add(emptySpace, BorderLayout.CENTER);
        add(id, BorderLayout.EAST);
//        add(line,BorderLayout.SOUTH);

    }

    public void setSearchBarGUI() throws IOException, JavaLayerException {
        previousBtn = new JButton();
        nextBtn = new JButton();
        searchField = new JTextField(" Search");

        searchBar.setOpaque(true);
        searchBar.setBackground(Color.GRAY);

        searchBar.setPreferredSize(new Dimension(320, 40));

        searchBar.setLayout(new BorderLayout());

        previousBtn.setPreferredSize(new Dimension(40, 50));
        searchField.setPreferredSize(new Dimension(240, 50));
        previousBtn.setPreferredSize(new Dimension(40, 40));
        searchField.setPreferredSize(new Dimension(240, 40));

        Image img = ImageIO.read(getClass().getResource("icons\\top-screen-icons-2\\png\\002-left-arrow.png"));
        img = img.getScaledInstance(14, 14, java.awt.Image.SCALE_SMOOTH);
        previousBtn.setIcon(new ImageIcon(img));
        previousBtn.setPreferredSize(new Dimension(40, 40));
        previousBtn.setBorderPainted(false);
        previousBtn.setContentAreaFilled(false);
        previousBtn.setFocusPainted(false);

//        RunMusic runMusic=new RunMusic(CentralPanel.getPath());
//        Thread thread=new Thread(runMusic);

        previousBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (CentralPanel.getThread().isAlive()) {
//                    runMusic.resume(thread);
                    CentralPanel.getThread().resume();
                } else {
                    CentralPanel.getThread().start();
                }
            }
        });

        img = ImageIO.read(getClass().getResource("icons\\top-screen-icons-2\\png\\001-right-arrow.png"));
        img = img.getScaledInstance(14, 14, java.awt.Image.SCALE_SMOOTH);
        nextBtn.setIcon(new ImageIcon(img));
        nextBtn.setPreferredSize(new Dimension(40, 40));
        nextBtn.setBorderPainted(false);
        nextBtn.setContentAreaFilled(false);
        nextBtn.setFocusPainted(false);

        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
//                    runMusic.stopThread(thread);
                    CentralPanel.getThread().stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


        searchBar.add(previousBtn, BorderLayout.WEST);
        searchBar.add(nextBtn, BorderLayout.CENTER);
        searchBar.add(searchField, BorderLayout.EAST);

    }

    public void setIdGUI() {
        id.setOpaque(true);
        id.setBackground(Color.WHITE);
        id.setPreferredSize(new Dimension(100, 40));

        Font font1 = new Font("Font1", 1, 20);

        idLabel = new JLabel("9731***");
        id.setFont(font1);

        id.add(idLabel);

        searchField.addMouseListener(this);
    }

    public void setLineGUI() {
        line.setText("_______________________________________________________________________________________________________________________");
        line.setForeground(Color.DARK_GRAY);
    }

    public void setEmptySpaceGUI() {
        emptySpace.setOpaque(true);
        emptySpace.setBackground(Color.lightGray);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (searchField.getText().equals(" Search")) {
            searchField.setText("");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}


class InteractivePart extends JPanel {
    private static int gridX = 0;
    private static int gridY = 0;
    private SongInfo songInfo;

    public InteractivePart(SongInfo songInfo) throws IOException, InvalidDataException, UnsupportedTagException {
        super();
        this.songInfo = songInfo;

        setOpaque(true);
        setBackground(Color.GRAY);

        setLayout(new GridBagLayout());

        Save save = new Save();
        save.readFile();
//        for (Map.Entry<String, Boolean> entry :
//                save.getMusics().entrySet()) {
//            makeMusicPad(entry.getKey());
//            System.out.println(entry.getKey() + "-----------------");
//        }

        for (int i = 0; i < save.getSortedMusics().size(); i++) {
            makeMusicPad(save.getSortedMusics().get(i));
        }
    }


//    Image img = ImageIO.read(getClass().getResource("icons\\fence_notes_music_staff_139053_3840x2160.jpg"));
//    public void paintComponent(Graphics g)
//    {
//        g.drawImage(img,0,0,null);
//        repaint();
//    }


    public String findSongInfo(String filePath, int index) throws IOException {
        File file = new File(filePath);
        byte[] songName = new byte[30];
        byte[] fileContent = Files.readAllBytes(file.toPath());
        for (int i = 0; i < 30; i++) {
            songName[i] = fileContent[i + fileContent.length - 125 + index * 30];
        }
        String name = new String(songName);
        System.out.println(name);
        return name;
    }


    public void showCoverImage(Container container, String path) throws InvalidDataException, IOException, UnsupportedTagException {
        JLabel label = new JLabel();

        Mp3File song = new Mp3File(path);
        if (song.hasId3v2Tag()) {
            ID3v2 id3v2tag = song.getId3v2Tag();
            byte[] imageData = id3v2tag.getAlbumImage();
            if (imageData != null) {
                System.out.println("debug:: imageData is not null");
                Image img = ImageIO.read(new ByteArrayInputStream(imageData));
                img = img.getScaledInstance(400, 230, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(img);
                label.setIcon(icon);
            }
        }

        container.add(label);
    }

    public void makeMusicPad(String path) throws InvalidDataException, IOException, UnsupportedTagException {

        JPanel panel = new JPanel();
        JPanel coverImage = new JPanel();
        JButton artistName = new JButton();
        JButton albumName = new JButton();

        panel.setLayout(new BorderLayout());

        panel.setOpaque(true);
        panel.setBackground(Color.DARK_GRAY);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 20, 20, 20);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 2;
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.ipadx = 200;
        constraints.ipady = 0;
        gridX++;
        if (gridX == 5) {
            gridX = 0;
            gridY++;
        }
        add(panel, constraints);

        Font font = new Font("font", 1, 17);

        coverImage.setPreferredSize(new Dimension(200, 200));
        albumName.setPreferredSize(new Dimension(200, 35));

        coverImage.setOpaque(true);
        coverImage.setBackground(Color.GRAY);

        artistName.setOpaque(true);
        artistName.setBackground(Color.DARK_GRAY);
        artistName.setForeground(Color.white);
        artistName.setContentAreaFilled(false);
        artistName.setFocusPainted(false);
        artistName.setBorderPainted(false);
        artistName.setFont(font);
        artistName.setHorizontalAlignment(SwingConstants.LEFT);

        albumName.setOpaque(true);
        albumName.setBackground(Color.DARK_GRAY);
        albumName.setForeground(Color.white);
        albumName.setContentAreaFilled(false);
        albumName.setFocusPainted(false);
        albumName.setBorderPainted(false);
        albumName.setHorizontalAlignment(SwingConstants.LEFT);


        showCoverImage(coverImage, path);
        artistName.setText(findSongInfo(path, 0));
        albumName.setText(findSongInfo(path, 2));


        panel.add(coverImage, BorderLayout.NORTH);
        panel.add(artistName, BorderLayout.CENTER);
        panel.add(albumName, BorderLayout.SOUTH);

        Save save=new Save();
        save.readFile();

        artistName.addActionListener(e -> {
            if (CentralPanel.getThread() != null) {
                CentralPanel.getThread().stop();
            }
            CentralPanel.setPath(path);
            RunMusic runMusic = new RunMusic(CentralPanel.getPath());
            Thread thread = new Thread(runMusic);
            CentralPanel.setThread(thread);
            try {
                if (songInfo != null) {
                    songInfo.changeSongInfo(path);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InvalidDataException e1) {
                e1.printStackTrace();
            } catch (UnsupportedTagException e1) {
                e1.printStackTrace();
            }
            save.deleteAndReAddMusic(path);
            save.saveToFile();

        });

        albumName.addActionListener(e -> {
            if (CentralPanel.getThread() != null) {
                CentralPanel.getThread().stop();
            }
            CentralPanel.setPath(path);
            RunMusic runMusic = new RunMusic(CentralPanel.getPath());
            Thread thread = new Thread(runMusic);
            CentralPanel.setThread(thread);
            try {
                if (songInfo != null) {
                    songInfo.changeSongInfo(path);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InvalidDataException e1) {
                e1.printStackTrace();
            } catch (UnsupportedTagException e1) {
                e1.printStackTrace();
            }
            save.deleteAndReAddMusic(path);
            save.saveToFile();

        });

        coverImage.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (CentralPanel.getThread() != null) {
                    CentralPanel.getThread().stop();
                }
                CentralPanel.setPath(path);
                RunMusic runMusic = new RunMusic(CentralPanel.getPath());
                Thread thread = new Thread(runMusic);
                CentralPanel.setThread(thread);
                try {
                    if (songInfo != null) {
                        songInfo.changeSongInfo(path);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidDataException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedTagException e1) {
                    e1.printStackTrace();
                }
                save.deleteAndReAddMusic(path);
                save.saveToFile();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

}