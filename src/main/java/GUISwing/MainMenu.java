package GUISwing;

import controllers.CalendarController;
import controllers.EventController;
import controllers.MainController;
import controllers.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu implements ActionListener {
    private final JFrame frame;
    private final MainController mc;
    private final JButton buttonProfile = new JButton("1. Profile Setting");
    private final JButton buttonCalendar = new JButton("2. View/Export Calendar");
    private final JButton buttonAddEvent = new JButton("3. Add a new Event");
    private final JButton buttonModifyEvent = new JButton("4. Modify an Event");
    private final JButton buttonExport = new JButton("5. Export Entire Calendar to iCal File");
    private final JButton buttonLogOut = new JButton("6. Log Out");
    private final JButton buttonExit = new JButton("7. Exit");

    public MainMenu(MainController mainController) {
        this.frame = new MainFrame();
        this.mc = mainController;
        EventController eventController= mc.getEventController();
        CalendarController calendarController = mc.getCalendarController();
        UserController userController =  mc.getUserController();
        JLabel welcomeMessage = new JLabel();
        setUpWelcomeMessage(userController.getCurrentUsername(), welcomeMessage);
        frame.add(welcomeMessage);
        JPanel calendarPanel = setUpCalendarPanel();
        setUpDefaultCalendar(eventController, calendarController, calendarPanel);
        JScrollPane calendarScrollPane = new JScrollPane(calendarPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        calendarScrollPane.setBounds(50, 150, 1344, 500);
        frame.add(calendarScrollPane);
        JPanel menuPanel = new JPanel();
        menuPanel.setBounds(0, 700, 1444, 1000/2);
        menuPanel.setBackground(new Color(233, 161, 161));
        frame.add(menuPanel);
        buttonSetUp(menuPanel);
    }

    private void buttonSetUp(JPanel menuPanel) {
        setUpIndividualButton(buttonProfile, menuPanel);
        setUpIndividualButton(buttonCalendar, menuPanel);
        setUpIndividualButton(buttonAddEvent, menuPanel);
        setUpIndividualButton(buttonModifyEvent, menuPanel);
        setUpIndividualButton(buttonExport, menuPanel);
        setUpIndividualButton(buttonLogOut, menuPanel);
        setUpIndividualButton(buttonExit, menuPanel);
    }

    private void setUpIndividualButton(JButton button, JPanel menuPanel) {
        button.setPreferredSize(new Dimension(300, 50));
        button.addActionListener(this);
        menuPanel.add(button);
    }

    private JPanel setUpCalendarPanel() {
        JPanel calendarPanel = new JPanel();
        calendarPanel.setBackground(new Color(233, 161, 161));
        return calendarPanel;
    }

    private void setUpDefaultCalendar(EventController eventController, CalendarController calendarController, JPanel calendarPanel) {
        JLabel defaultCalendar = new JLabel();
        String defaultCalendarString = calendarController.showDefaultCalendar(eventController);
        defaultCalendarString = defaultCalendarString.replaceAll("\n", "<br/>");
        defaultCalendar.setText("<html><pre>" + defaultCalendarString + "</pre><html>");
        defaultCalendar.setHorizontalTextPosition(JLabel.CENTER);
        defaultCalendar.setVerticalTextPosition(JLabel.TOP);
        defaultCalendar.setVerticalAlignment(JLabel.TOP);
        defaultCalendar.setHorizontalAlignment(JLabel.CENTER);
        calendarPanel.add(defaultCalendar);
    }

    private void setUpWelcomeMessage(String name, JLabel welcomeMessage) {
        welcomeMessage.setText("Welcome " + name + "!");
        welcomeMessage.setHorizontalTextPosition(JLabel.CENTER);
        welcomeMessage.setVerticalTextPosition(JLabel.TOP);
        welcomeMessage.setFont(new Font("MV Boli", Font.ITALIC, 25));
        welcomeMessage.setVerticalAlignment(JLabel.CENTER);
        welcomeMessage.setHorizontalAlignment(JLabel.CENTER);
        welcomeMessage.setBounds(1444/3, 0, 1444/3, 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonProfile) {
            //lead to profile setting page
            mc.getLoginController().logout();
            frame.dispose();
            new LogInWindow();
        }
        else if (e.getSource() == buttonCalendar) {
            //lead to calendar page
            mc.getLoginController().logout();
            frame.dispose();
            new LogInWindow();
        }
        else if (e.getSource() == buttonAddEvent) {
            //lead to add event page
            mc.getLoginController().logout();
            frame.dispose();
            new LogInWindow();
        }
        else if (e.getSource() == buttonModifyEvent) {
            //lead to modify event page
            mc.getLoginController().logout();
            frame.dispose();
            new LogInWindow();
        }
        else if (e.getSource() == buttonExport) {
            SaveICalendar saveCalendar = new SaveICalendar();
            saveCalendar.save(mc.getEventController());
        }
        else if (e.getSource() == buttonLogOut) {
            mc.getLoginController().logout();
            frame.dispose();
            new LogInWindow();
        }
        else if (e.getSource() == buttonExit) {
            mc.saveAndExitProgram();
            frame.dispose();
        }
    }

    public void display() {
        frame.setVisible(true);
    }
}
