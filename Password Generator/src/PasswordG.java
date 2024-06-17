import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class PasswordG extends JDialog {
    private JTextField dispPassword;
    private JTextField lengthTextField;
    private JButton generateButton;
    private JButton COPYButton;
    private JButton regenerateButton;
    private JPanel PasswordScreen;
    private JLabel lockImage;
    private JSlider slider1;
    private JButton OKButton;
    private JButton saveButton;

    public PasswordG(JFrame parent) {
        super(parent);
        setTitle("Random Password Generator");
        setContentPane(PasswordScreen);
        setMinimumSize(new Dimension(450,550));
        setModal(true);
        setLocationRelativeTo(parent);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
        regenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
        COPYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nLength;
                nLength = slider1.getValue();
                lengthTextField.setText(Integer.toString(nLength));
            }
        });

        slider1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int nLength2;
                nLength2 = slider1.getValue();
                lengthTextField.setText(Integer.toString(nLength2));
            }
        });
        slider1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int nLength2;
                nLength2 = slider1.getValue();
                lengthTextField.setText(Integer.toString(nLength2));
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePassword();
            }
        });

        setVisible(true);
    }

    private void savePassword() {
        String password = dispPassword.getText();
        JTextField domainField = new JTextField();
        JTextField passwordField = new JTextField(password);
        passwordField.setEditable(false);

        Object[] message = {
                "Domain : ", domainField,
                "Password: ", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Save Password", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            String domain = domainField.getText();
            if(!domain.isEmpty()) {
                New newEntry = new New();
                newEntry.domain = domain;
                newEntry.password = password;
                saveToDatabase(newEntry);
            } else {
                JOptionPane.showMessageDialog(this, "Domain cannot be Empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveToDatabase(New newEntry) {
        final String DB_URL = "jdbc:mysql://localhost/se_project?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO new (domain, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newEntry.domain);
            preparedStatement.setString(2, newEntry.password);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Password saved for domain: " + newEntry.domain);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving password to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePassword() {
        try {
            // Sanitize input by trimming whitespace
            String input = lengthTextField.getText().trim();
            // Parse the input length
            int N = Integer.parseInt(input);
            // Generate and display the password
            String password = generateRandomPassword(N);
            dispPassword.setText(password);
        } catch (NumberFormatException e) {
            // Handle invalid input gracefully
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the password length.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyToClipboard() {
        String str = dispPassword.getText();
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection str1 = new StringSelection(str);
        clip.setContents(str1, null);
        JOptionPane.showMessageDialog(null, "Copied!");
    }

    private String generateRandomPassword(int N) {
        String numbers = "0123456789";
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String caps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols = "_@#$!%^&*=-|";

        String passChar = numbers + letters + caps + symbols;
        StringBuilder password = new StringBuilder();

        Random random = new Random();
        for(int i = 0 ; i < N ; i++) {
            int index = random.nextInt(passChar.length());
            password.append(passChar.charAt(index));
        }
        return password.toString();

    }

    public static void main(String[] args) {
        PasswordG pg = new PasswordG(null);
    }
}
