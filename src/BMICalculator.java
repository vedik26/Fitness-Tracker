import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BMICalculator extends JFrame {
    private DatabaseHelper dbHelper;
    private String loggedInUsername;
    private JTable previousDataTable;

    public BMICalculator() {
        dbHelper = new DatabaseHelper();
        setupLookAndFeel();
        setupLoginUI();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLoginUI() {
        setTitle("BMI Calculator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(143, 188, 143));

        JPanel containerPanel = createStyledPanel(new BorderLayout());
        containerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(143, 188, 143));
        tabbedPane.setForeground(new Color(57, 121, 94));

        JPanel registerPanel = createStyledPanel(new GridLayout(3, 2, 15, 15));
        JTextField regUsername = createStyledTextField();
        JPasswordField regPassword = createStyledPasswordField();
        JButton registerButton = new RoundedButton("Register");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = regUsername.getText();
                String password = new String(regPassword.getPassword());
                dbHelper.registerUser(username, password);
                JOptionPane.showMessageDialog(null, "User registered successfully!");
            }
        });

        registerPanel.add(createStyledLabel("Username:")).setBounds(50,10,100,30);
        registerPanel.add(regUsername).setBounds(240,10,170,30);
        registerPanel.add(createStyledLabel("Password:")).setBounds(50,60,100,30);
        registerPanel.add(regPassword).setBounds(240,60,170,30);
        registerPanel.add(new JLabel(""));
        registerPanel.add(registerButton).setBounds(240,100,100,30);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 70), 2, true));

        registerPanel.setLayout(null);

        JPanel loginPanel = createStyledPanel(new GridLayout(3, 2, 15, 15));
        JTextField loginUsername = createStyledTextField();
        JPasswordField loginPassword = createStyledPasswordField();
        JButton loginButton = new RoundedButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginUsername.getText();
                String password = new String(loginPassword.getPassword());
                if (dbHelper.authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    loggedInUsername = username;
                    setupBMICalculatorUI();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });

        loginPanel.add(createStyledLabel("Username:")).setBounds(50,10,100,30);
        loginPanel.add(loginUsername).setBounds(240,10,170,30);
        loginPanel.add(createStyledLabel("Password:")).setBounds(50,60,100,30);
        loginPanel.add(loginPassword).setBounds(240,60,170,30);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton).setBounds(240,100,100,30);
        loginPanel.setLayout(null);

        tabbedPane.add("Register", registerPanel);
        tabbedPane.add("Login", loginPanel);

        containerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(containerPanel);
    }
    private JLabel createBackground() {
        ImageIcon icon = new ImageIcon("C:\\Users\\Lenovo\\OneDrive\\Desktop\\Screenshot 2026-02-15 155316.png");
        Image img = icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(img));
        background.setLayout(new BorderLayout());
        return background;
    }


    private void setupBMICalculatorUI() {
        getContentPane().removeAll();
        setTitle("BMI Calculator");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JLabel background = createBackground();
        setContentPane(background);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false); // Makes panel transparent
        containerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        containerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel inputPanel = createStyledPanel(new GridLayout(6, 2, 15, 15));
        JTextField nameField = createStyledTextField();
        JTextField heightField = createStyledTextField();
        JTextField weightField = createStyledTextField();
        JButton calculateButton = createStyledButton("Calculate BMI");
        JButton consultDoctorButton = createStyledButton("Consult Doctor");
        JButton viewDataButton = createStyledButton("View Previous Data");
        JButton getDietPlanButton = createStyledButton("Get Weekly Diet Plan");

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    double height = Double.parseDouble(heightField.getText());
                    double weight = Double.parseDouble(weightField.getText());
                    double bmi = weight / (height * height);
                    String status = getStatus(bmi);

                    dbHelper.insertRecord(loggedInUsername, name, height, weight, bmi, status);

                    JOptionPane.showMessageDialog(null, String.format("BMI: %.2f\nStatus: %s", bmi, status));
                    displayPreviousData();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for height and weight.");
                }
            }
        });

        consultDoctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupAppointmentUI();
            }
        });

        viewDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPreviousData();
            }
        });

        getDietPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double bmi = weight / (height * height);
                String status = getStatus(bmi);
                String dietRecommendation = getDietRecommendation(status);

                JTextArea dietPlanTextArea = new JTextArea(10, 40);
                dietPlanTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                dietPlanTextArea.setForeground(new Color(0, 128, 60));
                dietPlanTextArea.setBackground(new Color(255, 255, 255));
                dietPlanTextArea.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 41), 2));
                dietPlanTextArea.setText(String.format("Diet Recommendation for %s:\n\n%s\n\nWeekly Diet Plan:\n\nDay 1: Milk\nDay 2: ...\nDay 3: ...\nDay 4: ...\nDay 5: ...\nDay 6: ...\nDay 7: ...", name, dietRecommendation));

                JScrollPane scrollPane = new JScrollPane(dietPlanTextArea);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                JOptionPane.showMessageDialog(null, scrollPane, "Weekly Diet Plan", JOptionPane.PLAIN_MESSAGE);
            }
        });

        inputPanel.add(createStyledLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Height (meters):"));
        inputPanel.add(heightField);
        inputPanel.add(createStyledLabel("Weight (kg):"));
        inputPanel.add(weightField);
        inputPanel.add(calculateButton);
        inputPanel.add(consultDoctorButton);
        inputPanel.add(viewDataButton);
        inputPanel.add(getDietPlanButton);

        containerPanel.add(inputPanel, BorderLayout.NORTH);

        previousDataTable = new JTable();
        previousDataTable.setFont(new Font("Arial", Font.PLAIN, 14));
        previousDataTable.setForeground(new Color(0, 0, 0));
        previousDataTable.setGridColor(new Color(143, 188, 143));
        previousDataTable.setBackground(new Color(255, 255, 255));
        JScrollPane scrollPane = new JScrollPane(previousDataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        add(containerPanel);
        revalidate();
        repaint();
    }

    private void displayPreviousData() {
        List<String> records = dbHelper.getPreviousData(loggedInUsername);
        String[] columnNames = {"Name", "Height", "Weight", "BMI", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String record : records) {
            String[] data = record.split(", ");
            model.addRow(data);
        }

        previousDataTable.setModel(model);
    }

    private void setupAppointmentUI() {
        getContentPane().removeAll();
        setTitle("Book Appointment");
        setSize(600,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(143, 188, 143));

        JPanel containerPanel = createStyledPanel(null); // Null layout for custom placement
        containerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));


        JLabel patientNameLabel = createStyledLabel("Patient Name:");
        JTextField patientNameField = createStyledTextField();

        JLabel doctorNameLabel = createStyledLabel("Doctor Name:");
        JComboBox<String> doctorNameComboBox = new JComboBox<>(new String[]{"Dr. Smith", "Dr. Brown", "Dr. Lee"});
        doctorNameComboBox.setPreferredSize(new Dimension(200, 40));

        JLabel appointmentDateLabel = createStyledLabel("Appointment Date:");
        JComboBox<String> dayComboBox = new JComboBox<>(generateDays());
        JComboBox<String> monthComboBox = new JComboBox<>(generateMonths());
        JComboBox<String> yearComboBox = new JComboBox<>(generateYears());

        JButton bookAppointmentButton = new RoundedButton("Book Appointment");

        // Set component bounds (x, y, width, height)
        patientNameLabel.setBounds(100, 50, 150, 40);
        patientNameField.setBounds(300, 50, 200, 40);

        doctorNameLabel.setBounds(100, 120, 150, 40);
        doctorNameComboBox.setBounds(300, 120, 200, 40);

        appointmentDateLabel.setBounds(100, 190, 150, 40);
        dayComboBox.setBounds(300, 190, 60, 40);
        monthComboBox.setBounds(370, 190, 80, 40);
        yearComboBox.setBounds(460, 190, 80, 40);

        bookAppointmentButton.setBounds(300, 260, 200, 50);

        // Add components to the container panel
        containerPanel.add(patientNameLabel);
        containerPanel.add(patientNameField);
        containerPanel.add(doctorNameLabel);
        containerPanel.add(doctorNameComboBox);
        containerPanel.add(appointmentDateLabel);
        containerPanel.add(dayComboBox);
        containerPanel.add(monthComboBox);
        containerPanel.add(yearComboBox);
        containerPanel.add(bookAppointmentButton);

        // Book Appointment action listener
        bookAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientName = patientNameField.getText();
                String doctorName = (String) doctorNameComboBox.getSelectedItem();
                String appointmentDate = dayComboBox.getSelectedItem() + "-" +
                        monthComboBox.getSelectedItem() + "-" + yearComboBox.getSelectedItem();

                dbHelper.bookAppointment(patientName, doctorName, appointmentDate);
                JOptionPane.showMessageDialog(null, "Appointment booked successfully!");

                setupBMICalculatorUI();
            }
        });

        add(containerPanel);
        revalidate();
        repaint();
    }

    // Helper methods to generate days, months, and years
    private String[] generateDays() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        return days;
    }

    private String[] generateMonths() {
        return new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    }

    private String[] generateYears() {
        String[] years = new String[20];
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = 0; i < 20; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        return years;
    }


    private String getStatus(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }

    private String getDietRecommendation(String status) {
        switch (status) {
            case "Underweight":
                return "Increase your calorie intake by eating more nutritious foods like nuts, avocados, and whole grains. \n Consider adding protein-rich foods to your diet.";
            case "Normal weight":
                return "Maintain a balanced diet with a variety of fruits, vegetables, whole grains, and lean proteins.\n Stay hydrated and engage in regular physical activity.";
            case "Overweight":
                return "Reduce your calorie intake by focusing on low-calorie, nutrient-dense foods like leafy greens,\n berries, and lean meats.\n Increase your physical activity to burn more calories.";
            case "Obesity":
                return "Consult a healthcare professional for a personalized diet plan. \nThey can help you create a calorie-deficit diet while ensuring you get the necessary nutrients. Regular exercise is also important.";
            default:
                return "No diet recommendation available.";
        }
    }

    private JPanel createStyledPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0, 0, 0, 120)); // Semi-transparent black
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 40)));
        return panel;
    }


    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return textField;
    }


    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(new Color(255, 255, 255));
        passwordField.setForeground(new Color(0, 128, 64));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(120, 178, 87), 2));
        passwordField.setPreferredSize(new Dimension(200, 40));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public class RoundedButton extends JButton {

        public RoundedButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 16));
            setBackground(new Color(147, 234, 191));
            setForeground(new Color(0, 8, 66));
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setOpaque(false); // Set this to false for custom painting
            setPreferredSize(new Dimension(200, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 100, 100); // Adjust arc for rounding

            // Draw the border
            g2.setColor(new Color(84, 141, 71));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 100, 10);

            // Paint the text
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            // Prevents default border painting
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(229, 237, 255));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BMICalculator().setVisible(true);
            }
        });
    }
}