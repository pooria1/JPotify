package GUI;


import Logic.Save;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PlayLists extends JPanel {
    private BoxLayout layout;
    private JButton addPlaylistButton;
    private Save save = new Save();
    private static ArrayList<String> songs = new ArrayList<>();
    private MusicController musicController;
    private JLabel label;
    private Font font1 = new Font("Font1", Font.ITALIC, 50);
    private Font font2 = new Font("Font2", Font.BOLD, 30);
    private JButton addToPlaylist;
    private JFrame chooseFrame;
    private JButton allMusicsInNewFrame[];
    private String fileName;
    private String clickedButtonName;
    private JButton deletePlayList;
    private static String theCurrentFilePath;
    private int i;

    public PlayLists(MusicController musicController) throws IOException, ClassNotFoundException {
        super();
        this.musicController = musicController;

        save.readPlayListsName();

        layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        Font font = new Font("MyFont", 1, 17);
        JLabel title = new JLabel("      PLAYLISTS");
        title.setForeground(Color.white);
        title.setFont(font);

        deletePlayList = new JButton("-");
        deletePlayList.setFont(font2);
        deletePlayList.setPreferredSize(new Dimension(100, 100));
        deletePlayList.setOpaque(true);
        deletePlayList.setBackground(Color.gray);
        deletePlayList.setFocusPainted(false);
        deletePlayList.setBorderPainted(false);

        addToPlaylist = new JButton("+");
        addToPlaylist.setFont(font2);
        addToPlaylist.setPreferredSize(new Dimension(100, 100));
        addToPlaylist.setOpaque(true);
        addToPlaylist.setBackground(Color.gray);
        addToPlaylist.setFocusPainted(false);
        addToPlaylist.setBorderPainted(false);

        allMusicsInNewFrame = new JButton[100];

        JButton[] buttons = new JButton[50];
        addPlaylistButton = new JButton();
        createButton(addPlaylistButton, "+ Add playlist");
        add(title);
        add(Box.createVerticalStrut(20));

        for (int i = 0; i < save.getPlayListsName().size(); i++) {
            if (i != 0) {
                add(Box.createVerticalStrut(20));
            }
            buttons[i] = new JButton();
            createButton(buttons[i], save.getPlayListsName().get(i));
            add(buttons[i]);
//            if (i==save.getPlayListsName().size()-1)
//                add(Box.createVerticalStrut(25));
//            addPlayList();
        }

        add(addPlaylistButton);
        add(Box.createVerticalStrut(20));
        setOpaque(true);
        setBackground(Color.BLACK);
    }

//    public InteractivePart getInteractivePart() {
//        return interactivePart;
//    }


    public static ArrayList<String> getSongs() {
        return songs;
    }

    public MusicController getMusicController() {
        return musicController;
    }

    public void createButton(JButton button, String name) {

        Font font1 = new Font("Font1", Font.ITALIC, 15);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setText("  " + name);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(font1);
        button.setForeground(Color.WHITE);
        buttonEventHandler(button);
    }

    public void buttonEventHandler(JButton clickedButton) {
        clickedButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == addPlaylistButton) {
                    JFrame f = new JFrame("PlayList");
                    String name = JOptionPane.showInputDialog(f, "Enter Name");
                    try {
                        save.addPlayList(name);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JButton button = new JButton(name);
                    createButton(button, name);
                    remove(addPlaylistButton);
                    add(button);
                    add(Box.createVerticalStrut(20));
//                    revalidate();
//                    repaint();
                    add(addPlaylistButton);
                    add(Box.createVerticalStrut(20));
                    getSongs().clear();
                    try {
                        createAndSaveFile(button.getText().trim());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    revalidate();
                    repaint();

                    for (int i = 0; i < save.getPlayListsName().size(); i++) {
                        System.out.println(save.getPlayListsName().get(i));
                    }
                } else {
                    InteractivePart.setPart(1);
                    getMusicController().clearMusidControler();

                    getMusicController().getTitle().setLayout(new BorderLayout());

                    label = new JLabel("........." + clickedButton.getText() + " playlist  .........");
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setFont(font1);

                    setTheCurrentFilePath(clickedButton.getText().trim());

                    setClickedButtonName(clickedButton.getText().trim());

                    try {
                        getSongs().clear();
                        readFile(clickedButton.getText().trim());

                        //////////////////////////////////////////////////////
                        save.setSortedMusicsCopy(getSongs());
                        //////////////////////////////////////////////////////


                    } catch (IOException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    if (getSongs().size() == 0) {
                        JLabel sorryLabel = new JLabel("Sorry , but there is no music in the playlist ...!");
                        sorryLabel.setFont(font2);
                        sorryLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        getMusicController().getInteractivePart().add(sorryLabel);
                    } else {
                        for (int j = 0; j < save.getSortedMusics().size(); j++) {
                            if (getSongs().contains(save.getSortedMusics().get(j))) {
                                try {
                                    getMusicController().getInteractivePart().makeMusicPad(save.getSortedMusics().get(j));
                                } catch (InvalidDataException | IOException | UnsupportedTagException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }


                    getMusicController().getTitle().add(addToPlaylist, BorderLayout.EAST);
                    getMusicController().getTitle().add(deletePlayList, BorderLayout.WEST);
                    getMusicController().getTitle().add(label, BorderLayout.CENTER);

                    deletePlayList.addActionListener(e13 -> {
                        try {
                            save.deletePlayList(clickedButton.getText().trim());
                            refreshPlayList();
                            getMusicController().getInteractivePart().removeAll();
                            getMusicController().getTitle().removeAll();
                            Files.delete(Paths.get("C:\\Users\\Public\\Documents\\" + clickedButton.getText().trim() + ".ser"));
                            save.getPlayListsName().trimToSize();
                            getMusicController().getInteractivePart().setBackground(Color.gray);
                            getMusicController().getTitle().setBackground(Color.gray);
                            revalidate();
                            repaint();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });

                    addToPlaylist.addActionListener(e12 -> {
                        chooseFrame = new JFrame("All Songs");
                        chooseFrame.setVisible(true);
                        JPanel panel = new JPanel();
                        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                        panel.setLayout(layout);
                        JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        chooseFrame.setSize(600, 600);
                        panel.setBackground(Color.black);
                        Font font1 = new Font("Font2", Font.ITALIC, 20);
                        Font font2 = new Font("Font2", Font.BOLD, 20);
                        JLabel label = new JLabel("  Choose song to add to playlist: ");
                        panel.add(Box.createVerticalStrut(25));
                        panel.add(label);
                        label.setFont(font2);
                        panel.add(Box.createVerticalStrut(25));
                        chooseFrame.pack();

                        for (int counter = 0; counter < save.getSortedMusics().size(); counter++) {
                            try {
                                String songInfo = " +         " + getMusicController().getInteractivePart().findSongInfo(save.getSortedMusics().get(counter), 0) + " - " + getMusicController().getInteractivePart().findSongInfo(save.getSortedMusics().get(counter), 1);
                                allMusicsInNewFrame[counter] = new JButton(songInfo);
                                allMusicsInNewFrame[counter].setHorizontalAlignment(SwingConstants.LEFT);
                                allMusicsInNewFrame[counter].setBorderPainted(false);
                                allMusicsInNewFrame[counter].setFocusPainted(false);
                                allMusicsInNewFrame[counter].setFont(font1);
                                allMusicsInNewFrame[counter].setContentAreaFilled(false);
                                allMusicsInNewFrame[counter].setBackground(Color.GRAY);
                                allMusicsInNewFrame[counter].setForeground(Color.WHITE);
                                panel.add(allMusicsInNewFrame[counter]);
                                panel.add(Box.createVerticalStrut(25));
                                buttonHandler(allMusicsInNewFrame[counter]);
//                                    allMusicsInNewFrame[counter].addActionListener(e12 -> {
//                                        try {
//                                            getMusicController().getInteractivePart().makeMusicPad(save.getSortedMusics().get(counter-1));
//                                            getMusicController().getInteractivePart().revalidate();
//                                            getMusicController().getInteractivePart().repaint();
//                                        } catch (InvalidDataException e1) {
//                                            e1.printStackTrace();
//                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
//                                        } catch (UnsupportedTagException e1) {
//                                            e1.printStackTrace();
//                                        }
//                                    });

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        chooseFrame.add(scrollPane);
                    });
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                clickedButton.setForeground(Color.getHSBColor(104, 69, 55));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                clickedButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                clickedButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clickedButton.setForeground(Color.WHITE);
            }
        });
    }

    public void addPlayList() {
        JPanel playListButton = new JPanel();
        JLabel playListName = new JLabel();
        JButton addMusic = new JButton();
        JButton deletePlayList = new JButton();

        playListButton.setLayout(new FlowLayout());
        add(Box.createVerticalStrut(25));
        add(playListButton);

        playListName.setText("PlayList");
        addMusic.setText("+");
        deletePlayList.setText("-");

        playListName.setPreferredSize(new Dimension(150, 25));
        playListButton.add(playListName);
        playListButton.add(addMusic);
        playListButton.add(deletePlayList);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public void addAndSave(String path, String filename) throws IOException {
        if (!getSongs().contains(path)) {
            getSongs().add(path);
            createAndSaveFile(filename);
        }
    }

    public static void createAndSaveFile(String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Public\\Documents\\" + fileName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(getSongs());
        oos.close();
        fos.close();
    }

    public static void setSongs(ArrayList<String> songs) {
        getSongs().clear();
        PlayLists.songs = songs;
    }

    public static void readFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fisOfArreyList = new FileInputStream("C:\\Users\\Public\\Documents\\" + fileName + ".ser");
        ObjectInputStream oisOfArreyList = new ObjectInputStream(fisOfArreyList);
        setSongs((ArrayList) oisOfArreyList.readObject());
        for (int i = 0; i < getSongs().size(); i++) {
        }
        oisOfArreyList.close();
        fisOfArreyList.close();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    public void buttonHandler(JButton button) {
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e) {
                for (i = 0; i < save.getSortedMusics().size(); i++) {
                    if (e.getSource() == allMusicsInNewFrame[i]) {
                        try {
                            PlayLists.this.getMusicController().getInteractivePart().makeMusicPad(save.getSortedMusics().get(i));
                            PlayLists.this.addAndSave(save.getSortedMusics().get(i), PlayLists.this.getClickedButtonName());
                        } catch (InvalidDataException | IOException | UnsupportedTagException e1) {
                            e1.printStackTrace();
                        }
                        PlayLists.this.getMusicController().getInteractivePart().revalidate();
                        PlayLists.this.getMusicController().getInteractivePart().repaint();
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setForeground(Color.getHSBColor(104, 69, 55));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setForeground(Color.GREEN);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }

        });
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClickedButtonName() {
        return clickedButtonName;
    }

    public void setClickedButtonName(String clickedButtonName) {
        this.clickedButtonName = clickedButtonName;
    }

    public void refreshPlayList() {
        this.removeAll();
        layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        Font font = new Font("MyFont", 1, 17);
        JLabel title = new JLabel("      PLAYLISTS");
        title.setForeground(Color.white);
        title.setFont(font);

        deletePlayList = new JButton("-");
        deletePlayList.setFont(font2);
        deletePlayList.setPreferredSize(new Dimension(100, 100));
        deletePlayList.setOpaque(true);
        deletePlayList.setBackground(Color.gray);
        deletePlayList.setFocusPainted(false);
        deletePlayList.setBorderPainted(false);

        addToPlaylist = new JButton("+");
        addToPlaylist.setFont(font2);
        addToPlaylist.setPreferredSize(new Dimension(100, 100));
        addToPlaylist.setOpaque(true);
        addToPlaylist.setBackground(Color.gray);
        addToPlaylist.setFocusPainted(false);
        addToPlaylist.setBorderPainted(false);

        allMusicsInNewFrame = new JButton[100];

        JButton[] buttons = new JButton[50];
        addPlaylistButton = new JButton();
        createButton(addPlaylistButton, "+ Add playlist");
        add(title);
        add(Box.createVerticalStrut(20));

        for (int i = 0; i < save.getPlayListsName().size(); i++) {
            if (i != 0) {
                add(Box.createVerticalStrut(20));
            }
            buttons[i] = new JButton();
            createButton(buttons[i], save.getPlayListsName().get(i));
            add(buttons[i]);
//            if (i==save.getPlayListsName().size()-1)
//                add(Box.createVerticalStrut(25));
//            addPlayList();
        }

        add(addPlaylistButton);
        add(Box.createVerticalStrut(20));
        setOpaque(true);
        setBackground(Color.BLACK);
        revalidate();
        repaint();
    }

    public static void setTheCurrentFilePath(String theCurrentFilePath) {
        PlayLists.theCurrentFilePath = theCurrentFilePath;
    }

    public static String getTheCurrentFilePath() {
        return theCurrentFilePath;
    }
}


