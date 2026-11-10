
package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TaskManagerGUI extends JFrame {

    private final TaskDAO dao = new TaskDAO();

    private final Color BG = new Color(15, 20, 35);
    private final Color SIDEBAR = new Color(20, 27, 45);
    private final Color PANEL = new Color(27, 36, 57);
    private final Color PANEL2 = new Color(34, 44, 67);
    private final Color BLUE = new Color(91, 120, 255);
    private final Color GREEN = new Color(48, 205, 145);
    private final Color RED = new Color(255, 100, 115);
    private final Color TEXT = new Color(235, 240, 250);
    private final Color MUTED = new Color(155, 168, 190);

    private JTable table;
    private DefaultTableModel model;

    private JLabel totalValue, pendingValue, completedValue;
    private JLabel overdueValue, completionValue;
    private JProgressBar progressBar;

    private JTextField searchField;
    private JComboBox<String> filterBox;

    private JTextField titleField, descriptionField, dateField;
    private JComboBox<String> priorityBox;

    public TaskManagerGUI() {
        setTitle("TaskFlow | Productivity Manager");
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createDashboard(), BorderLayout.CENTER);

        refreshTasks();
    }

    // SIDEBAR
    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(225, 0));
        side.setBackground(SIDEBAR);
        side.setBorder(BorderFactory.createEmptyBorder(30, 22, 25, 22));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("✦ TaskFlow");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        logo.setForeground(TEXT);

        JLabel sub = new JLabel("PRODUCTIVITY WORKSPACE");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        sub.setForeground(MUTED);

        side.add(logo);
        side.add(Box.createVerticalStrut(6));
        side.add(sub);
        side.add(Box.createVerticalStrut(55));

        JLabel menu = new JLabel("WORKSPACE");
        menu.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menu.setForeground(MUTED);

        side.add(menu);
        side.add(Box.createVerticalStrut(17));

        JLabel dashboard = sideItem("▦   Dashboard");
        JLabel allTasks = sideItem("✓   All Tasks");
        JLabel productivity = sideItem("◷   My Productivity");

        dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                searchField.setText("");
                filterBox.setSelectedItem("All");
                refreshTasks();
            }
        });

        allTasks.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                searchField.setText("");
                filterBox.setSelectedItem("All");
                refreshTasks();
                table.clearSelection();
            }
        });

        productivity.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showProductivity();
            }
        });

        side.add(dashboard);
        side.add(Box.createVerticalStrut(10));
        side.add(allTasks);
        side.add(Box.createVerticalStrut(10));
        side.add(productivity);

        side.add(Box.createVerticalGlue());

        JPanel profile = new JPanel();
        profile.setOpaque(false);
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));

        JLabel user = new JLabel("  STUDENT WORKSPACE");
        user.setForeground(TEXT);
        user.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel version = new JLabel("  TaskFlow v1.0");
        version.setForeground(MUTED);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        profile.add(user);
        profile.add(Box.createVerticalStrut(6));
        profile.add(version);

        side.add(profile);

        return side;
    }

    private JLabel sideItem(String name) {
        JLabel item = new JLabel(name);
        item.setFont(new Font("Segoe UI", Font.BOLD, 13));
        item.setForeground(MUTED);
        item.setOpaque(true);
        item.setBackground(SIDEBAR);
        item.setBorder(BorderFactory.createEmptyBorder(13, 12, 13, 12));
        item.setMaximumSize(new Dimension(190, 45));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(new Color(49, 67, 112));
                item.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                item.setBackground(SIDEBAR);
                item.setForeground(MUTED);
            }
        });

        return item;
    }

    // DASHBOARD
    private JPanel createDashboard() {
        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(28, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Organize your work. Focus on what matters."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        JButton refresh = button("↻ Refresh", PANEL2, TEXT);
        refresh.addActionListener(e -> refreshTasks());

        header.add(heading, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);

        totalValue = valueLabel(BLUE);
        pendingValue = valueLabel(new Color(255, 190, 85));
        completedValue = valueLabel(GREEN);
        overdueValue = valueLabel(RED);

        stats.add(statCard("TOTAL TASKS", totalValue, "All your tasks"));
        stats.add(statCard("PENDING", pendingValue, "Awaiting completion"));
        stats.add(statCard("COMPLETED", completedValue, "Successfully finished"));
        stats.add(statCard("OVERDUE", overdueValue, "Past their deadline"));

        body.add(stats, BorderLayout.NORTH);

        JPanel lower = new JPanel(new BorderLayout(0, 17));
        lower.setOpaque(false);

        lower.add(createTaskSection(), BorderLayout.CENTER);
        lower.add(createBottomSection(), BorderLayout.SOUTH);

        body.add(lower, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        return root;
    }

    private JLabel valueLabel(Color color) {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        label.setForeground(color);
        return label;
    }

    private JPanel statCard(String title, JLabel value, String desc) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(PANEL);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 59, 83)),
                BorderFactory.createEmptyBorder(18, 18, 17, 18)
        ));

        JLabel name = new JLabel(title);
        name.setFont(new Font("Segoe UI", Font.BOLD, 10));
        name.setForeground(MUTED);

        JLabel description = new JLabel(desc);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        description.setForeground(MUTED);

        card.add(name, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(description, BorderLayout.SOUTH);

        return card;
    }

    // TASK TABLE
    private JPanel createTaskSection() {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(PANEL);

        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 59, 83)),
                BorderFactory.createEmptyBorder(20, 20, 18, 20)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel heading = new JLabel("Your Tasks");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        heading.setForeground(TEXT);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tools.setOpaque(false);

        searchField = new JTextField(13);
        styleField(searchField);
        searchField.setToolTipText("Search task title");
        searchField.addActionListener(e -> applyFilter());

        filterBox = new JComboBox<>(
                new String[]{"All", "Pending", "Completed"}
        );
        styleCombo(filterBox);
        filterBox.addActionListener(e -> applyFilter());

        JButton search = button("Search", BLUE, Color.WHITE);
        search.addActionListener(e -> applyFilter());

        tools.add(searchField);
        tools.add(filterBox);
        tools.add(search);

        top.add(heading, BorderLayout.WEST);
        top.add(tools, BorderLayout.EAST);

        model = new DefaultTableModel(
                new String[]{
                        "ID", "TASK", "DESCRIPTION",
                        "DEADLINE", "PRIORITY", "STATUS"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(39);
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(52, 68, 105));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(48, 59, 83));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader th = table.getTableHeader();
        th.setBackground(PANEL2);
        th.setForeground(TEXT);
        th.setFont(new Font("Segoe UI", Font.BOLD, 10));
        th.setPreferredSize(new Dimension(0, 38));
        th.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(145);
        table.getColumnModel().getColumn(2).setPreferredWidth(175);
        table.getColumnModel().getColumn(3).setPreferredWidth(105);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(48, 59, 83)));
        scroll.getViewport().setBackground(PANEL);

        JPanel content = new JPanel(new BorderLayout(0, 13));
        content.setOpaque(false);
        content.add(top, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        section.add(content, BorderLayout.CENTER);

        return section;
    }

    // ADD TASK AND QUICK ACTIONS
    private JPanel createBottomSection() {
        JPanel bottom = new JPanel(new BorderLayout(15, 0));
        bottom.setOpaque(false);

        JPanel addPanel = new JPanel(new BorderLayout(0, 13));
        addPanel.setBackground(PANEL);

        addPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 59, 83)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel title = new JLabel("+  Add New Task");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);

        JPanel fields = new JPanel(new GridLayout(2, 2, 10, 9));
        fields.setOpaque(false);

        titleField = new JTextField();
        descriptionField = new JTextField();
        dateField = new JTextField();
        priorityBox = new JComboBox<>(
                new String[]{"High", "Medium", "Low"}
        );

        styleField(titleField);
        styleField(descriptionField);
        styleField(dateField);
        styleCombo(priorityBox);

        fields.add(fieldPanel("TASK TITLE", titleField));
        fields.add(fieldPanel("DESCRIPTION", descriptionField));
        fields.add(fieldPanel("DEADLINE (YYYY-MM-DD)", dateField));
        fields.add(fieldPanel("PRIORITY", priorityBox));

        JButton add = button("+ Add Task", BLUE, Color.WHITE);
        add.addActionListener(e -> addTask());

        addPanel.add(title, BorderLayout.NORTH);
        addPanel.add(fields, BorderLayout.CENTER);
        addPanel.add(add, BorderLayout.SOUTH);

        JPanel actions = new JPanel();
        actions.setBackground(PANEL);
        actions.setPreferredSize(new Dimension(175, 0));

        actions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 59, 83)),
                BorderFactory.createEmptyBorder(18, 12, 18, 12)
        ));

        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JLabel actionTitle = new JLabel("QUICK ACTIONS");
        actionTitle.setForeground(MUTED);
        actionTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        actionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton complete = button("✓ Complete", GREEN, Color.WHITE);
        JButton delete = button("✕ Delete", RED, Color.WHITE);

        complete.setAlignmentX(Component.LEFT_ALIGNMENT);
        delete.setAlignmentX(Component.LEFT_ALIGNMENT);

        complete.addActionListener(e -> completeTask());
        delete.addActionListener(e -> deleteTask());

        actions.add(actionTitle);
        actions.add(Box.createVerticalStrut(17));
        actions.add(complete);
        actions.add(Box.createVerticalStrut(10));
        actions.add(delete);
        actions.add(Box.createVerticalGlue());

        completionValue = new JLabel("0% completed");
        completionValue.setForeground(TEXT);
        completionValue.setFont(new Font("Segoe UI", Font.BOLD, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(GREEN);
        progressBar.setBackground(PANEL2);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(145, 9));

        actions.add(completionValue);
        actions.add(Box.createVerticalStrut(8));
        actions.add(progressBar);

        bottom.add(addPanel, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.EAST);

        return bottom;
    }

    private JPanel fieldPanel(String name, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(name);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 9));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    // STYLING
    private void styleField(JTextField field) {
        field.setBackground(PANEL2);
        field.setForeground(TEXT);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 78, 105)),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(PANEL2);
        combo.setForeground(TEXT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private JButton button(String text, Color color, Color foreground) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(foreground);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 13, 10, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
    }

    // ADD TASK
    private void addTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String dateText = dateField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a task title.");
            return;
        }

        LocalDate date = null;

        if (!dateText.isEmpty()) {
            try {
                date = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Use date format YYYY-MM-DD."
                );
                return;
            }
        }

        Task task = new Task(
                title,
                description,
                date,
                (String) priorityBox.getSelectedItem()
        );

        if (dao.addTask(task)) {
            titleField.setText("");
            descriptionField.setText("");
            dateField.setText("");

            refreshTasks();

            JOptionPane.showMessageDialog(
                    this,
                    "Task added successfully!"
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not add task. Check database connection."
            );
        }
    }

    // COMPLETE TASK
    private void completeTask() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        if (dao.updateStatus(id, "Completed")) {
            refreshTasks();

            JOptionPane.showMessageDialog(
                    this,
                    "Task marked completed!"
            );
        }
    }

    // DELETE TASK
    private void deleteTask() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this task?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION && dao.deleteTask(id)) {
            refreshTasks();
        }
    }

    // REFRESH TASKS AND UPDATE STATISTICS
    private void refreshTasks() {
        model.setRowCount(0);

        ArrayList<Task> tasks = dao.getAllTasks();

        int total = tasks.size();
        int pending = 0;
        int completed = 0;
        int overdue = 0;

        for (Task task : tasks) {
            boolean done = task.getStatus().equals("Completed");

            if (done) {
                completed++;
            } else {
                pending++;

                if (task.getDueDate() != null &&
                        task.getDueDate().isBefore(LocalDate.now())) {
                    overdue++;
                }
            }

            model.addRow(new Object[]{
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate() == null
                            ? "No deadline"
                            : task.getDueDate(),
                    task.getPriority(),
                    task.getStatus()
            });
        }

        totalValue.setText(String.valueOf(total));
        pendingValue.setText(String.valueOf(pending));
        completedValue.setText(String.valueOf(completed));
        overdueValue.setText(String.valueOf(overdue));

        int percentage = total == 0
                ? 0
                : completed * 100 / total;

        progressBar.setValue(percentage);
        completionValue.setText(percentage + "% completed");

        applyFilter();
    }

    // SEARCH AND FILTER
    private void applyFilter() {
        if (model == null) {
            return;
        }

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);

        table.setRowSorter(sorter);

        String query = searchField.getText().trim();
        String status = (String) filterBox.getSelectedItem();

        ArrayList<RowFilter<Object, Object>> filters =
                new ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter(
                    "(?i)" + java.util.regex.Pattern.quote(query),
                    1
            ));
        }

        if (!"All".equals(status)) {
            filters.add(RowFilter.regexFilter(
                    "^" + status + "$",
                    5
            ));
        }

        sorter.setRowFilter(
                filters.isEmpty()
                        ? null
                        : RowFilter.andFilter(filters)
        );
    }

    // MY PRODUCTIVITY
    private void showProductivity() {
        refreshTasks();

        int total = Integer.parseInt(totalValue.getText());
        int pending = Integer.parseInt(pendingValue.getText());
        int completed = Integer.parseInt(completedValue.getText());
        int overdue = Integer.parseInt(overdueValue.getText());

        int percentage = total == 0
                ? 0
                : completed * 100 / total;

        JPanel report = new JPanel(new GridLayout(0, 1, 8, 8));
        report.setBackground(PANEL);
        report.setBorder(
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        );

        JLabel heading = new JLabel("YOUR PRODUCTIVITY OVERVIEW");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(BLUE);

        report.add(heading);
        report.add(reportLabel("Total tasks: " + total));
        report.add(reportLabel("Completed: " + completed));
        report.add(reportLabel("Pending: " + pending));
        report.add(reportLabel("Overdue: " + overdue));
        report.add(reportLabel("Completion rate: " + percentage + "%"));

        JProgressBar reportProgress = new JProgressBar(0, 100);
        reportProgress.setValue(percentage);
        reportProgress.setStringPainted(true);
        reportProgress.setString(percentage + "% completed");
        reportProgress.setForeground(GREEN);
        reportProgress.setBackground(PANEL2);
        reportProgress.setPreferredSize(new Dimension(300, 24));

        report.add(reportProgress);

        JOptionPane.showMessageDialog(
                this,
                report,
                "TaskFlow - My Productivity",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private JLabel reportLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT);
        return label;
    }
}
