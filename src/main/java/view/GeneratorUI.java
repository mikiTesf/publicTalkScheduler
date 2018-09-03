package view;

import com.publictalkgenerator.domain.Congregation;
import com.publictalkgenerator.domain.Talk;
import com.publictalkgenerator.domain.Elder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class GeneratorUI extends JFrame {

    private JTextField firstNameTextField;
    private JTextField middleNameTextField;
    private JTextField lastNameTextField;
    private JTextField phoneNumberTextField;
    private JComboBox congregationComboBox;
    private JTabbedPane tabbedPane;
    private JButton addElderButton;
    private JTextField congregationNameField;
    private JButton addCongregationButton;
    private JTextField talkTitleTextField;
    private JSpinner talkNumberSpinner1;
    private JComboBox talkNumberComboBox2;
    private JButton addTalkButton;
    private JLabel congregationNameTextField;
    private JLabel talkNameTextField;
    private JLabel talkNumberSpinner;

    private GeneratorUI() {
        setTitle("የንግግር ፕሮግራም አመንጪ");
        // congregation_talk tab button events
        addCongregationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (congregationNameField.getText().equals(""))
                    return;
                try {
                    Congregation.getCongregationDao()
                            .createIfNotExists(new Congregation(congregationNameField.getText()));
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
                Talk talk = new Talk((int) talkNumberSpinner1.getValue(), talkTitleTextField.getText());
                try {
                    Talk.getTalkDao().createIfNotExists(talk);
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
                            .eq("talkNumber", talkNumberComboBox2.getSelectedItem())
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
            }
        });
    }

    private void constructUI () {
        setContentPane(tabbedPane);

        pack();
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GeneratorUI ui = new GeneratorUI();
        ui.constructUI();
    }
}
