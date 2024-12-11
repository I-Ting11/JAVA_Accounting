import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Record {
    String date;       // 日期
    String type;       //  "支出"
    String category;   // 生活分類
    String description;
    double amount;

    public Record(String date, String type, String category, String description, double amount) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date + " | " + type + " [" + category + "] - " + description + ": " + amount + " 元";
    }
}

public class DailyAccountingGUI extends JFrame {

    private ArrayList<Record> records = new ArrayList<>();
    private double balance = 0.0;

    // GUI 元件
    private JTextArea recordArea;
    private JTextField dateField, typeField, categoryField, descriptionField, amountField;

    public DailyAccountingGUI() {
        setTitle("日常記帳系統");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 上方：輸入區域
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("新增記錄"));

        inputPanel.add(new JLabel("日期（格式: yyyy-MM-dd，留空為今天）："));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("類型（收入/支出）："));
        typeField = new JTextField();
        inputPanel.add(typeField);

        inputPanel.add(new JLabel("分類（餐飲/娛樂等）："));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("描述："));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("金額："));
        amountField = new JTextField();
        inputPanel.add(amountField);

        JButton addButton = new JButton("新增記錄");
        inputPanel.add(addButton);

        JButton calculateButton = new JButton("目前總支出");
        inputPanel.add(calculateButton);

        add(inputPanel, BorderLayout.NORTH);

        // 中間：顯示區域
        recordArea = new JTextArea();
        recordArea.setEditable(false);
        recordArea.setBorder(BorderFactory.createTitledBorder("記帳記錄"));
        add(new JScrollPane(recordArea), BorderLayout.CENTER);

        // 下方：按分類查看
        JPanel categoryPanel = new JPanel(new BorderLayout());
        JTextField categorySearchField = new JTextField();
        JButton searchButton = new JButton("按分類查詢");
        categoryPanel.add(new JLabel("輸入分類："), BorderLayout.WEST);
        categoryPanel.add(categorySearchField, BorderLayout.CENTER);
        categoryPanel.add(searchButton, BorderLayout.EAST);
        add(categoryPanel, BorderLayout.SOUTH);

        // 事件處理
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBalance();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRecordsByCategory(categorySearchField.getText());
            }
        });
    }

    private void addRecord() {
        String date = dateField.getText();
        if (date.isEmpty()) {
            // 若日期為空，使用今天的日期
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        String type = typeField.getText();
        String category = categoryField.getText();
        String description = descriptionField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "金額必須是數字！", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (type.equals("支出")) {
            amount = -amount;
        }

        records.add(new Record(date, type, category, description, amount));
        balance += amount;

        // 清空輸入欄位
        dateField.setText("");
        typeField.setText("");
        categoryField.setText("");
        descriptionField.setText("");
        amountField.setText("");

        updateRecordArea();
        JOptionPane.showMessageDialog(this, "記錄已新增！", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateRecordArea() {
        StringBuilder sb = new StringBuilder();
        for (Record record : records) {
            sb.append(record).append("\n");
        }
        recordArea.setText(sb.toString());
    }

    private void showBalance() {
        JOptionPane.showMessageDialog(this, "目前總餘額為：" + String.format("%.2f", balance) + " 元", "總餘額", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewRecordsByCategory(String category) {
        StringBuilder sb = new StringBuilder();
        for (Record record : records) {
            if (record.category.equals(category)) {
                sb.append(record).append("\n");
            }
        }
        if (sb.length() == 0) {
            sb.append("該分類下沒有記錄。");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "分類記錄", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DailyAccountingGUI().setVisible(true));
    }
}
