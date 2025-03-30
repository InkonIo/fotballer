package navigate;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class ProfileGUI extends JPanel {
    private JLabel profilePicLabel;
    private JLabel favoriteClubLabel;
    private JLabel clubLogoLabel;
    private static ImageIcon savedProfileIcon = null;
    private static String savedNickname = "Ваш никнейм";
    private static String savedFavoriteClub = null;
    private static String savedClubLogoUrl = null;
    private JTextField nicknameField;
    private JButton confirmButton;
    private static final String PROFILE_PIC_PATH = "profile_pic.txt";

    public ProfileGUI(String league, String club, String clubLogoUrl, JFrame parent) {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        headerPanel.setBackground(new Color(30, 30, 30));

        // --- Фото профиля (с рамкой) ---
        profilePicLabel = new JLabel(getDefaultProfileImage());
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        profilePicLabel.setPreferredSize(new Dimension(120, 120));
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        profilePicLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseProfilePicture();
            }
        });

        // --- Поле для никнейма ---
        nicknameField = new JTextField(15);
        nicknameField.setFont(new Font("Arial", Font.BOLD, 16));
        nicknameField.setBackground(new Color(50, 50, 50));
        nicknameField.setForeground(Color.WHITE);
        nicknameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        nicknameField.setEditable(false);

        nicknameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nicknameField.setEditable(true);
                nicknameField.setBackground(Color.WHITE);
                nicknameField.setForeground(Color.BLACK);
                confirmButton.setVisible(true);
            }
        });

        // --- Кнопка "✔" (подтверждение никнейма) ---
        confirmButton = new JButton("✔");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 12));
        confirmButton.setBackground(new Color(50, 205, 50));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setVisible(false);
        confirmButton.setPreferredSize(new Dimension(40, 30));

        confirmButton.addActionListener(e -> {
            savedNickname = nicknameField.getText();
            nicknameField.setEditable(false);
            nicknameField.setBackground(new Color(50, 50, 50));
            nicknameField.setForeground(Color.WHITE);
            confirmButton.setVisible(false);
        });

        headerPanel.add(profilePicLabel);
        headerPanel.add(nicknameField);
        headerPanel.add(confirmButton);
        add(headerPanel, BorderLayout.NORTH);

        // --- Центральная панель (Любимый клуб) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(30, 30, 30));

        JLabel welcomeLabel = new JLabel("Добро пожаловать в профиль!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel clubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clubPanel.setBackground(new Color(30, 30, 30));
        clubLogoLabel = new JLabel();

        favoriteClubLabel = new JLabel("Любимый клуб: " + (savedFavoriteClub != null ? savedFavoriteClub : "Не выбран"));
        favoriteClubLabel.setFont(new Font("Arial", Font.BOLD, 16));
        favoriteClubLabel.setForeground(Color.WHITE);

        clubPanel.add(clubLogoLabel);
        clubPanel.add(favoriteClubLabel);
        centerPanel.add(welcomeLabel, BorderLayout.NORTH);
        centerPanel.add(clubPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Нижняя панель (Кнопка назад) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));

        JButton backButton = new JButton("Назад");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(70, 70, 70));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> this.setVisible(false));

        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updateFavoriteClub(club, clubLogoUrl);
        loadProfilePicture();
        loadNickname();
    }

    public void updateFavoriteClub(String club, String clubLogoUrl) {
        if (club != null) {
            savedFavoriteClub = club;
            savedClubLogoUrl = clubLogoUrl;
        }

        SwingUtilities.invokeLater(() -> {
            favoriteClubLabel.setText("Любимый клуб: " + (savedFavoriteClub != null ? savedFavoriteClub : "Не выбран"));

            if (savedClubLogoUrl != null && !savedClubLogoUrl.isEmpty()) {
                try {
                    ImageIcon clubIcon = new ImageIcon(new URL(savedClubLogoUrl));
                    clubLogoLabel.setIcon(new ImageIcon(clubIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
                } catch (Exception e) {
                    System.out.println("Ошибка загрузки логотипа: " + e.getMessage());
                    clubLogoLabel.setText("Ошибка загрузки логотипа");
                }
            } else {
                clubLogoLabel.setText("Нет логотипа");
            }
        });
    }

    private void loadNickname() {
        nicknameField.setText(savedNickname != null ? savedNickname : "");
    }

    private void chooseProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            savedProfileIcon = new ImageIcon(selectedFile.getAbsolutePath());
            profilePicLabel.setIcon(scaleImage(savedProfileIcon, 120, 120));
            profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

            // Сохраняем путь в файл
            saveProfilePicturePath(selectedFile.getAbsolutePath());
        }
    }

    private void loadProfilePicture() {
        File file = new File(PROFILE_PIC_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String path = reader.readLine();
                if (path != null && !path.isEmpty()) {
                    savedProfileIcon = new ImageIcon(path);
                    profilePicLabel.setIcon(scaleImage(savedProfileIcon, 120, 120));
                }
            } catch (IOException e) {
                System.out.println("Ошибка загрузки аватарки: " + e.getMessage());
            }
        }
    }

    private void saveProfilePicturePath(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_PIC_PATH))) {
            writer.write(path);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения аватарки: " + e.getMessage());
        }
    }

    private ImageIcon getDefaultProfileImage() {
        int width = 120, height = 120;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Задаем стиль текста
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        String text = "Выбери аву!";
        int textWidth = fm.stringWidth(text);
        int textX = (width - textWidth) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();

        // Отрисовываем рамку и текст
        g.drawRect(0, 0, width - 1, height - 1);
        g.drawString(text, textX, textY);
        g.dispose();

        return new ImageIcon(img);
    }

    private ImageIcon scaleImage(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}