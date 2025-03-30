package guis;

import navigate.LeagueSelectionDialog;
import navigate.NewsGUI;
import navigate.ProfileGUI;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PersonalAccount extends JFrame {
    private JPanel sidePanel;
    private boolean expanded = false;
    private JPanel contentPanel;
    private String favoriteLeague = "";
    private static String favoriteClub = "";
    private static String clubLogoUrl;
    private boolean isLeagueSelectionDialogOpened = false;

    public PersonalAccount() {
        setTitle("Личный кабинет");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(60, getHeight()));
        sidePanel.setBackground(Color.DARK_GRAY);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        addMenuItem("Профиль", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\user.png");
        addMenuItem("Новости футбола", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\newspaper-open.png");
        addMenuItem("Мой клуб", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\football.png");
        addMenuItem("Следим за игроками", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\football-player.png");
        addMenuItem("Цены и трансферы", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\usd-circle.png");
        addMenuItem("Календарь матчей", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\icons\\calendar.png");

        add(sidePanel, BorderLayout.WEST);
        setupPanel();

        showWelcomeMessage(); // Показать приветственное сообщение

        sidePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                expandPanel();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (expanded && e.getX() > 200) {
                    collapsePanel();
                }
            }
        });
        setVisible(true);
    }

    private void addMenuItem(String title, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(title);
        textLabel.setForeground(Color.WHITE);
        textLabel.setVisible(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setPreferredSize(new Dimension(200, 50));
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.add(iconLabel);
        buttonPanel.add(textLabel);

        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                expandPanel();
                textLabel.setForeground(Color.YELLOW);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                textLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showContent(title);
            }
        });

        sidePanel.add(buttonPanel);
    }

    private void showWelcomeMessage() {
        contentPanel.removeAll();
        JLabel welcomeLabel = new JLabel("<html><center><h1>Добро пожаловать!</h1><br>Выберите раздел в меню слева</center></html>", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setupPanel() {
        sidePanel.setPreferredSize(new Dimension(60, getHeight()));

        for (Component comp : sidePanel.getComponents()) {
            if (comp instanceof JPanel panel) {
                panel.setLayout(null);

                if (panel.getComponentCount() > 1) {
                    JLabel iconLabel = (JLabel) panel.getComponent(0);
                    JLabel textLabel = (JLabel) panel.getComponent(1);

                    iconLabel.setBounds(10, 10, 40, 40);
                    textLabel.setBounds(60, 10, 120, 40);
                    textLabel.setVisible(false);
                }
            }
        }

        sidePanel.revalidate();
        sidePanel.repaint();
        expanded = false;
    }

    public static void setClubLogoUrl(String url) {
        clubLogoUrl = url;
    }

    public static String getClubLogoUrl() {
        return clubLogoUrl;
    }

    private void expandPanel() {
        if (!expanded) {
            sidePanel.setPreferredSize(new Dimension(200, getHeight()));

            for (Component comp : sidePanel.getComponents()) {
                if (comp instanceof JPanel panel) {
                    panel.setLayout(null);

                    if (panel.getComponentCount() > 1) {
                        JLabel iconLabel = (JLabel) panel.getComponent(0);
                        JLabel textLabel = (JLabel) panel.getComponent(1);

                        iconLabel.setBounds(10, 10, 40, 40);
                        textLabel.setBounds(60, 10, 120, 40);
                        textLabel.setVisible(true);
                    }
                }
            }

            sidePanel.revalidate();
            sidePanel.repaint();
            expanded = true;
        }
    }

    private void collapsePanel() {
        if (expanded) {
            sidePanel.setPreferredSize(new Dimension(60, getHeight()));

            for (Component comp : sidePanel.getComponents()) {
                if (comp instanceof JPanel panel) {
                    panel.setLayout(null);

                    if (panel.getComponentCount() > 1) {
                        JLabel iconLabel = (JLabel) panel.getComponent(0);
                        JLabel textLabel = (JLabel) panel.getComponent(1);

                        iconLabel.setBounds(10, 10, 40, 40);
                        textLabel.setVisible(false);
                    }
                }
            }

            sidePanel.revalidate();
            sidePanel.repaint();
            expanded = false;
        }
    }

    private void showContent(String section) {
        contentPanel.removeAll();

        if ("Профиль".equals(section)) {
            if (!isLeagueSelectionDialogOpened) {
                LeagueSelectionDialog dialog = new LeagueSelectionDialog(this);
                favoriteLeague = dialog.getSelectedLeague();
                favoriteClub = dialog.getSelectedClub();

                if (clubLogoUrl == null || clubLogoUrl.equals("URL_ЗАГЛУШКИ")) {
                    clubLogoUrl = fetchClubLogo(favoriteClub); // Обновляем логотип, только если он пустой
                }

                isLeagueSelectionDialogOpened = true;
            }

            // Передаем сохраненный логотип
            contentPanel.add(new ProfileGUI(favoriteLeague, favoriteClub, clubLogoUrl, null));
        } else if ("Новости футбола".equals(section)) {
            contentPanel.add(new NewsGUI(null));
        } else {
            contentPanel.add(new JLabel("Раздел " + section + " в разработке", SwingConstants.CENTER));
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }


    public String fetchClubLogo(String clubName) {
        try {
            // Создаем URL для запроса
            String urlString = "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=" + clubName.replace(" ", "%20");
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json"); // Добавляем заголовок
            connection.connect();

            // Читаем ответ от API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            String jsonResponse = response.toString();

            // Выводим API-ответ в консоль
            System.out.println("API Response: \"" + jsonResponse + "\"");

            // Проверяем, что jsonResponse не пустой
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                System.err.println("Ошибка: пустой ответ от API");
                return "URL_ЗАГЛУШКИ";
            }

            // Проверяем, начинается ли ответ с "{", что указывает на JSON-объект
            if (!jsonResponse.trim().startsWith("{")) {
                System.err.println("Ошибка: API вернул не JSON, а строку -> " + jsonResponse);
                return "URL_ЗАГЛУШКИ";
            }

            // Парсим JSON-ответ
            JSONObject responseObj = new JSONObject(jsonResponse);
            if (responseObj.has("teams")) {
                JSONArray teamsArray = responseObj.getJSONArray("teams");
                if (teamsArray.length() > 0) {
                    JSONObject teamObj = teamsArray.getJSONObject(0);
                    if (teamObj.has("strBadge")) {
                        return teamObj.getString("strBadge");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "URL_ЗАГЛУШКИ";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PersonalAccount account = new PersonalAccount();
            account.setVisible(true);
        });
    }
}
