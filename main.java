import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
public class TaskManager {
private JFrame mainFrame;
private JPanel calendarPanel, completedPanel, pendingPanel;
private JLabel selectedDateLabel;
private LocalDate selectedDate = LocalDate.now();
private DefaultTableModel taskTableModel, completedTableModel, pendingTableModel;
public TaskManager() {
createMainPage();
}
private void createMainPage() {
mainFrame = new JFrame("Task Manager");
mainFrame.setSize(1920, 1080);
mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
mainFrame.setLayout(new BorderLayout());
// Left: Calendar Panel
JPanel leftPanel = new JPanel(new BorderLayout());
leftPanel.setPreferredSize(new Dimension(500, 1080));
leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
leftPanel.setBackground(new Color(255, 230, 204));
selectedDateLabel = new JLabel("Selected Date: " + selectedDate, SwingConstants.CENTER);
selectedDateLabel.setFont(new Font("Arial", Font.BOLD, 18));
selectedDateLabel.setForeground(new Color(51, 102, 255));
calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
calendarPanel.setBackground(new Color(255, 255, 204));
updateCalendarPanel();
leftPanel.add(selectedDateLabel, BorderLayout.NORTH);
leftPanel.add(calendarPanel, BorderLayout.CENTER);
// Right: Task Management Panel
JPanel rightPanel = new JPanel(new BorderLayout());
rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
rightPanel.setBackground(new Color(204, 255, 229));
// Task Input Section
JPanel taskInputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
taskInputPanel.setBackground(new Color(204, 255, 229));
taskInputPanel.setBorder(BorderFactory.createTitledBorder("Add New Task"));
JTextField taskField = new JTextField();
JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Work", "Personal", "Other"});
JButton addTaskButton = new JButton("Add Task");
addTaskButton.setBackground(new Color(255, 153, 51));
addTaskButton.setForeground(Color.WHITE);
addTaskButton.setFont(new Font("Arial", Font.BOLD, 14));

addTaskButton.addActionListener(e -> {
String task = taskField.getText().trim();
String priority = (String) priorityComboBox.getSelectedItem();
String category = (String) categoryComboBox.getSelectedItem();
if (!task.isEmpty()) {
taskTableModel.addRow(new Object[]{task, priority, category, selectedDate});
pendingTableModel.addRow(new Object[]{task, priority, category});
taskField.setText("");
}
});
taskInputPanel.add(new JLabel("Task:"));
taskInputPanel.add(taskField);
taskInputPanel.add(new JLabel("Priority:"));
taskInputPanel.add(priorityComboBox);
taskInputPanel.add(new JLabel("Category:"));
taskInputPanel.add(categoryComboBox);
taskInputPanel.add(new JLabel());
taskInputPanel.add(addTaskButton);
// Task Table Section
taskTableModel = new DefaultTableModel(new String[]{"Task", "Priority", "Category", "Date"}, 0);
JTable taskTable = new JTable(taskTableModel);
taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
// Complete Task Button
JButton markAsCompleteButton = new JButton("Complete Task");
markAsCompleteButton.setBackground(new Color(102, 204, 102));
markAsCompleteButton.setForeground(Color.WHITE);
markAsCompleteButton.setFont(new Font("Arial", Font.BOLD, 14));
// Initially disable the button
markAsCompleteButton.setEnabled(false);
// Button ActionListener for completing a task
markAsCompleteButton.addActionListener(e -> {
int selectedRow = taskTable.getSelectedRow();
if (selectedRow != -1) {
// Retrieve the task details from the selected row
String task = (String) taskTableModel.getValueAt(selectedRow, 0);
String priority = (String) taskTableModel.getValueAt(selectedRow, 1);
String category = (String) taskTableModel.getValueAt(selectedRow, 2);
// Add task to the Completed Tasks table
completedTableModel.addRow(new Object[]{task, priority, category});
// Remove task from Pending Tasks table
removeFromPending(task);
taskTableModel.removeRow(selectedRow);
// Optionally, refresh the completed and pending task panels to reflect the changes
completedPanel.revalidate();
completedPanel.repaint();
pendingPanel.revalidate();
pendingPanel.repaint();
} else {
JOptionPane.showMessageDialog(mainFrame, "Please select a task to complete.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
}
});
// Add a row selection listener to enable the button when a task is selected
taskTable.getSelectionModel().addListSelectionListener(e -> {
boolean taskSelected = taskTable.getSelectedRow() != -1;
markAsCompleteButton.setEnabled(taskSelected);
});
JPanel taskTablePanel = new JPanel(new BorderLayout());
taskTablePanel.setBackground(new Color(204, 255, 229));
taskTablePanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
taskTablePanel.add(markAsCompleteButton, BorderLayout.SOUTH);
// Completed and Pending Tasks Section
JPanel taskStatusPanel = new JPanel(new GridLayout(1, 2, 10, 10));
completedPanel = createTaskStatusPanel("Completed Tasks", completedTableModel = new DefaultTableModel(new String[]{"Task", "Priority", "Category"}, 0), new Color(204, 255, 204));
pendingPanel = createTaskStatusPanel("Pending Tasks", pendingTableModel = new DefaultTableModel(new String[]{"Task", "Priority", "Category"}, 0), new Color(255, 204, 204));
taskStatusPanel.add(completedPanel);
taskStatusPanel.add(pendingPanel);
// Add panels to the rightPanel
rightPanel.add(taskInputPanel, BorderLayout.NORTH);
rightPanel.add(taskTablePanel, BorderLayout.CENTER);
rightPanel.add(taskStatusPanel, BorderLayout.SOUTH);

// Add panels to the main frame
mainFrame.add(leftPanel, BorderLayout.WEST);
mainFrame.add(rightPanel, BorderLayout.CENTER);
mainFrame.setVisible(true);
}
private JPanel createTaskStatusPanel(String title, DefaultTableModel tableModel, Color bgColor) {
JPanel panel = new JPanel(new BorderLayout());
panel.setBorder(BorderFactory.createTitledBorder(title));
panel.setBackground(bgColor);
JTable table = new JTable(tableModel);
table.setBackground(bgColor);
panel.add(new JScrollPane(table), BorderLayout.CENTER);
return panel;
}
private void removeFromPending(String task) {
// Iterate through the rows in the pending table and find the task
for (int i = 0; i < pendingTableModel.getRowCount(); i++) {
if (pendingTableModel.getValueAt(i, 0).equals(task)) {
// Remove the task from the pending table
pendingTableModel.removeRow(i);
break;
}
}
}
private void updateCalendarPanel() {
calendarPanel.removeAll();
// Add day names
String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
for (String day : days) {
JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
dayLabel.setFont(new Font("Arial", Font.BOLD, 14));
calendarPanel.add(dayLabel);
}
// Get the first day and total days of the month
YearMonth yearMonth = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
int daysInMonth = yearMonth.lengthOfMonth();
LocalDate firstDayOfMonth = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
// Adjust to start on Sunday
firstDayOfWeek = (firstDayOfWeek == 7) ? 0 : firstDayOfWeek;
// Fill blank days before the start of the month
for (int i = 0; i < firstDayOfWeek; i++) {
calendarPanel.add(new JLabel(""));
}
// Add buttons for each day of the month
for (int day = 1; day <= daysInMonth; day++) {
int currentDay = day;
JButton dayButton = new JButton(String.valueOf(day));
dayButton.setBackground(new Color(255, 204, 153));
dayButton.setFont(new Font("Arial", Font.BOLD, 12));
dayButton.addActionListener(e -> {
selectedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), currentDay);
selectedDateLabel.setText("Selected Date: " + selectedDate);
});
calendarPanel.add(dayButton);
}
calendarPanel.revalidate();
calendarPanel.repaint();
}
public static void main(String[] args) {
SwingUtilities.invokeLater(TaskManager::new);
}
}                                                                                                      
