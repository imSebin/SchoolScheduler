package GUISwing;

import helpers.Constants;

import javax.swing.*;
import java.awt.*;

public class PopUpWindowFrame extends JFrame {
    PopUpWindowFrame() {
        this.setTitle("Haunted Pyramid Calendar App");
        this.setResizable(false);
        ImageIcon hauntedPyramid = new ImageIcon("res/Haunted_Pyramid_Icon.png");
        this.setIconImage(hauntedPyramid.getImage());
        this.getContentPane().setBackground(new Color(233, 161, 161));
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(Constants.POPUP_WIDTH, Constants.POPUP_HEIGHT);
        this.setVisible(false);
    }
}