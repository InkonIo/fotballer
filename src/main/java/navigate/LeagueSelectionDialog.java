package navigate;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class LeagueSelectionDialog extends JDialog {
    private JComboBox<String> leagueDropdown;
    private JComboBox<String> clubDropdown;
    private HashMap<String, String[]> leagues;
    private HashMap<String, String> leagueLogos;
    private JLabel leagueLogoLabel;
    private JLabel clubLogoLabel;
    private HashMap<String, String> clubLogos;


    public LeagueSelectionDialog(JFrame parent) {

        super(parent, "Select League", true);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(230, 230, 250));

        leagues = new HashMap<>();
        leagueLogos = new HashMap<>();
        initializeLeagues();

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        leagueLogoLabel = new JLabel();
        leagueLogoLabel.setPreferredSize(new Dimension(100, 100));

        leagueDropdown = new JComboBox<>(leagues.keySet().toArray(new String[0]));
        styleDropdown(leagueDropdown);
        leagueDropdown.addActionListener(e -> updateLeagueAndClub());

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(leagueLogoLabel, gbc);

        gbc.gridx = 1;
        topPanel.add(leagueDropdown, gbc);

        clubLogoLabel = new JLabel();
        clubLogoLabel.setPreferredSize(new Dimension(100, 100));

        clubDropdown = new JComboBox<>();
        styleDropdown(clubDropdown);
        updateClubDropdown("Выберите лигу"); // Устанавливаем placeholder
        clubDropdown.addActionListener(e -> updateClubLogo());

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(clubLogoLabel, gbc);

        gbc.gridx = 1;
        topPanel.add(clubDropdown, gbc);

        JButton confirmButton = new JButton("Подтвердить");
        styleButton(confirmButton);
        confirmButton.addActionListener(e -> {
            String selectedLeague = (String) leagueDropdown.getSelectedItem();
            String selectedClub = (String) clubDropdown.getSelectedItem();

            if (selectedClub != null && !selectedClub.equals("Выберите клуб")) {
                String clubLogoUrl = fetchClubLogoURL(selectedClub); // Получаем URL логотипа
                dispose();
                new ProfileGUI(selectedLeague, selectedClub, clubLogoUrl, null);
            } else {
                JOptionPane.showMessageDialog(this, "Выберите клуб!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.GRAY);
        bottomPanel.add(confirmButton);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateLeagueAndClub();
        setVisible(true);
    }

    private void updateLeagueAndClub() {
        String selectedLeague = (String) leagueDropdown.getSelectedItem();
        updateClubDropdown(selectedLeague);

        String logoURL = leagueLogos.get(selectedLeague);
        if (logoURL != null) {
            leagueLogoLabel.setIcon(loadImageFromURL(logoURL));
        }

        // Выбираем первый клуб по умолчанию
        if (clubDropdown.getItemCount() > 1) {
            clubDropdown.setSelectedIndex(1);
            updateClubLogo();
        }
    }

    private void updateClubDropdown(String league) {
        clubDropdown.removeAllItems();
        clubDropdown.addItem("Выберите клуб");

        if (leagues.containsKey(league)) {
            for (String club : leagues.get(league)) {
                clubDropdown.addItem(club);
            }
        }
    }

    private void updateClubLogo() {
        String selectedClub = (String) clubDropdown.getSelectedItem();
        if (selectedClub != null && !selectedClub.equals("Выберите клуб")) {
            // Проверяем, есть ли локальный логотип
            if (clubLogos.containsKey(selectedClub)) {
                clubLogoLabel.setIcon(loadImageFromURL(clubLogos.get(selectedClub)));
            } else {
                String logoURL = fetchClubLogoURL(selectedClub);
                if (logoURL != null && !logoURL.isEmpty()) {
                    clubLogoLabel.setIcon(loadImageFromURL(logoURL));
                } else {
                    System.err.println("Ошибка: логотип не найден для клуба " + selectedClub);
                    clubLogoLabel.setIcon(null);
                }
            }
        } else {
            clubLogoLabel.setIcon(null);
        }
    }

    private String fetchClubLogoURL(String clubName) {
        if (clubName == null || clubName.trim().isEmpty()) {
            System.err.println("Ошибка: название клуба не задано.");
            return null;
        }

        String apiUrl = "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=" + clubName.replace(" ", "%20");
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Ошибка: API вернуло код " + responseCode);
                return null;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(jsonResponse.toString());
            JSONArray teams = jsonObject.optJSONArray("teams");

            if (teams == null || teams.length() == 0) {
                System.err.println("Ошибка: команда '" + clubName + "' не найдена в API.");
                return null;
            }

            String logoURL = teams.getJSONObject(0).optString("strBadge", null);
            if (logoURL == null || logoURL.isEmpty()) {
                System.err.println("Ошибка: логотип для команды '" + clubName + "' не найден.");
            }

            return logoURL;
        } catch (Exception e) {
            System.err.println("Ошибка при запросе к API: " + e.getMessage());
            return null;
        }
    }


    private ImageIcon loadImageFromURL(String path) {
        if (path == null || path.isEmpty()) {
            System.err.println("Ошибка: path равно null или пусто.");
            return new ImageIcon(); // Возвращаем пустую иконку
        }

        try {
            if (path.startsWith("http")) {
                URL url = new URL(path);
                Image img = ImageIO.read(url);
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    Image img = ImageIO.read(file);
                    Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                } else {
                    System.err.println("Файл не найден: " + path);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения: " + e.getMessage());
        }
        return new ImageIcon();
    }

    private void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        dropdown.setBackground(Color.WHITE);
        dropdown.setForeground(Color.BLACK);
        dropdown.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 120, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 100, 180));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 120, 200));
            }
        });
    }

    private void initializeLeagues() {
        leagues.put("Premier League (Англия)", new String[]{
                "Arsenal", "Manchester City", "Liverpool", "Chelsea", "Manchester United",
                "Tottenham Hotspur", "Newcastle United", "Aston Villa", "West Ham United",
                "Brighton & Hove Albion", "Everton", "Wolverhampton Wanderers", "Brentford",
                "Crystal Palace", "Fulham", "Nottingham Forest", "Bournemouth", "Burnley",
                "Sheffield United", "Luton Town"
        });

        leagues.put("La Liga (Испания)", new String[]{
                "Real Madrid", "Barcelona", "Atletico Madrid", "Valencia", "Sevilla",
                "Villarreal", "Real Sociedad", "Athletic Bilbao", "Real Betis", "Getafe",
                "Celta Vigo", "Osasuna", "Mallorca", "Almeria", "Cadiz",
                "Las Palmas", "Granada", "Girona", "Alaves"
        });

        leagues.put("Bundesliga (Германия)", new String[]{
                "Bayern Munich", "Borussia Dortmund", "RB Leipzig", "Bayer Leverkusen",
                "Eintracht Frankfurt", "Union Berlin", "Freiburg", "Hoffenheim",
                "Wolfsburg", "Stuttgart", "Borussia Mönchengladbach", "Werder Bremen",
                "Augsburg", "Bochum"
        });

        leagues.put("Serie A (Италия)", new String[]{
                "Juventus", "Inter Milan", "AC Milan", "Napoli", "Roma", "Lazio",
                "Atalanta", "Fiorentina", "Torino", "Bologna", "Udinese", "Sassuolo",
                "Empoli", "Hellas Verona", "Cagliari", "Genoa", "Frosinone", "Lecce",
                "Salernitana", "Monza"
        });

        leagues.put("Ligue 1 (Франция)", new String[]{
                "PSG", "Marseille", "Lyon", "Monaco", "Lille", "Nice", "Rennes", "Lens",
                "Strasbourg", "Nantes", "Toulouse", "Montpellier", "Brest", "Reims",
                "Metz", "Lorient", "Clermont Foot", "Le Havre"
        });

        leagues.put("Eredivisie (Нидерланды)", new String[]{
                "Ajax", "PSV Eindhoven", "Feyenoord", "AZ Alkmaar", "Utrecht"
        });

        leagues.put("Primeira Liga (Португалия)", new String[]{
                "Benfica", "Porto", "Sporting CP", "Braga"
        });

        leagues.put("Scottish Premiership (Шотландия)", new String[]{
                "Celtic", "Rangers", "Aberdeen", "Hearts", "Hibernian", "Dundee United"
        });

        leagues.put("MLS (США)", new String[]{
                "LA Galaxy", "Inter Miami", "New York City FC", "Atlanta United",
                "LAFC", "Portland Timbers", "Philadelphia Union"
        });

        leagues.put("J1 League (Япония)", new String[]{
                "Kawasaki Frontale", "Urawa Red Diamonds", "Kashima Antlers",
                "Vissel Kobe", "Nagoya Grampus", "FC Tokyo"
        });

        leagues.put("Super Lig (Турция)", new String[]{
                "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor"
        });

        leagues.put("Brasileirão (Бразилия)", new String[]{
                "Flamengo", "Palmeiras", "São Paulo", "Corinthians", "Grêmio", "Internacional",
                "Santos", "Fluminense", "Atlético Mineiro"
        });

        leagues.put("Argentine Primera División (Аргентина)", new String[]{
                "Boca Juniors", "River Plate", "Racing Club", "Independiente", "San Lorenzo",
                "Vélez Sarsfield", "Estudiantes", "Newell's Old Boys"
        });

        leagueLogos.put("Premier League (Англия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\epl.jpg"); // 1
        leagueLogos.put("La Liga (Испания)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\laliga.png"); // 2
        leagueLogos.put("Bundesliga (Германия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\bundes.jpg"); // 3
        leagueLogos.put("Serie A (Италия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\serieA.png");
        leagueLogos.put("Ligue 1 (Франция)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\ligueOne.png");
        leagueLogos.put("Eredivisie (Нидерланды)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\eredivise.png");
        leagueLogos.put("J1 League (Япония)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\japan.png");
        leagueLogos.put("Major League Soccer (США)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\mls.png");
        leagueLogos.put("Primeira Liga (Португалия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\portugal.png");
        leagueLogos.put("Scottish Premiership (Шотландия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\scottish.png");
        leagueLogos.put("Süper Lig (Турция)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\turkey.png");
        leagueLogos.put("Campeonato Brasileiro Série A (Бразилия)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\brazil.jpg");
        leagueLogos.put("Primera División (Аргентина)", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\League\\argentina.jpg");


        // Добавляем логотип только для PSG
        clubLogos = new HashMap<>();
        clubLogos.put("PSG", "C:\\Users\\lapsh\\OneDrive - uib.kz\\Рабочий стол\\fotballer\\src\\main\\resources\\clubs\\psg.png");
    }


    public String getSelectedLeague() {
        return (String) leagueDropdown.getSelectedItem();
    }

    public String getSelectedClub() {
        return (String) clubDropdown.getSelectedItem();
    }
}
