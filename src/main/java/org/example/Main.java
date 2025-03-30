package org.example;

import guis.LoginFormGUI;
import javax.swing.*;

import static db.MyJDBC.testConnection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new LoginFormGUI().setVisible(true);
            }
        });

        if (testConnection()) {
            System.out.println("База данных работает.");
        } else {
            System.out.println("Ошибка подключения. Проверь настройки.");
        }
    }
}