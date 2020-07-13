import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

public class GUI extends JPanel implements ActionListener, ListSelectionListener {

    private Window window;
    private Handler handler;

    private JLabel leftHandLabelOne = new JLabel("Left Hand");
    private JLabel rightHandLabelOne = new JLabel("Right Hand");
    private JLabel leftHandLabelTwo = new JLabel("Left Hand");
    private JLabel rightHandLabelTwo = new JLabel("Right Hand");
    private JLabel instructions = new JLabel("Instructions");

    private CardLayout cardLayout = new CardLayout(0, 0);
    private JPanel actionArea = new JPanel();
    private JPanel imagePanel = new JPanel();
    private JPanel configPanel = new JPanel();
    private JPanel emptyPanel = new JPanel();

    private JLabel imageLabel = new JLabel();

    private ArrayList<JButton> configButtons = new ArrayList<>();
    private JButton southButton = new JButton();
    private JButton southwestButton = new JButton();
    private JButton westButton = new JButton();
    private JButton northwestButton = new JButton();
    private JButton northButton = new JButton();
    private JButton northeastButton = new JButton();
    private JButton eastButton = new JButton();
    private JButton southeastButton = new JButton();

    private JPanel indicatorPanel = new JPanel();
    private JScrollPane eventLog = new JScrollPane();
    private DefaultListModel<String> eventLogListModel = new DefaultListModel<>();
    private JList<String> eventLogJList = new JList<>(eventLogListModel);

    private JButton addSensor = new JButton("Add Sensor");
    private JButton beginTestingButton = new JButton("Begin Testing");
    private JButton addGestureButton;
    private JButton removeGestureButton = new JButton("Remove");

    private JScrollPane gestureList = new JScrollPane();
    private DefaultListModel<String> dlm = new DefaultListModel<>();
    private JList<String> jList = new JList<>(dlm);
    private String selectedGesture;

    public GUI(Window window) {

        this.window = window;

        setLayout(null);


        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        actionArea.setBounds(210, 11, 564, 469);
        actionArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        actionArea.setLayout(cardLayout);
        actionArea.add(emptyPanel, "ep");
        actionArea.add(imagePanel, "ip");
        add(actionArea);

        jList.addListSelectionListener(this);
        gestureList.setViewportView(jList);
        gestureList.setBounds(8, 11, 195, 234);
        TitledBorder gestureListBorder = BorderFactory.createTitledBorder("List of Gestures");
        gestureListBorder.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        gestureList.setBorder(gestureListBorder);
        add(gestureList);

        eventLog.setViewportView(eventLogJList);
        eventLog.setBounds(8, 290, 195, 192);
        TitledBorder eventLogBorder = BorderFactory.createTitledBorder("Event Log");
        eventLogBorder.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        eventLog.setBorder(eventLogBorder);
        add(eventLog);

        beginTestingButton.setBounds(210, 528, 280, 32);
        beginTestingButton.addActionListener(this);
        add(beginTestingButton);

        addSensor.addActionListener(this);
        addSensor.setBounds(494, 528, 280, 32);
        add(addSensor);

        removeGestureButton.setBounds(105, 252, 93, 23);
        add(removeGestureButton);

        addGestureButton = new JButton("Add");
        addGestureButton.setBounds(10, 252, 93, 23);
        addGestureButton.addActionListener(this);
        add(addGestureButton);

        indicatorPanel.setBounds(10, 488, 190, 72);
        indicatorPanel.setLayout(new GridLayout(2,2));
        indicatorPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(indicatorPanel);

        leftHandLabelOne.setHorizontalAlignment(SwingConstants.CENTER);
        leftHandLabelOne.setOpaque(true);
        indicatorPanel.add(leftHandLabelOne);

        rightHandLabelOne.setHorizontalAlignment(SwingConstants.CENTER);
        rightHandLabelOne.setOpaque(true);
        indicatorPanel.add(rightHandLabelOne);

        leftHandLabelTwo.setHorizontalAlignment(SwingConstants.CENTER);
        leftHandLabelTwo.setOpaque(true);
        indicatorPanel.add(leftHandLabelTwo);

        rightHandLabelTwo.setHorizontalAlignment(SwingConstants.CENTER);
        rightHandLabelTwo.setOpaque(true);
        indicatorPanel.add(rightHandLabelTwo);

        instructions.setHorizontalAlignment(SwingConstants.CENTER);
        instructions.setBounds(210, 488, 564, 34);
        instructions.setOpaque(true);
        instructions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        add(instructions);

        setupConfigPanel();

    }

    public void setupConfigPanel() {

        configPanel.setLayout(null);
        actionArea.add(configPanel, "cp");

        southButton.setBounds(256, 356, 50, 30);
        southButton.setToolTipText("South");
        configButtons.add(southButton);

        southwestButton.setBounds(96, 316, 50, 30);
        southwestButton.setToolTipText("Southwest");
        configButtons.add(southwestButton);

        westButton.setBounds(55, 211, 50, 30);
        westButton.setToolTipText("West");
        configButtons.add(westButton);

        northwestButton.setBounds(95, 101, 50, 30);
        northwestButton.setToolTipText("Northwest");
        configButtons.add(northwestButton);

        northButton.setBounds(256, 61, 50, 30);
        northButton.setToolTipText("North");
        configButtons.add(northButton);

        northeastButton.setBounds(415, 101, 50, 30);
        northeastButton.setToolTipText("Northeast");
        configButtons.add(northeastButton);

        eastButton.setBounds(455, 211, 50, 30);
        eastButton.setToolTipText("East");
        configButtons.add(eastButton);

        southeastButton.setBounds(415, 316, 50, 30);
        southeastButton.setToolTipText("Southeast");
        configButtons.add(southeastButton);

        for (JButton button : configButtons) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.WHITE);
            button.addActionListener(this);
            configPanel.add(button);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        selectedGesture = jList.getSelectedValue();

        if (!e.getValueIsAdjusting() && selectedGesture != null) {
        } else {
            selectedGesture = null;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton clicked = (JButton) e.getSource();

        if (clicked.getText().equalsIgnoreCase("Remove Sensor")) {
            addSensor.setText("Add Sensor");
            handler.destroyServer();
        }

        else if (clicked == addSensor) {
            addSensor.setText("Remove Sensor");
            handler.startServer();
        }

        if (clicked.getText().equalsIgnoreCase("Confirm Selection")) {
            handler.confirmSelection();
        }

        if (clicked == beginTestingButton && beginTestingButton.getText().equalsIgnoreCase("Begin Testing")) {
            handler.beginTesting();
        }

        if (clicked == addGestureButton) {
            handler.addGesture();
        }

        if (clicked == removeGestureButton) {
            handler.removeGesture();
        }

        for (JButton button : configButtons) {
            if (e.getSource() == button) {
                doButtonThing(button);
            }
        }

    }

    public void doButtonThing(JButton button) {

        int selectedCount = 0;

        for (JButton b : configButtons) {
            if (b.getBackground() == Color.GRAY) {
                selectedCount++;
            }
        }

        if (button.getBackground() == Color.GRAY) {
            button.setBackground(Color.WHITE);
            button.setText("");
        }

        else if (selectedCount < 2) {
            button.setBackground(Color.GRAY);
            selectedCount++;

            if (selectedCount == 1) {
                button.setText("1");
            }

            if (selectedCount == 2) {
                button.setText("2");
            }
        }

        boolean is1 = false;
        boolean is2 = false;
        for (JButton b : configButtons) {
            if (b.getText().equalsIgnoreCase("1")) is1 = true;
            if (b.getText().equalsIgnoreCase("2")) is2 = true;
        }
        if (is2 && !is1) {
            for (JButton b : configButtons) {
                if (b.getText().equalsIgnoreCase("2")) b.setText("1");
            }
        }

    }

    public String getPositionsOfSelected() {
        String positionOne = "";
        String positionTwo = "";

        for (JButton b : configButtons) {
            if (b.getBackground() == Color.GRAY) {
                if (b.getText().equalsIgnoreCase("1")) {
                    positionOne = b.getToolTipText();
                }
                if (b.getText().equalsIgnoreCase("2")) {
                    positionTwo = b.getToolTipText();
                }
            }
        }

        if (positionOne.equalsIgnoreCase("")) return null;

        return positionOne.concat(" ".concat(positionTwo));
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public JLabel getLeftHandLabelOne() {
        return leftHandLabelOne;
    }

    public void setLeftHandLabelOne(JLabel leftHandLabelOne) {
        this.leftHandLabelOne = leftHandLabelOne;
    }

    public JLabel getRightHandLabelOne() {
        return rightHandLabelOne;
    }

    public void setRightHandLabelOne(JLabel rightHandLabelOne) {
        this.rightHandLabelOne = rightHandLabelOne;
    }

    public JLabel getLeftHandLabelTwo() {
        return leftHandLabelTwo;
    }

    public void setLeftHandLabelTwo(JLabel leftHandLabelTwo) {
        this.leftHandLabelTwo = leftHandLabelTwo;
    }

    public JLabel getRightHandLabelTwo() {
        return rightHandLabelTwo;
    }

    public void setRightHandLabelTwo(JLabel rightHandLabelTwo) {
        this.rightHandLabelTwo = rightHandLabelTwo;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public JLabel getInstructions() {
        return instructions;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setInstructions(JLabel instructions) {
        this.instructions = instructions;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public JPanel getActionArea() {
        return actionArea;
    }

    public void setActionArea(JPanel actionArea) {
        this.actionArea = actionArea;
    }

    public JPanel getConfigPanel() {
        return configPanel;
    }

    public void setConfigPanel(JPanel configPanel) {
        this.configPanel = configPanel;
    }

    public ArrayList<JButton> getConfigButtons() {
        return configButtons;
    }

    public void setConfigButtons(ArrayList<JButton> configButtons) {
        this.configButtons = configButtons;
    }

    public JButton getSouthButton() {
        return southButton;
    }

    public void setSouthButton(JButton southButton) {
        this.southButton = southButton;
    }

    public JButton getSouthwestButton() {
        return southwestButton;
    }

    public void setSouthwestButton(JButton southwestButton) {
        this.southwestButton = southwestButton;
    }

    public JButton getWestButton() {
        return westButton;
    }

    public void setWestButton(JButton westButton) {
        this.westButton = westButton;
    }

    public JButton getNorthwestButton() {
        return northwestButton;
    }

    public void setNorthwestButton(JButton northwestButton) {
        this.northwestButton = northwestButton;
    }

    public JButton getNorthButton() {
        return northButton;
    }

    public void setNorthButton(JButton northButton) {
        this.northButton = northButton;
    }

    public JButton getNortheastButton() {
        return northeastButton;
    }

    public void setNortheastButton(JButton northeastButton) {
        this.northeastButton = northeastButton;
    }

    public JButton getEastButton() {
        return eastButton;
    }

    public void setEastButton(JButton eastButton) {
        this.eastButton = eastButton;
    }

    public JButton getSoutheastButton() {
        return southeastButton;
    }

    public void setSoutheastButton(JButton southeastButton) {
        this.southeastButton = southeastButton;
    }

    public JPanel getIndicatorPanel() {
        return indicatorPanel;
    }

    public void setIndicatorPanel(JPanel indicatorPanel) {
        this.indicatorPanel = indicatorPanel;
    }

    public JButton getAddSensor() {
        return addSensor;
    }

    public void setAddSensor(JButton addSensor) {
        this.addSensor = addSensor;
    }

    public JButton getBeginTestingButton() {
        return beginTestingButton;
    }

    public void setBeginTestingButton(JButton beginTestingButton) {
        this.beginTestingButton = beginTestingButton;
    }

    public JButton getAddGestureButton() {
        return addGestureButton;
    }

    public void setAddGestureButton(JButton addGestureButton) {
        this.addGestureButton = addGestureButton;
    }

    public JPanel getEmptyPanel() {
        return emptyPanel;
    }

    public void setEmptyPanel(JPanel emptyPanel) {
        this.emptyPanel = emptyPanel;
    }

    public JScrollPane getGestureList() {
        return gestureList;
    }

    public void setGestureList(JScrollPane gestureList) {
        this.gestureList = gestureList;
    }

    public DefaultListModel<String> getDlm() {
        return dlm;
    }

    public void setDlm(DefaultListModel<String> dlm) {
        this.dlm = dlm;
    }

    public JList<String> getjList() {
        return jList;
    }

    public void setjList(JList<String> jList) {
        this.jList = jList;
    }

    public String getSelectedGesture() {
        return selectedGesture;
    }

    public void setSelectedGesture(String selectedGesture) {
        this.selectedGesture = selectedGesture;
    }

    public JScrollPane getEventLog() {
        return eventLog;
    }

    public void setEventLog(JScrollPane eventLog) {
        this.eventLog = eventLog;
    }

    public DefaultListModel<String> getEventLogListModel() {
        return eventLogListModel;
    }

    public void setEventLogListModel(DefaultListModel<String> eventLogListModel) {
        this.eventLogListModel = eventLogListModel;
    }

    public JList<String> getEventLogJList() {
        return eventLogJList;
    }

    public void setEventLogJList(JList<String> eventLogJList) {
        this.eventLogJList = eventLogJList;
    }

    public JButton getRemoveGestureButton() {
        return removeGestureButton;
    }

    public void setRemoveGestureButton(JButton removeGestureButton) {
        this.removeGestureButton = removeGestureButton;
    }

    public JPanel getImagePanel() {
        return imagePanel;
    }

    public void setImagePanel(JPanel imagePanel) {
        this.imagePanel = imagePanel;
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(JLabel imageLabel) {
        this.imageLabel = imageLabel;
    }

}
