package com.publictalkgenerator.view;

import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Talk;
import com.publictalkgenerator.domain.Elder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class GeneratorUI extends JFrame {
    private JFrame frame = this;
    private JTextField firstNameTextField;
    private JTextField middleNameTextField;
    private JTextField lastNameTextField;
    private JTextField phoneNumberTextField;
    private JComboBox<String> congregationComboBox;
    private JTabbedPane tabbedPane;
    private JButton addElderButton;
    private JTextField congregationNameField;
    private JButton addCongregationButton;
    private JTextField talkTitleTextField;
    private JSpinner talkNumberSpinner1;
    private JComboBox<Integer> talkNumberComboBox;
    private JButton addTalkButton;
    private JLabel congregationNameLabel;
    private JLabel talkNameLabel;
    private JLabel talkNumberLabel;
    private JTable congregationTable;
    private DefaultTableModel congregationTableModel;
    private DefaultTableModel talkTableModel;
    private DefaultTableModel elderTableModel;
    private JTable talkTable;
    private JScrollPane congTabelScrollPane;
    private JScrollPane talkTableScrollPane;
    private JButton removeCongregationButton;
    private JButton removeTalkButton;
    private JTable elderTable;
    private JScrollPane elderTableScrollPane;
    private JButton removeElderButton;

    private GeneratorUI() {
        setTitle("የንግግር ፕሮግራም አመንጪ");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        List<Congregation> congList = null;
        List<Talk> talkList = null;
        List<Elder> elderList = null;

//        congregationComboBox talkNumberComboBox
        try {
            congList = Congregation.getCongregationDao().queryForAll();
            talkList = Talk.getTalkDao().queryForAll();
            elderList = Elder.getElderDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // setting up the TableModel(s) for congregation and talk
        congregationTableModel = new DefaultTableModel();
        congregationTableModel.addColumn("#");
        congregationTableModel.addColumn("ስም");

        talkTableModel = new DefaultTableModel();
        talkTableModel.addColumn("#");
        talkTableModel.addColumn("ርዕስ");
        talkTableModel.addColumn("ቁጥር");

        elderTableModel = new DefaultTableModel();
        elderTableModel.addColumn("ስም");
        elderTableModel.addColumn("የአባት ስም");
        elderTableModel.addColumn("የአያት ስም");
        elderTableModel.addColumn("የስልክ ቁጥር");
        elderTableModel.addColumn("የንግግር ቁጥር");
        elderTableModel.addColumn("ጉባኤ");

        // fill the tables on the congregation/talk tab
        Object[] congTalkElderTableRow = new Object[2];
        for (Congregation cong : congList) {
            congTalkElderTableRow[0] = cong.getId();
            congTalkElderTableRow[1] = cong.getName();
            congregationTableModel.addRow(congTalkElderTableRow);
        }
        congTalkElderTableRow = new Object[3];
        for (Talk talk : talkList) {
            congTalkElderTableRow[0] = talk.getId();
            congTalkElderTableRow[1] = talk.getTalkNumber();
            congTalkElderTableRow[2] = talk.getTitle();
            talkTableModel.addRow(congTalkElderTableRow);
        }
        congTalkElderTableRow = new Object[6];
        for (Elder elder : elderList) {
            congTalkElderTableRow[0] = elder.getFirstName();
            congTalkElderTableRow[1] = elder.getMiddleName();
            congTalkElderTableRow[2] = elder.getLastName();
            congTalkElderTableRow[3] = elder.getPhoneNumber();
            congTalkElderTableRow[4] = elder.getTalk().getTalkNumber();
            congTalkElderTableRow[5] = elder.getCongregation().getName();
            elderTableModel.addRow(congTalkElderTableRow);
        }
        // fill congregation and talk comboBoxes in the ተናጋሪ tab
        for (Congregation congregation : congList) {
            congregationComboBox.addItem(congregation.getName());
        }

        for (Talk talk : talkList) {
            talkNumberComboBox.addItem(talk.getTalkNumber());
        }

        congregationTable.setModel(congregationTableModel);
        talkTable.setModel(talkTableModel);
        elderTable.setModel(elderTableModel);

        congTabelScrollPane.setPreferredSize(new Dimension(400, 100));
        talkTableScrollPane.setPreferredSize(new Dimension(400, 100));
        elderTableScrollPane.setPreferredSize(new Dimension(400, 100));
        // congregation_talk tab button events
        addCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (congregationNameField.getText().equals(""))
                    return;
                Congregation congregation = new Congregation(congregationNameField.getText());
                try {
                    Congregation.getCongregationDao()
                            .createIfNotExists(congregation);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                Object[] congregationDetails = new Object[2];
                congregationDetails[0] = congregation.getId();
                congregationDetails[1] = congregation.getName();
                congregationTableModel.addRow(congregationDetails);
                clearFields(Field.CONGREGATION);
            }
        });

        addTalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (talkTitleTextField.getText().equals("") || talkNumberSpinner1.getValue() == null)
                    return;
                Talk talk = new Talk((int) talkNumberSpinner1.getValue(), talkTitleTextField.getText());
                try {
                    Talk.getTalkDao().createIfNotExists(talk);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                Object[] talkDetails = new Object[3];
                talkDetails[0] = talk.getId();
                talkDetails[2] = talk.getTitle();
                talkDetails[1] = talk.getTalkNumber();
                clearFields(Field.TALK);
            }
        });

        // Talker tab button events
        addElderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (
                        firstNameTextField.getText().equals("")     ||
                        middleNameTextField.getText().equals("")    ||
                        lastNameTextField.getText().equals("")      ||
                        firstNameTextField.getText().equals("")
                    )
                    return;

                Elder elder = new Elder();

                elder.setFirstName(firstNameTextField.getText());
                elder.setMiddleName(lastNameTextField.getText());
                elder.setLastName(lastNameTextField.getText());
                elder.setPhoneNumber(phoneNumberTextField.getText());

                Congregation congregation = null;
                try {
                    congregation = Congregation
                            .getCongregationDao()
                            .queryBuilder()
                            .where()
                            .eq("name", congregationComboBox.getSelectedItem())
                            .query()
                            .get(0);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elder.setCongregation(congregation);

                Talk talk = null;
                try {
                    talk = Talk.getTalkDao()
                            .queryBuilder()
                            .where()
                            .eq("talkNumber", talkNumberComboBox.getSelectedItem())
                            .query()
                            .get(0);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elder.setTalk(talk);

                try {
                    Elder.getElderDao().createIfNotExists(elder);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                Object[] elderDetails = new Object[6];
                elderDetails[0] = elder.getFirstName();
                elderDetails[1] = elder.getMiddleName();
                elderDetails[2] = elder.getLastName();
                elderDetails[3] = elder.getPhoneNumber();
                elderDetails[4] = elder.getTalk().getTalkNumber();
                elderDetails[5] = elder.getCongregation().getName();
                elderTableModel.addRow(elderDetails);
                clearFields(Field.ELDER);
            }
        });

        removeCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = congregationTable.getSelectedRow();
                try {
                    Congregation.getCongregationDao().deleteById
                            ((int) congregationTable.getValueAt(selectedRow, 0) + "");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                congregationTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(frame, "ንግግሩ ወጥቷል", null, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        removeTalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = talkTable.getSelectedRow();
                try {
                    Talk.getTalkDao().deleteById
                            ((int) talkTable.getValueAt(selectedRow, 0) + "");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                talkTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(frame, "ጉባኤው ወጥቷል", null, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        removeElderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String elderPhoneNumber = elderTable.getValueAt(elderTable.getSelectedRow(), 3).toString();
                try {
                    Elder.getElderDao().deleteById(elderPhoneNumber);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elderTableModel.removeRow(elderTable.getSelectedRow());
                JOptionPane.showMessageDialog(frame, "ተናጋሪው ወጥቷል", null, JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void constructUI () {
        setContentPane(tabbedPane);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void clearFields (Field field) {
        switch (field) {
            case CONGREGATION:
                congregationNameField.setText("");
                break;
            case TALK:
                talkTitleTextField.setText("");
                break;
            case ELDER:
                firstNameTextField.setText("");
                middleNameTextField.setText("");
                lastNameTextField.setText("");
                phoneNumberTextField.setText("");
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        GeneratorUI UI = new GeneratorUI();
        UI.constructUI();
    }
}
