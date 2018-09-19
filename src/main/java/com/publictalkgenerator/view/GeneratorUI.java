package com.publictalkgenerator.view;

import com.publictalkgenerator.Constants;
import com.publictalkgenerator.controller.ExcelFileGenerator;
import com.publictalkgenerator.controller.ProgramDate;
import com.publictalkgenerator.controller.ProgramGenerator;
import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Talk;
import com.publictalkgenerator.domain.Elder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.GregorianCalendar;

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
    private JComboBox<String> talkNumberComboBox;
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
    private JSpinner startDateDaySpinner;
    private JComboBox startDateMonthComboBox;
    private JSpinner startDateYearSpinner;
    private JSpinner endDateDaySpinner;
    private JComboBox endDateMonthComboBox;
    private JSpinner endDateYearSpinner;
    private JButton generateButton;
    private JButton updateCongregationButton;
    private JButton updateTalkButton;
    private JButton updateElderButton;
    private JSpinner totalEldersSpinner;
    private List<Congregation> congList;
    private List<Talk> talkList;

    public GeneratorUI() {
        setTitle(Constants.FRAME_TITLE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        congList              = null;
        talkList              = null;
        List<Elder> elderList = null;

        try {
            congList  = Congregation.getCongregationDao().queryForAll();
            talkList  = Talk.getTalkDao().queryForAll();
            elderList = Elder.getElderDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // setting up the TableModel(s) for congregation, talk and elder
        congregationTableModel = new DefaultTableModel () {
            @Override
            public boolean isCellEditable (int row, int column) {
                return column != 0;
            }
        };
        congregationTableModel.addColumn(Constants.CONGREGATION_TABLE_ID_TITLE);
        congregationTableModel.addColumn(Constants.CONGREGATION_TABLE_NAME_TITLE);
        congregationTableModel.addColumn(Constants.CONGREGATION_TABLE_TOTAL_ELDERS_TITLE);

        talkTableModel = new DefaultTableModel () {
            @Override
            public boolean isCellEditable (int row, int column) {
                return column != 0;
            }
        };
        talkTableModel.addColumn(Constants.TALK_TABLE_ID_TITLE);
        talkTableModel.addColumn(Constants.TALK_TABLE_TITLE_TITLE);
        talkTableModel.addColumn(Constants.TALK_TABLE_TALK_NUMBER_TITLE);

        elderTableModel = new DefaultTableModel () {
            @Override
            public boolean isCellEditable (int row, int column) {
                return column != 0;
            }
        };
        elderTableModel.addColumn(Constants.ELDER_TABLE_ID_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_FIRST_NAME_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_MIDDLE_NAME_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_LAST_NAME_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_PHONE_NUMBER_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_TALK_NUMBER_TITLE);
        elderTableModel.addColumn(Constants.ELDER_TABLE_CONGREGATION_TITLE);

        // fill the tables on the congregation/talk tab
        Object[] congTalkElderTableRow = new Object[3];
        for (Congregation cong : congList) {
            congTalkElderTableRow[0] = cong.getId();
            congTalkElderTableRow[1] = cong.getName();
            congTalkElderTableRow[2] = cong.getTotalElders();
            congregationTableModel.addRow(congTalkElderTableRow);
        }

        Object[] talkTableRows = new Object[3];
        for (Talk talk : talkList) {
            talkTableRows[0] = talk.getId();
            talkTableRows[1] = talk.getTitle();
            talkTableRows[2] = talk.getTalkNumber();
            talkTableModel.addRow(talkTableRows);
        }

        Object[] elderTableRows = new Object[7];
        for (Elder elder : elderList) {
            elderTableRows[0] = elder.getId();
            elderTableRows[1] = elder.getFirstName();
            elderTableRows[2] = elder.getMiddleName();
            elderTableRows[3] = elder.getLastName();
            elderTableRows[4] = elder.getPhoneNumber();
            elderTableRows[5] = elder.getTalk().getTalkNumber();
            elderTableRows[6] = elder.getCongregation().getName();
            elderTableModel.addRow(elderTableRows);
        }
        // fill congregation and talk comboBoxes in the ተናጋሪ tab
        for (Congregation congregation : congList) {
            congregationComboBox.addItem(congregation.getName());
        }

        for (Talk talk : talkList) {
            talkNumberComboBox.addItem(talk.getTalkNumber() + " - " + talk.getTitle());
        }

        congregationTable.setModel(congregationTableModel);
        congregationTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        talkTable.setModel(talkTableModel);
        talkTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        elderTable.setModel(elderTableModel);
        elderTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        congTabelScrollPane.setPreferredSize(new Dimension(400, 150));
        talkTableScrollPane.setPreferredSize(new Dimension(400, 150));
        elderTableScrollPane.setPreferredSize(new Dimension(400, 150));
        // congregation_talk tab button events
        addCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (congregationNameField.getText().equals(""))
                    return;

                Congregation congregation = new Congregation(congregationNameField.getText());
                congregation.setTotalElders((int) totalEldersSpinner.getValue());
                try {
                    congregation = Congregation.getCongregationDao().createIfNotExists(congregation);
                    Object[] congregationDetails = new Object[3];
                    congregationDetails[0] = congregation.getId();
                    congregationDetails[1] = congregation.getName();
                    congregationDetails[2] = congregation.getTotalElders();
                    congregationTableModel.addRow(congregationDetails);
                    clearFields(Field.CONGREGATION);
                    congregationComboBox.addItem(congregation.getName());
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.CONGREGATION_ADDED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        updateCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = congregationTable.getSelectedRow();

                Congregation congregation = new Congregation();
                congregation.setId( (int) congregationTable.getValueAt(selectedRow, 0));
                congregation.setName(congregationTable.getValueAt(selectedRow, 1).toString());
                congregation.setTotalElders(Integer.parseInt(congregationTable.getValueAt(selectedRow, 2).toString()));
                try {
                    Congregation.getCongregationDao().update(congregation);
                    refreshCongregationComboBox();
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.CONGREGATION_UPDATED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        addTalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (talkTitleTextField.getText().equals("") || talkNumberSpinner1.getValue() == null)
                    return;
                Talk talk = new Talk(talkTitleTextField.getText(), (int) talkNumberSpinner1.getValue());
                try {
                    talk = Talk.getTalkDao().createIfNotExists(talk);
                    Object[] talkDetails = new Object[3];
                    talkDetails[0] = talk.getId();
                    talkDetails[2] = talk.getTitle();
                    talkDetails[1] = talk.getTalkNumber();
                    clearFields(Field.TALK);
                    talkNumberComboBox.addItem(talk.getTalkNumber() + " - " + talk.getTitle());
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.TALK_ADDED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        updateTalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = talkTable.getSelectedRow();
                Talk talk = new Talk();

                talk.setId( (int) talkTable.getValueAt(selectedRow, 0));
                talk.setTalkNumber(Integer.parseInt(talkTable.getValueAt(selectedRow, 2).toString()));
                talk.setTitle(talkTable.getValueAt(selectedRow, 1).toString());

                try {
                    Talk.getTalkDao().update(talk);
                    refreshTalkComboBox();
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.TALK_UPDATED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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
                elder.setMiddleName(middleNameTextField.getText());
                elder.setLastName(lastNameTextField.getText());
                elder.setPhoneNumber(phoneNumberTextField.getText());

                Congregation congregation = null;
                try {
                    congregation = Congregation.getCongregationDao()
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
                    int spaceBeginIndex = talkNumberComboBox.getSelectedItem()
                                                            .toString()
                                                            .indexOf(" ");
                    int talkNumber      = Integer.parseInt (
                                            talkNumberComboBox.getSelectedItem()
                                                              .toString()
                                                              .substring(0, spaceBeginIndex)
                    );
                    talk = Talk.getTalkDao()
                               .queryBuilder()
                               .where()
                               .eq("talkNumber", talkNumber)
                               .query()
                               .get(0);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elder.setTalk(talk);

                try {
                    elder = Elder.getElderDao().createIfNotExists(elder);
                    Object[] elderDetails = new Object[7];
                    elderDetails[0] = elder.getId();
                    elderDetails[1] = elder.getFirstName();
                    elderDetails[2] = elder.getMiddleName();
                    elderDetails[3] = elder.getLastName();
                    elderDetails[4] = elder.getPhoneNumber();
                    elderDetails[5] = elder.getTalk().getTalkNumber();
                    elderDetails[6] = elder.getCongregation().getName();
                    elderTableModel.addRow(elderDetails);
                    clearFields(Field.ELDER);
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.ELDER_ADDED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        updateElderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = elderTable.getSelectedRow();
                Elder elder = new Elder();

                elder.setId((int) elderTable.getValueAt(selectedRow, 0));
                elder.setFirstName(elderTable.getValueAt(selectedRow, 1).toString());
                elder.setMiddleName(elderTable.getValueAt(selectedRow, 2).toString());
                elder.setLastName(elderTable.getValueAt(selectedRow, 3).toString());
                elder.setPhoneNumber(elderTable.getValueAt(selectedRow, 4).toString());

                Talk talk = null;
                try {
                    talk = Talk.getTalkDao()
                            .queryBuilder()
                            .where()
                            .eq("talkNumber", elderTable.getValueAt(selectedRow, 5))
                            .query().get(0);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elder.setTalk(talk);

                Congregation congregation = null;
                try {
                    congregation = Congregation.getCongregationDao()
                            .queryBuilder()
                            .where()
                            .eq("name", elderTable.getValueAt(selectedRow, 6))
                            .query().get(0);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                elder.setCongregation(congregation);

                try {
                    Elder.getElderDao().update(elder);
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.ELDER_UPDATED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        removeCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = congregationTable.getSelectedRow();
                try {
                    String congName = congregationTable.getValueAt(selectedRow, 1).toString();
                    Congregation.getCongregationDao()
                            .deleteById((int) congregationTable.getValueAt(selectedRow, 0));
                    congregationTableModel.removeRow(selectedRow);
                    congregationComboBox.removeItem(congName);
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.CONGREGATION_REMOVED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        removeTalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = talkTable.getSelectedRow();
                try {
                    String talkNumberPlusTitle = talkTable.getValueAt(selectedRow, 2)
                            + talkTable.getValueAt(selectedRow, 1).toString();
                    Talk.getTalkDao().deleteById((int) talkTable.getValueAt(selectedRow, 0));
                    talkTableModel.removeRow(selectedRow);
                    talkNumberComboBox.removeItem(talkNumberPlusTitle);
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.TALK_REMOVED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        removeElderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int elderID = (int) elderTable.getValueAt(elderTable.getSelectedRow(), 0);
                try {
                    Elder.getElderDao().deleteById(elderID);
                    elderTableModel.removeRow(elderTable.getSelectedRow());
                    JOptionPane.showMessageDialog(
                            frame,
                            Constants.ELDER_REMOVED_MESSAGE,
                            Constants.SUCCESS_TITLE,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        totalEldersSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        startDateDaySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        endDateDaySpinner.setModel(new SpinnerNumberModel(1, 1, 31, 1));
        startDateYearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));
        endDateYearSpinner.setModel(new SpinnerNumberModel(2018, 2018, 3000, 1));

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDate startDate = ProgramDate.dateToLocalDate(
                        new GregorianCalendar(
                                (int) startDateYearSpinner.getValue(),
                                Constants.monthNumber.get(startDateMonthComboBox.getSelectedItem()),
                                (int) startDateDaySpinner.getValue()
                        ).getTime()
                );
                LocalDate endDate   = ProgramDate.dateToLocalDate(
                        new GregorianCalendar(
                                (int) endDateYearSpinner.getValue(),
                                Constants.monthNumber.get(endDateMonthComboBox.getSelectedItem()),
                                (int) endDateDaySpinner.getValue()
                        ).getTime()
                );

                try {
                    ProgramGenerator generator = new ProgramGenerator(startDate, endDate);
                    generator.doGenerate();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                ExcelFileGenerator fileGenerator = new ExcelFileGenerator();
                fileGenerator.createExcel();
            }
        });
    }

    public void constructUI () {
        setContentPane(tabbedPane);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
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

    private void refreshCongregationComboBox () {
        try {
            congList = Congregation.getCongregationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        congregationComboBox.removeAllItems();
        for (Congregation cong : congList) {
            congregationComboBox.addItem(cong.getName());
        }
    }

    private void refreshTalkComboBox () {
        try {
            talkList = Talk.getTalkDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        talkNumberComboBox.removeAllItems();
        for (Talk talk : talkList) {
            talkNumberComboBox.addItem(talk.getTalkNumber() + " - " + talk.getTitle());
        }
    }
}
