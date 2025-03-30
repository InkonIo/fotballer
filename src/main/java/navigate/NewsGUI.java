package navigate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NewsGUI extends JPanel {
    public NewsGUI(JFrame parent) {
        setLayout(new BorderLayout());
        setBackground(new Color(24, 24, 24));

        JLabel titleLabel = new JLabel("⚽ Новости футбола", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel newsPanel = new JPanel();
        newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
        newsPanel.setBackground(new Color(24, 24, 24));
        JScrollPane scrollPane = new JScrollPane(newsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(15); // Ускоряем скролл
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("⬅ Назад");
        styleButton(backButton);
        backButton.addActionListener(e -> this.setVisible(false));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(24, 24, 24));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        displayNews(newsPanel);
    }

    private void displayNews(JPanel newsPanel) {
        List<String[]> newsList = FootballAPI.getNews();
        if (newsList == null || newsList.isEmpty()) {
            JLabel noNewsLabel = new JLabel("Новостей пока нет.", SwingConstants.CENTER);
            noNewsLabel.setForeground(Color.WHITE);
            newsPanel.add(noNewsLabel);
        } else {
            for (String[] news : newsList) {
                JPanel newsCard = new JPanel(new BorderLayout());
                newsCard.setBackground(new Color(34, 34, 34));
                newsCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                JLabel titleLabel = new JLabel("<html><b>" + news[0] + "</b></html>");
                titleLabel.setForeground(Color.WHITE);

                JLabel dateLabel = new JLabel(news[3]);
                dateLabel.setForeground(Color.LIGHT_GRAY);

                JTextArea textArea = new JTextArea(news[2]);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                textArea.setBackground(new Color(34, 34, 34));
                textArea.setForeground(Color.WHITE);

                JButton readMoreButton = new JButton("📖 Читать");
                styleButton(readMoreButton);
                readMoreButton.addActionListener(e -> openNews(news[1]));

                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.setBackground(new Color(34, 34, 34));
                textPanel.add(titleLabel, BorderLayout.NORTH);
                textPanel.add(dateLabel, BorderLayout.WEST);
                textPanel.add(textArea, BorderLayout.CENTER);
                textPanel.add(readMoreButton, BorderLayout.EAST);

                newsCard.add(textPanel, BorderLayout.CENTER);
                newsPanel.add(newsCard);
            }
        }
        newsPanel.revalidate();
        newsPanel.repaint();
    }

    private void openNews(String url) {
        if (url == null || url.isEmpty()) {
            System.err.println("Ошибка: Пустая ссылка на новость");
            return;
        }
        try {
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            System.err.println("Ошибка при открытии ссылки: " + e.getMessage());
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
}
