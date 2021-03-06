package GUISwing;

import controllers.UserController;
import interfaces.MeltParentWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * @author Taite Cullen
 */
public class SettingsMenu extends JMenu implements ActionListener {
    JMenuItem profile;
    JMenuItem freeTime;
    JCheckBoxMenuItem cram;
    JCheckBoxMenuItem procrastinate;
    JMenu spacing;
    UserController userController;
    JCheckBoxMenuItem morningPerson;

    MeltParentWindow parent;

    /**
     * Constructor for SettingsMenu
     * @param userController controller for User entity
     * @param parent parent window
     */
    public SettingsMenu(UserController userController, MeltParentWindow parent){
        this.userController = userController;
        this.parent = parent;
        this.setText("Settings");
        this.setVisible(true);


        profile = new JMenuItem("profile settings");
        profile.addActionListener(this);
        this.add(profile);

        freeTime = new JMenuItem("free time");
        freeTime.addActionListener(this);
        this.add(freeTime);

        this.addSeparator();
        morningPerson = new JCheckBoxMenuItem("morning person");
        morningPerson.setSelected(userController.getUserManager().getPreferences(userController.getCurrentUser()).getMorningPerson());
        this.add(morningPerson);
        morningPerson.setActionCommand("morning");
        morningPerson.addActionListener(this);

        this.addSeparator();
        cram = new JCheckBoxMenuItem("cram");
        cram.setSelected(userController.getUserManager().getPreferences(userController.getCurrentUser()).getCram());
        cram.addActionListener(this);
        procrastinate = new JCheckBoxMenuItem("procrastinate");
        procrastinate.setSelected(userController.getCurrentProcrastinate());
        procrastinate.addActionListener(this);
        this.add(cram);
        this.add(procrastinate);

        spacing = new JMenu("spacing");
        ButtonGroup spacings = new ButtonGroup();
        getCramButton(userController.getUserManager().getPreferences(userController.getCurrentUser()).getSpacingSameDay(), spacings);
        for (Iterator<AbstractButton> it = spacings.getElements().asIterator(); it.hasNext(); ) {
            AbstractButton button = it.next();

            spacing.add(button);
            button.setActionCommand(button.getText());
            button.addActionListener(this);
        }
        this.add(spacing);
    }


    /**
     * Method which gets the cram button
     * @param spacing A string which specifies the spacing
     * @param spacings A ButtonGroup
     */
    private void getCramButton(String spacing, ButtonGroup spacings){
        JRadioButtonMenuItem none = new JRadioButtonMenuItem("none");
        JRadioButtonMenuItem small = new JRadioButtonMenuItem("small");
        JRadioButtonMenuItem medium = new JRadioButtonMenuItem("medium");
        JRadioButtonMenuItem large = new JRadioButtonMenuItem("large");
        spacings.add(none);
//        spacings.add(small);
        spacings.add(medium);
//        spacings.add(large);
        switch (spacing) {
            case "none":
                spacings.setSelected(none.getModel(), true);
                break;
            case "small":
                spacings.setSelected(small.getModel(), true);
                break;
            case "medium":
                spacings.setSelected(medium.getModel(), true);
                break;
            case "large":
                spacings.setSelected(large.getModel(), true);
                break;
        }
    }

    /**
     * freeTime opens FreeTimeWindow
     * profile opens ProfileWindow
     * cram changes truth value of cram in UserPreferences for current user
     * procrastinate changes truth value of Procrastinate in UserPreferences for current user
     * morningPerson changes truth value of Procrastinate in UserPreferences for current user
     * the session spacing radio buttons set the String spacing in UserPreferences to be equal to their text
     * @param e Action
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == freeTime){
            new FreeTimeWindow(userController, parent);
        } else if (e.getSource() == profile){
            ProfileSettings pf = new ProfileSettings(userController, parent);
            pf.display();
        }
         else if (e.getSource() == cram){
            userController.getUserManager().toggleEvenSpacing(userController.getCurrentUser());
        } else if (e.getSource() == procrastinate){
            userController.setProcrastinate(procrastinate.isSelected());
        } else if(e.getSource() == morningPerson) {
            userController.setMorningPerson(morningPerson.isSelected());
        } else{
            userController.setSessionSpacing(e.getActionCommand());
        }
         parent.refresh();
    }
}

