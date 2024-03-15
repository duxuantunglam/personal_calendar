import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PersonalCalendarApp {
    private JFrame frame;
    private JTable calendarTable;
    private JLabel monthYearLabel;
    private JButton prevButton;
    private JButton nextButton;
    private LocalDate currentDate;
    private EventManager eventManager;
    private JTextArea eventTextArea;

    public PersonalCalendarApp() {
        currentDate = LocalDate.now();
        eventManager = new EventManager();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Personal Calendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 425);
        frame.setLayout(new BorderLayout());

        // Calendar Panel
        JPanel calendarPanel = new JPanel(new GridBagLayout());

        JPanel monthYearPanel = new JPanel(new BorderLayout());
        monthYearLabel = new JLabel();
        updateMonthYearLabel();

        prevButton = new JButton("<");
        nextButton = new JButton(">");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = currentDate.minusMonths(1);
                updateCalendar();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = currentDate.plusMonths(1);
                updateCalendar();
            }
        });

        monthYearPanel.add(prevButton, BorderLayout.WEST);
        monthYearPanel.add(monthYearLabel, BorderLayout.CENTER);
        monthYearPanel.add(nextButton, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        calendarPanel.add(monthYearPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        calendarTable = new JTable();
        calendarTable.setRowHeight(50);
        calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());
        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = calendarTable.rowAtPoint(evt.getPoint());
                int col = calendarTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    Object cellValue = calendarTable.getValueAt(row, col);
                    if (cellValue != null && !cellValue.toString().isEmpty()) {
                        int day = (int) cellValue;
                        LocalDate selectedDate = currentDate.withDayOfMonth(day);
                        List<Event> eventsInDay = eventManager.getEventsInDay(selectedDate);
                        if (!eventsInDay.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (Event event : eventsInDay) {
                                sb.append(event.toString()).append("\n");
                                sb.append("--------------------\n");
                            }
                            eventTextArea.setText(sb.toString());
                        } else {
                            eventTextArea.setText("No events found.");
                        }
                    }
                }
            }
        });

        JScrollPane calendarScrollPane = new JScrollPane(calendarTable);

        calendarPanel.add(calendarScrollPane, gbc);

        // Today Panel
        JPanel todayPanel = new JPanel(new BorderLayout());
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = LocalDate.now();
                updateCalendar();
            }
        });
        todayPanel.add(todayButton, BorderLayout.CENTER);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(todayPanel, BorderLayout.SOUTH);

        // Event Panel
        JPanel eventPanel = new JPanel(new BorderLayout());
        eventTextArea = new JTextArea(10, 30);
        eventTextArea.setEditable(false);
        JScrollPane eventScrollPane = new JScrollPane(eventTextArea);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton addEventButton = new JButton("Add Event");
        addEventButton.setPreferredSize(new Dimension(200, 100));
        addEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddEventDialog();
            }
        });
        buttonPanel.add(addEventButton);

        JButton searchEventButton = new JButton("Search Event");
        searchEventButton.setPreferredSize(new Dimension(200, 100));
        searchEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearchEventDialog();
            }
        });
        buttonPanel.add(searchEventButton);

        JButton editEventButton = new JButton("Edit Event");
        editEventButton.setPreferredSize(new Dimension(200, 100));
        editEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditEventDialog();
            }
        });
        buttonPanel.add(editEventButton);

        JButton deleteEventButton = new JButton("Delete Event");
        deleteEventButton.setPreferredSize(new Dimension(200, 100));
        deleteEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDeleteEventDialog();
            }
        });
        buttonPanel.add(deleteEventButton);

        eventPanel.add(buttonPanel, BorderLayout.NORTH);
        eventPanel.add(eventScrollPane, BorderLayout.CENTER);

        // Add panels to the frame
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(eventPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void updateMonthYearLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        String monthYearText = currentDate.format(formatter);

        Font labelFont = monthYearLabel.getFont();
        Font newFont = labelFont.deriveFont(Font.BOLD, 24);
        monthYearLabel.setFont(newFont);

        monthYearLabel.setText(monthYearText);
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void updateCalendar() {
        YearMonth yearMonth = YearMonth.from(currentDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();

        String[] columnNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Object[][] data = new Object[6][7];

        int day = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 0 && col < firstDayOfWeek) {
                    data[row][col] = "";
                } else if (day > daysInMonth) {
                    data[row][col] = "";
                } else {
                    data[row][col] = day;
                    day++;
                }
            }
        }

        calendarTable.setModel(new DefaultTableModel(data, columnNames));
        calendarTable.getTableHeader().setReorderingAllowed(false);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setShowGrid(true);
        calendarTable.setGridColor(Color.BLACK);

        updateMonthYearLabel();
    }

    private void openAddEventDialog() {
        JFrame addEventFrame = new JFrame("Add Event");
        addEventFrame.setSize(400, 300);
        addEventFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleTextField = new JTextField();
        inputPanel.add(titleLabel);
        inputPanel.add(titleTextField);

        JLabel dateLabel = new JLabel("Date (dd-MM-yyyy):");
        JTextField dateTextField = new JTextField();
        inputPanel.add(dateLabel);
        inputPanel.add(dateTextField);

        JLabel timeLabel = new JLabel("Time (HH:mm):");
        JTextField timeTextField = new JTextField();
        inputPanel.add(timeLabel);
        inputPanel.add(timeTextField);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionTextField = new JTextField();
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionTextField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleTextField.getText();
                String dateString = dateTextField.getText();
                String timeString = timeTextField.getText();
                String description = descriptionTextField.getText();

                if (title.isEmpty() || dateString.isEmpty() || timeString.isEmpty()) {
                    JOptionPane.showMessageDialog(addEventFrame, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = LocalDate.parse(dateString, dateFormatter);

                    try {
                        LocalTime time = LocalTime.parse(timeString);
                        LocalDateTime dateTime = LocalDateTime.of(date, time);
                        Event event = new Event(dateTime, title, description);

                        if (!event.isValidDate()) {
                            JOptionPane.showMessageDialog(addEventFrame, "Invalid date or time.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (!event.isValidTime()) {
                            JOptionPane.showMessageDialog(addEventFrame, "Invalid date or time.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        eventManager.addEvent(event);
                        JOptionPane.showMessageDialog(addEventFrame, "Event added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        addEventFrame.dispose();
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(addEventFrame, "Invalid time or time format.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(addEventFrame, "Invalid date or time format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addEventFrame.add(inputPanel, BorderLayout.CENTER);
        addEventFrame.add(addButton, BorderLayout.SOUTH);

        addEventFrame.setVisible(true);
    }

    private void openSearchEventDialog() {
        JFrame searchEventFrame = new JFrame("Search Event");
        searchEventFrame.setSize(400, 300);
        searchEventFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel keywordLabel = new JLabel("Please type Title or Description:");
        JTextField keywordTextField = new JTextField(15);
        inputPanel.add(keywordLabel);
        inputPanel.add(keywordTextField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = keywordTextField.getText();
                List<Event> searchResults = eventManager.searchEvents(keyword);
                if (searchResults.isEmpty()) {
                    eventTextArea.setText("No events found.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Event event : searchResults) {
                        sb.append(event.toString()).append("\n");
                        sb.append("--------------------\n");
                    }
                    eventTextArea.setText(sb.toString());
                }
            }
        });

        searchEventFrame.add(inputPanel, BorderLayout.NORTH);

        eventTextArea = new JTextArea(10, 30);
        eventTextArea.setEditable(false);
        JScrollPane eventScrollPane = new JScrollPane(eventTextArea);

        searchEventFrame.add(eventScrollPane, BorderLayout.CENTER);
        searchEventFrame.add(searchButton, BorderLayout.SOUTH);

        searchEventFrame.setVisible(true);
    }

    private void openEditEventDialog() {
        JFrame editEventFrame = new JFrame("Edit Event");
        editEventFrame.setSize(400, 300);
        editEventFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleTextField = new JTextField();
        inputPanel.add(titleLabel);
        inputPanel.add(titleTextField);

        JLabel dateLabel = new JLabel("Date (dd-MM-yyyy):");
        JTextField dateTextField = new JTextField();
        inputPanel.add(dateLabel);
        inputPanel.add(dateTextField);

        JLabel timeLabel = new JLabel("Time (HH:mm):");
        JTextField timeTextField = new JTextField();
        inputPanel.add(timeLabel);
        inputPanel.add(timeTextField);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionTextField = new JTextField();
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionTextField);

        // Fill input fields with event data
        int selectedRow = calendarTable.getSelectedRow();
        int selectedColumn = calendarTable.getSelectedColumn();
        if (selectedRow >= 0 && selectedColumn >= 0) {
            Object cellValue = calendarTable.getValueAt(selectedRow, selectedColumn);
            if (cellValue != null && !cellValue.toString().isEmpty()) {
                int day = (int) cellValue;
                LocalDate selectedDate = currentDate.withDayOfMonth(day);
                List<Event> eventsInDay = eventManager.getEventsInDay(selectedDate);
                if (!eventsInDay.isEmpty()) {
                    Event event = eventsInDay.get(0); // Assuming there's only one event in the selected day
                    titleTextField.setText(event.getTitle());
                    dateTextField.setText(event.getDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    timeTextField.setText(event.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    descriptionTextField.setText(event.getDescription());
                }
            }
        }

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleTextField.getText();
                String dateString = dateTextField.getText();
                String timeString = timeTextField.getText();
                String description = descriptionTextField.getText();

                if (title.isEmpty() || dateString.isEmpty() || timeString.isEmpty()) {
                    JOptionPane.showMessageDialog(editEventFrame, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Parse date and time
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = LocalDate.parse(dateString, dateFormatter);
                    String[] timeParts = timeString.split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

                    Event event = eventManager.findEventByTitle(title);
                    if (event != null) {
                        eventManager.updateEvent(event, dateTime, title, description);
                        JOptionPane.showMessageDialog(editEventFrame, "Event updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        editEventFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editEventFrame, "Event not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editEventFrame, "Invalid date or time format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editEventFrame.add(inputPanel, BorderLayout.CENTER);
        editEventFrame.add(updateButton, BorderLayout.SOUTH);

        editEventFrame.setVisible(true);
    }

    private void openDeleteEventDialog() {
        JFrame deleteEventFrame = new JFrame("Delete Event");
        deleteEventFrame.setSize(400, 300);
        deleteEventFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleTextField = new JTextField();
        inputPanel.add(titleLabel);
        inputPanel.add(titleTextField);

        JLabel dateLabel = new JLabel("Date (dd-MM-yyyy):");
        JTextField dateTextField = new JTextField();
        inputPanel.add(dateLabel);
        inputPanel.add(dateTextField);

        JLabel timeLabel = new JLabel("Time (HH:mm):");
        JTextField timeTextField = new JTextField();
        inputPanel.add(timeLabel);
        inputPanel.add(timeTextField);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionTextField = new JTextField();
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionTextField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleTextField.getText();
                String dateString = dateTextField.getText();
                String timeString = timeTextField.getText();
                String description = descriptionTextField.getText();

                if (title.isEmpty() || dateString.isEmpty() || timeString.isEmpty()) {
                    JOptionPane.showMessageDialog(deleteEventFrame, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Thay đổi định dạng ngày tháng
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate date = LocalDate.parse(dateString, dateFormatter);
                    String[] timeParts = timeString.split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    LocalDateTime dateTime = date.atTime(hour, minute);
                    Event event = eventManager.findEventByDateAndTitle(dateTime.toLocalDate(), title);
                    if (event != null) {
                        eventManager.removeEvent(event);
                        JOptionPane.showMessageDialog(deleteEventFrame, "Event deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        deleteEventFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(deleteEventFrame, "Event not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(deleteEventFrame, "Invalid date or time format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteEventFrame.add(inputPanel, BorderLayout.CENTER);
        deleteEventFrame.add(deleteButton, BorderLayout.SOUTH);

        deleteEventFrame.setVisible(true);
    }

    private void displayEventsInDay(LocalDate date) {
        List<Event> eventsInDay = eventManager.getEventsInDay(date);
        StringBuilder sb = new StringBuilder();
        sb.append("Events on ").append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(":\n\n");
        if (eventsInDay.isEmpty()) {
            sb.append("No events found.");
        } else {
            for (Event event : eventsInDay) {
                sb.append(event.toString()).append("\n");
                sb.append("--------------------\n");
            }
        }
        eventTextArea.setText(sb.toString());
    }

    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel cellLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cellLabel.setVerticalAlignment(SwingConstants.CENTER);

            if (value == null || value.equals("")) {
                cellLabel.setText("");
                cellLabel.setBackground(table.getBackground());
            } else {
                int day = (int) value;
                cellLabel.setText(String.valueOf(day));
                cellLabel.setBackground(table.getSelectionBackground());

                // Ngày hôm nay
                LocalDate today = LocalDate.now();
                if (currentDate.getMonth() == today.getMonth() && day == today.getDayOfMonth()) {
                    cellLabel.setForeground(Color.BLACK);
                    cellLabel.setOpaque(true); // Khoanh tròn
                    cellLabel.setBackground(Color.LIGHT_GRAY); // Màu nền
                } else {
                    cellLabel.setForeground(table.getForeground());
                    cellLabel.setOpaque(false);
                }
            }

            return cellLabel;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PersonalCalendarApp calendarApp = new PersonalCalendarApp();
                calendarApp.updateCalendar();
            }
        });
    }
}