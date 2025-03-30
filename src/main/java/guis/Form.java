package guis;

import constants.CommonConstants;

import javax.swing.*;

public class Form extends JFrame {
    public Form(String title){
        super(title);
        setSize(374, 530);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.PRIMARY_COLOR);
    }
}