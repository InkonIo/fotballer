package guis;

import constants.CommonConstants;
import db.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFormGUI extends Form {
    public LoginFormGUI() {
        super("Вход");
        addGuiComponents();
    }

    private void addGuiComponents() {
        setLayout(null);

        JLabel loginLabel = new JLabel("Вход");
        loginLabel.setBounds(0, 20, 370, 80);
        loginLabel.setForeground(CommonConstants.TEXT_COLOR);
        loginLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loginLabel);

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        usernameLabel.setBounds(20, 110, 300, 20);
        usernameLabel.setForeground(CommonConstants.TEXT_COLOR);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(20, 140, 330, 45);
        usernameField.setBackground(CommonConstants.SECONDARY_COLOR);
        usernameField.setForeground(CommonConstants.TEXT_COLOR);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(usernameField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setBounds(20, 220, 300, 20);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(20, 250, 330, 45);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR);
        passwordField.setForeground(CommonConstants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(passwordField);

        JButton loginButton = new JButton("Войти");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBackground(CommonConstants.TEXT_COLOR);
        loginButton.setBounds(90, 350, 190, 45);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (MyJDBC.validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(LoginFormGUI.this, "Вход выполнен успешно!");
                    LoginFormGUI.this.dispose();
                    new PersonalAccount().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFormGUI.this, "Ошибка входа...");
                }
            }
        });
        add(loginButton);

        JLabel registerLabel = new JLabel("Нет аккаунта? Зарегистрируйтесь здесь");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setForeground(CommonConstants.TEXT_COLOR);
        registerLabel.setBounds(90, 410, 190, 25);
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginFormGUI.this.dispose();
                new RegisterFormGUI().setVisible(true);
            }
        });
        add(registerLabel);
    }
}
