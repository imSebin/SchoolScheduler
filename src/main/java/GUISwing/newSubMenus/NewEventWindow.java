package GUISwing.newSubMenus;

import GUISwing.MainFrame;
import GUISwing.MenuCreationHelper;
import com.dropbox.core.v2.teamlog.DeviceManagementEnabledDetails;
import controllers.EventController;
import controllers.UserController;
import entities.User;
import gateways.IOSerializable;
import usecases.events.EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class NewEventWindow implements ActionListener {
    EventController eventController;
    JFrame frame = new MainFrame();
    JPanel datesPanel = new JPanel(new GridLayout(2, 4));
    JPanel infoPanel = new JPanel(new GridLayout(2, 1));
    JComboBox<YearMonth> startMonth = MenuCreationHelper.monthComboBox();
    JComboBox<YearMonth> endMonth = MenuCreationHelper.monthComboBox();
    JComboBox<Integer> startDate = MenuCreationHelper.dateJComboBox(startMonth.getItemAt(0));
    JComboBox<Integer> endDate = MenuCreationHelper.dateJComboBox(endMonth.getItemAt(0));
    JComboBox<LocalTime> startTime = MenuCreationHelper.timeComboBox();
    JComboBox<LocalTime> endTime = MenuCreationHelper.timeComboBox();

    JButton save = new JButton("save");
    JButton delete = new JButton("delete");

    JTextField name = new JTextField("event name");
    JTextArea description = new JTextArea("description");


    public NewEventWindow(EventController eventController){
        this.eventController = eventController;
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(new Dimension( 500, 300));
        setNameField();
        setDateTimePickers();
        saveDeleteButtons();

        frame.setVisible(true);
    }

    public void setDateTimePickers(){
        frame.getContentPane().add(datesPanel);
        datesPanel.setBounds(5, 5, 100, 100);
        datesPanel.setBackground(Color.ORANGE);

        JLabel lblStart = new JLabel("start: ");
        setDateOptions(lblStart, startMonth, startDate, startTime);

        JLabel lblEnd = new JLabel("End: ");
        setDateOptions(lblEnd, endMonth, endDate, endTime);

        startMonth.addActionListener(this);
        endMonth.addActionListener(this);
    }

    private void setNameField(){
        frame.getContentPane().add(infoPanel);
        infoPanel.add(name);
        infoPanel.add(description);
    }

    private void setDateOptions(JLabel lblEnd, JComboBox<YearMonth> endMonth, JComboBox<Integer> endDate, JComboBox<LocalTime> endTime) {
        datesPanel.add(lblEnd);
        datesPanel.add(endMonth);
        datesPanel.add(endDate);
        datesPanel.add(endTime);
    }

    private void saveDeleteButtons(){
        frame.getContentPane().add(save);
        frame.getContentPane().add(delete);
        save.addActionListener(this);
        delete.addActionListener(this);
    }

    private void changeDays(ActionEvent e){
        if (e.getSource() == startMonth | e.getSource() == endMonth){
            startDate = MenuCreationHelper.dateJComboBox((YearMonth) Objects.requireNonNull(startMonth.getSelectedItem()));
            endDate = MenuCreationHelper.dateJComboBox((YearMonth) Objects.requireNonNull(endMonth.getSelectedItem()));
        }
    }
    private void save(ActionEvent e){
        if(e.getSource() == save){
            UUID event = eventController.getEventManager().addEvent(name.getText(), LocalDateTime.of(LocalDate.of((
                    (YearMonth) (endMonth.getSelectedItem())).getYear(), ((YearMonth) (endMonth.getSelectedItem())).
                    getMonthValue(), (Integer)(endDate.getSelectedItem())), (LocalTime) endTime.getSelectedItem()));
            eventController.getEventManager().setDescription(event, description.getText());
            eventController.getEventManager().setStart(event, LocalDateTime.of(LocalDate.of((
                    (YearMonth) (startMonth.getSelectedItem())).getYear(), ((YearMonth) (startMonth.getSelectedItem())).
                    getMonthValue(), (Integer)(startDate.getSelectedItem())), (LocalTime) startTime.getSelectedItem()));
            new EditEventWindow(eventController, event);
            frame.dispose();
            System.out.println(eventController.getEventManager().getAllEvents());
        }
    }

    private void delete(ActionEvent e){
        if (e.getSource() == delete){
            frame.dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        changeDays(e);
        save(e);
        delete(e);
    }
}
