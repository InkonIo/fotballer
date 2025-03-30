package guis;

import constants.CommonConstants;
import db.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFormGUI extends Form {
    public RegisterFormGUI() {
        super("Регистрация");
        setSize(370, 530); // Новый размер окна
        setLocationRelativeTo(null); // Центрирование окна
        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel registerLabel = new JLabel("Регистрация");
        registerLabel.setBounds(0, 20, 370, 80);
        registerLabel.setForeground(CommonConstants.TEXT_COLOR);
        registerLabel.setFont(new Font("Dialog", Font.BOLD, 30));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(registerLabel);

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        usernameLabel.setBounds(35, 100, 300, 20);
        usernameLabel.setForeground(CommonConstants.TEXT_COLOR);
        usernameLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        JTextField usernameField = new JTextField();
        usernameField.setBounds(35, 125, 300, 45);
        usernameField.setBackground(CommonConstants.SECONDARY_COLOR);
        usernameField.setForeground(CommonConstants.TEXT_COLOR);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(usernameLabel);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setBounds(35, 185, 300, 20);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(35, 210, 300, 45);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR);
        passwordField.setForeground(CommonConstants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(passwordLabel);
        add(passwordField);

        JLabel rePasswordLabel = new JLabel("Повторите пароль:");
        rePasswordLabel.setBounds(35, 270, 300, 20);
        rePasswordLabel.setForeground(CommonConstants.TEXT_COLOR);
        rePasswordLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        JPasswordField rePasswordField = new JPasswordField();
        rePasswordField.setBounds(35, 295, 300, 45);
        rePasswordField.setBackground(CommonConstants.SECONDARY_COLOR);
        rePasswordField.setForeground(CommonConstants.TEXT_COLOR);
        rePasswordField.setFont(new Font("Dialog", Font.PLAIN, 20));
        add(rePasswordLabel);
        add(rePasswordField);

        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.setFont(new Font("Dialog", Font.BOLD, 16));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setBackground(CommonConstants.TEXT_COLOR);
        registerButton.setBounds(60, 370, 250, 45);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String rePassword = new String(rePasswordField.getPassword());
                if (validateUserInput(username, password, rePassword)) {
                    if (MyJDBC.register(username, password)) {
                        RegisterFormGUI.this.dispose();
                        LoginFormGUI loginFormGUI = new LoginFormGUI();
                        loginFormGUI.setVisible(true);
                        JOptionPane.showMessageDialog(loginFormGUI, "Аккаунт успешно зарегистрирован!");
                    } else {
                        JOptionPane.showMessageDialog(RegisterFormGUI.this, "Ошибка: Имя пользователя уже занято");
                    }
                } else {
                    JOptionPane.showMessageDialog(RegisterFormGUI.this,
                            "Ошибка: Имя пользователя должно содержать минимум 6 символов, " +
                                    "а пароли должны совпадать");
                }
            }
        });
        add(registerButton);

        JLabel loginLabel = new JLabel("Уже есть аккаунт? Войти");
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.setForeground(CommonConstants.TEXT_COLOR);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterFormGUI.this.dispose();
                new LoginFormGUI().setVisible(true);
            }
        });
        loginLabel.setBounds(60, 430, 250, 30);
        add(loginLabel);
    }

    private boolean validateUserInput(String username, String password, String rePassword) {
        if (username.length() == 0 || password.length() == 0 || rePassword.length() == 0) return false;
        if (username.length() < 6) return false;
        return password.equals(rePassword);
    }
}