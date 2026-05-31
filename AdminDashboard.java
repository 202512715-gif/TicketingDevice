package ticketingdevice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    // --- DYNAMIC SCALING ENGINE ---
    public static double CURRENT_SCALE = 0.33; // Starts at 0.33 for the login window

    // Exact Theme Colors
    private static final Color BG_PLATE = Color.decode("#FFCDC7"); // Light pink backdrop
    private static final Color SIDEBAR_BG = Color.decode("#8F3E3D"); // Maroon sidebar
    private static final Color TAB_ACTIVE = Color.decode("#FF7A6A"); // Soft orange-red active tab
    private static final Color WHITE_BOX = Color.WHITE;
    private static final Color TEXT_DARK = Color.decode("#2B434A"); 
    private static final Color TEXT_RED = Color.decode("#FF6969");  
    private static final Color FIELD_BG = Color.decode("#FFCCCC");  

    // Active Card Layout for Navigation
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel clockLabel;

    // --- STATIC GLOBAL DATA ENGINE ---
    public static class Shift {
        public String driver;
        public String route;
        public String busCode;
        public String time;
        public int day;
        public int month; // 1 - 12
        public int year;
        
        public Shift(String driver, String route, String busCode, String time, int day, int month, int year) {
            this.driver = driver;
            this.route = route;
            this.busCode = busCode;
            this.time = time;
            this.day = day;
            this.month = month;
            this.year = year;
        }
    }

    public static List<Shift> bookedShifts = new ArrayList<>();
    static {
        // Initial Mock Shifts using the name "Christian Mongkel"
        bookedShifts.add(new Shift("Christian Mongkel", "Olongapo → Subic", "VL-882", "03:20 AM", 12, 5, 2026));
        bookedShifts.add(new Shift("Ariston Valdivia", "Subic → Castillejos", "VL-112", "08:00 AM", 12, 5, 2026));
        bookedShifts.add(new Shift("Christian Mongkel", "Olongapo → Subic", "VL-882", "03:20 AM", 20, 5, 2026));
        bookedShifts.add(new Shift("Mateo Reyes", "Subic → Castillejos", "VL-302", "10:30 AM", 25, 5, 2026));
        bookedShifts.add(new Shift("Dhel Rikk", "Castillejos → Olongapo", "VL-904", "05:30 PM", 26, 5, 2026));
    }

    public AdminDashboard() {
        // Switch to the larger 0.65 scale when the dashboard opens
        CURRENT_SCALE = 0.65; 

        setTitle("Victory Liner - Admin Dashboard System");
        setSize(scale(1550), scale(950));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar Navigation & Card Panel container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BG_PLATE);

        // Left Sidebar
        JPanel sidebar = buildSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);

        // Right Content Area (CardLayout)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(scale(30), scale(40), scale(30), scale(40)));

        cardPanel.add(buildDriverPanel(), "Driver");
        cardPanel.add(buildConductorPanel(), "Conductor");
        cardPanel.add(new TripsPanel(), "Trips");
        cardPanel.add(buildTicketPanel(), "Ticket");

        // Top Header Wrapper (Houses Tab Title & Live Clock)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.add(buildHeaderBar(), BorderLayout.NORTH);
        rightContainer.add(cardPanel, BorderLayout.CENTER);

        mainContainer.add(rightContainer, BorderLayout.CENTER);
        setContentPane(mainContainer);

        startClock();
    }

    // =====================================================================
    // 1. LIVE HEADER BAR (CLOCK & TIME ENGINE)
    // =====================================================================
    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(scale(30), scale(40), 0, scale(40)));

        // Live Clock container matching design exactly
        JPanel clockPanel = new RoundedPanel(WHITE_BOX, scale(40));
        clockPanel.setPreferredSize(new Dimension(scale(380), scale(60)));
        clockPanel.setLayout(new BorderLayout());

        clockLabel = new JLabel("", SwingConstants.CENTER);
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, scale(22)));
        clockLabel.setForeground(TEXT_DARK);
        clockPanel.add(clockLabel, BorderLayout.CENTER);

        header.add(clockPanel, BorderLayout.EAST);
        return header;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  •  hh:mm:ss a");
            clockLabel.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    // =====================================================================
    // 2. SIDEBAR NAVIGATION
    // =====================================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(scale(320), scale(950)));

        // Title Header
        JLabel title = new JLabel("<html>BUS TICKETING<br>DEVICE</html>");
        title.setFont(new Font("SansSerif", Font.BOLD, scale(32)));
        title.setForeground(Color.WHITE);
        title.setBounds(scale(35), scale(40), scale(260), scale(100));
        sidebar.add(title);

        // Thin decorative line
        JPanel line = new RoundedPanel(new Color(255, 255, 255, 50), scale(4));
        line.setBounds(scale(35), scale(150), scale(250), scale(4));
        sidebar.add(line);

        // Navigation Tabs (Driver, Conductor, Trips, Ticket)
        String[] tabs = {"Driver", "Conductor", "Trips", "Ticket"};
        JButton[] buttons = new JButton[tabs.length];
        int yOffset = scale(190);

        for (int i = 0; i < tabs.length; i++) {
            final String tabName = tabs[i];
            buttons[i] = new RoundedButton(tabName, scale(40), SIDEBAR_BG, Color.WHITE);
            buttons[i].setFont(new Font("SansSerif", Font.BOLD, scale(26)));
            buttons[i].setBounds(scale(20), yOffset, scale(280), scale(70));
            buttons[i].setHorizontalAlignment(SwingConstants.LEFT);
            buttons[i].setBorder(new EmptyBorder(0, scale(30), 0, 0));

            buttons[i].addActionListener(e -> {
                cardLayout.show(cardPanel, tabName);
                for (JButton btn : buttons) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setForeground(Color.WHITE);
                }
                ((JButton) e.getSource()).setBackground(TAB_ACTIVE);
                ((JButton) e.getSource()).setForeground(Color.WHITE);
            });

            sidebar.add(buttons[i]);
            yOffset += scale(100);
        }

        // Set the default tab active
        buttons[0].setBackground(TAB_ACTIVE);

        // Log Out Button
        JButton logoutBtn = new RoundedButton("Log Out", scale(35), Color.decode("#2E434A"), Color.WHITE);
        logoutBtn.setBounds(scale(20), scale(800), scale(280), scale(80));
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, scale(28)));
        logoutBtn.addActionListener(e -> {
            new AdminLogin().setVisible(true);
            dispose();
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    // =====================================================================
    // 3. DRIVER VIEW PANEL WITH BUS CODE COLUMN (TAB 1)
    // =====================================================================
    private JPanel buildDriverPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Top Section
        JLabel title = createLabel("Top Driver", TEXT_DARK, 45, true);
        title.setBounds(0, 0, scale(400), scale(50));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title);

        JLabel ruleText = createLabel("RULE: Mark Inactive if out of service for ≥ 24 hours. Ordered by Status.", Color.decode("#A53B3B"), 18, true);
        ruleText.setBounds(0, scale(55), scale(800), scale(30));
        ruleText.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(ruleText);

        JButton addBtn = new RoundedButton("+ Add Driver", scale(30), Color.decode("#A34C4B"), Color.WHITE);
        addBtn.setBounds(scale(920), scale(15), scale(210), scale(55));
        addBtn.setFont(new Font("SansSerif", Font.BOLD, scale(20)));
        panel.add(addBtn);

        // Table Container Box
        JPanel tableContainer = new RoundedPanel(WHITE_BOX, scale(50));
        tableContainer.setBounds(0, scale(110), scale(1130), scale(620));
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(scale(30), scale(30), scale(30), scale(30)));

        String[] cols = {"ID", "Name", "Bus Code", "IN", "OUT", "Status"};
        Object[][] data = {
            {"202513847", "Christian Mongkel", "VL-882", "03:20 AM", "--", "ACTIVE"},
            {"202521191", "Dhel Rikk", "VL-904", "--", "--", "INACTIVE (> 24 HRS)"},
            {"202588120", "Mateo Reyes", "VL-302", "--", "--", "INACTIVE (> 24 HRS)"},
            {"202511450", "Ariston Valdivia", "VL-112", "08:00 AM", "05:00 PM", "COMPLETED"}
        };

        JTable table = createStyledTable(data, cols);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE_BOX);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableContainer);
        return panel;
    }

    // =====================================================================
    // 4. CONDUCTOR VIEW PANEL WITH BUS CODE COLUMN (TAB 2)
    // =====================================================================
    private JPanel buildConductorPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel title = createLabel("Top Conductor", TEXT_DARK, 45, true);
        title.setBounds(0, 0, scale(400), scale(50));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title);

        JLabel ruleText = createLabel("RULE: Mark Inactive if out of service for ≥ 24 hours. Ordered by Status.", Color.decode("#A53B3B"), 18, true);
        ruleText.setBounds(0, scale(55), scale(800), scale(30));
        ruleText.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(ruleText);

        JButton addBtn = new RoundedButton("+ Add Conductor", scale(30), Color.decode("#A34C4B"), Color.WHITE);
        addBtn.setBounds(scale(900), scale(15), scale(230), scale(55));
        addBtn.setFont(new Font("SansSerif", Font.BOLD, scale(20)));
        panel.add(addBtn);

        JPanel tableContainer = new RoundedPanel(WHITE_BOX, scale(50));
        tableContainer.setBounds(0, scale(110), scale(1130), scale(620));
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(scale(30), scale(30), scale(30), scale(30)));

        String[] cols = {"ID", "Name", "Bus Code", "IN", "OUT", "Status"};
        Object[][] data = {
            {"202513847", "Christian Mongkel", "VL-882", "03:20 AM", "--", "ACTIVE"},
            {"202521191", "Ariston Valdivia", "VL-112", "--", "--", "INACTIVE (> 24 HRS)"},
            {"202534012", "Mateo Reyes", "VL-302", "--", "--", "INACTIVE (> 24 HRS)"},
            {"202521239", "Dhel Rikk", "VL-904", "08:00 AM", "05:00 PM", "COMPLETED"}
        };

        JTable table = createStyledTable(data, cols);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE_BOX);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableContainer);
        return panel;
    }

    // =====================================================================
    // 5. INTERACTIVE TRIPS CALENDAR PANEL (TAB 3)
    // =====================================================================
    class TripsPanel extends JPanel {
        private int currentMonth = 5; // May (1-12)
        private int currentYear = 2026;
        private int selectedDay = 13;
        
        private JPanel calGridContainer;
        private JLabel shiftsTitleLabel;
        private JPanel shiftsContainer;
        public JComboBox<String> monthCombo;
        public JComboBox<String> yearCombo;
        private JLabel noShiftLabel;

        public TripsPanel() {
            setLayout(null);
            setOpaque(false);
            buildUI();
        }
        
        private void buildUI() {
            // Title Header labels
            JLabel title = createLabel("Trip Schedules", TEXT_DARK, 45, true);
            title.setBounds(0, 0, scale(400), scale(50));
            title.setHorizontalAlignment(SwingConstants.LEFT);
            add(title);

            JLabel ruleText = createLabel("DBMS LEDGER CONSOLE  •  REGISTRY QUERY VIEW", Color.decode("#A53B3B"), 18, true);
            ruleText.setBounds(0, scale(55), scale(800), scale(30));
            ruleText.setHorizontalAlignment(SwingConstants.LEFT);
            add(ruleText);

            // Calendar Box Layout
            JPanel calBox = new RoundedPanel(WHITE_BOX, scale(50));
            calBox.setBounds(0, scale(110), scale(780), scale(620));
            calBox.setLayout(null);

            // Calendar vector icon
            JLabel calIcon = new JLabel("📅");
            calIcon.setFont(new Font("SansSerif", Font.PLAIN, scale(32)));
            calIcon.setBounds(scale(40), scale(30), scale(60), scale(55));
            calBox.add(calIcon);

            // Month Selector Dropdown
            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            monthCombo = new RoundedComboBox(months, Color.WHITE, scale(20));
            monthCombo.setSelectedIndex(4); // May
            monthCombo.setFont(new Font("SansSerif", Font.BOLD, scale(24)));
            monthCombo.setForeground(TEXT_DARK);
            monthCombo.setBounds(scale(100), scale(30), scale(240), scale(55));
            monthCombo.addActionListener(e -> {
                currentMonth = monthCombo.getSelectedIndex() + 1;
                refreshCalendar();
            });
            calBox.add(monthCombo);

            // Year Selector Dropdown
            String[] years = {"2025", "2026", "2027", "2028", "2029", "2030"};
            yearCombo = new RoundedComboBox(years, Color.WHITE, scale(20));
            yearCombo.setSelectedItem("2026");
            yearCombo.setFont(new Font("SansSerif", Font.BOLD, scale(24)));
            yearCombo.setForeground(TEXT_DARK);
            yearCombo.setBounds(scale(350), scale(30), scale(150), scale(55));
            yearCombo.addActionListener(e -> {
                currentYear = Integer.parseInt((String) yearCombo.getSelectedItem());
                refreshCalendar();
            });
            calBox.add(yearCombo);

            JButton bookBtn = new RoundedButton("+ Book Duty Shift", scale(30), Color.decode("#F1604F"), Color.WHITE);
            bookBtn.setBounds(scale(540), scale(30), scale(210), scale(55));
            bookBtn.setFont(new Font("SansSerif", Font.BOLD, scale(18)));
            bookBtn.addActionListener(e -> new BookShiftDialog(AdminDashboard.this, this).setVisible(true));
            calBox.add(bookBtn);

            // Days Header Row
            String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            for (int i = 0; i < 7; i++) {
                JLabel dayLbl = createLabel(days[i], Color.decode("#FF6D5D"), 18, true);
                dayLbl.setBounds(scale(40 + (i * 100)), scale(100), scale(90), scale(30));
                calBox.add(dayLbl);
            }

            // Grid Container Panel for calendar days
            calGridContainer = new JPanel(null);
            calGridContainer.setOpaque(false);
            calGridContainer.setBounds(scale(40), scale(140), scale(700), scale(460));
            calBox.add(calGridContainer);

            add(calBox);

            // Shifts Sidebar Container (Right)
            JPanel shiftsBox = new RoundedPanel(WHITE_BOX, scale(50));
            shiftsBox.setBounds(scale(810), scale(110), scale(320), scale(620));
            shiftsBox.setLayout(null);

            shiftsTitleLabel = createLabel("SHIFTS FOR MAY 13", TEXT_DARK, 24, true);
            shiftsTitleLabel.setBounds(scale(30), scale(30), scale(260), scale(40));
            shiftsTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
            shiftsBox.add(shiftsTitleLabel);

            JPanel sLine = new RoundedPanel(Color.decode("#FFCCCC"), scale(4));
            sLine.setBounds(scale(30), scale(85), scale(260), scale(4));
            shiftsBox.add(sLine);

            // Vertical list scroll pane for Shifts Card list
            shiftsContainer = new JPanel();
            shiftsContainer.setLayout(new BoxLayout(shiftsContainer, BoxLayout.Y_AXIS));
            shiftsContainer.setOpaque(false);
            shiftsContainer.setBorder(new EmptyBorder(scale(10), scale(10), scale(10), scale(10)));

            JScrollPane scrollPane = new JScrollPane(shiftsContainer);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            scrollPane.setBounds(scale(20), scale(100), scale(280), scale(390));
            shiftsBox.add(scrollPane);

            noShiftLabel = createLabel("<html><center>No assignments scheduled for<br>this day. Click \"Book Duty<br>Shift\" to assign a driver<br>timetable.</center></html>", Color.decode("#8A99AD"), 18, false);
            noShiftLabel.setBounds(0, scale(150), scale(280), scale(200));

            // Persisted registry card for Christian Mongkel
            JPanel footerCard = new RoundedPanel(Color.decode("#FFE7E6"), scale(15));
            footerCard.setBounds(scale(20), scale(510), scale(280), scale(90));
            footerCard.setLayout(new BorderLayout());
            footerCard.setBorder(new EmptyBorder(scale(8), scale(12), scale(8), scale(12)));
            
            JLabel footerText = new JLabel("<html><span style='color: #8D4343; font-size: 11px;'>👤 <b>Christian Mongkel</b> is registered to Zambales Lines. Duty logs are stored directly inside persistent MySQL registries.</span></html>");
            footerText.setFont(new Font("SansSerif", Font.PLAIN, scale(11)));
            footerCard.add(footerText, BorderLayout.CENTER);
            shiftsBox.add(footerCard);

            add(shiftsBox);

            // First run calendar load
            refreshCalendar();
        }

        // Re-renders calendar grids with active click listeners
        public void refreshCalendar() {
            calGridContainer.removeAll();

            java.time.LocalDate firstDay = java.time.LocalDate.of(currentYear, currentMonth, 1);
            int daysInMonth = firstDay.lengthOfMonth();
            int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0, ..., Saturday = 6

            int row = 0;
            int col = startDayOfWeek;

            for (int day = 1; day <= daysInMonth; day++) {
                final int dayVal = day;
                int xPos = scale(col * 100);
                int yPos = scale(row * 75);

                // Day Cell button panel
                JButton dayBtn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Check if day is currently selected
                        if (dayVal == selectedDay) {
                            g2.setColor(Color.decode("#FFE7E6"));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(20), scale(20));
                            g2.setColor(Color.decode("#FF6D5D"));
                            g2.setStroke(new BasicStroke(scale(2)));
                            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, scale(20), scale(20));
                        } else {
                            // Check if has shifts
                            boolean hasShifts = getShiftsForDate(dayVal, currentMonth, currentYear).size() > 0;
                            if (hasShifts) {
                                g2.setColor(Color.decode("#FFE7E6"));
                                g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(20), scale(20));
                            } else {
                                g2.setColor(Color.WHITE);
                                g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(20), scale(20));
                            }
                            g2.setColor(Color.decode("#F0F0F0"));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(20), scale(20));
                        }
                        
                        // Draw Day Number
                        g2.setColor(TEXT_DARK);
                        g2.setFont(new Font("SansSerif", Font.BOLD, scale(18)));
                        g2.drawString(String.valueOf(dayVal), scale(12), scale(25));

                        // Draw Shift Badge
                        List<Shift> shiftsThisDay = getShiftsForDate(dayVal, currentMonth, currentYear);
                        if (!shiftsThisDay.isEmpty()) {
                            g2.setColor(Color.decode("#A84B49"));
                            g2.setFont(new Font("SansSerif", Font.BOLD, scale(11)));
                            g2.drawString("• " + shiftsThisDay.size() + " shift", scale(12), scale(55));
                        }

                        g2.dispose();
                    }
                };

                dayBtn.setBounds(xPos, yPos, scale(90), scale(65));
                dayBtn.setContentAreaFilled(false);
                dayBtn.setBorderPainted(false);
                dayBtn.setFocusPainted(false);
                dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Click on days to instantly refresh the Shift panel
                dayBtn.addActionListener(e -> {
                    selectedDay = dayVal;
                    refreshCalendar(); // Redraw selection borders
                    refreshShiftsSidebar();
                });

                calGridContainer.add(dayBtn);

                col++;
                if (col > 6) {
                    col = 0;
                    row++;
                }
            }

            calGridContainer.revalidate();
            calGridContainer.repaint();

            refreshShiftsSidebar();
        }

        // Renders active assignments on the sidebar for the selected date
        public void refreshShiftsSidebar() {
            shiftsTitleLabel.setText("SHIFTS FOR " + getMonthName(currentMonth).toUpperCase() + " " + selectedDay);
            shiftsContainer.removeAll();

            List<Shift> dayShifts = getShiftsForDate(selectedDay, currentMonth, currentYear);
            if (dayShifts.isEmpty()) {
                shiftsContainer.setLayout(new BorderLayout());
                shiftsContainer.add(noShiftLabel, BorderLayout.CENTER);
            } else {
                shiftsContainer.setLayout(new BoxLayout(shiftsContainer, BoxLayout.Y_AXIS));
                for (Shift s : dayShifts) {
                    shiftsContainer.add(createShiftCard(s));
                    shiftsContainer.add(Box.createRigidArea(new Dimension(0, scale(15))));
                }
            }

            shiftsContainer.revalidate();
            shiftsContainer.repaint();
        }

        // --- UPDATED: Sized at scale(150) height and containing active "Cancel Shift" button ---
        private JPanel createShiftCard(Shift s) {
            JPanel card = new RoundedPanel(Color.decode("#FFE7E6"), scale(20));
            card.setLayout(new BorderLayout());
            card.setMaximumSize(new Dimension(scale(260), scale(150))); 
            card.setPreferredSize(new Dimension(scale(260), scale(150)));
            card.setBorder(new EmptyBorder(scale(10), scale(15), scale(10), scale(15)));

            JLabel driverLbl = new JLabel(s.driver);
            driverLbl.setFont(new Font("SansSerif", Font.BOLD, scale(18)));
            driverLbl.setForeground(TEXT_DARK);

            JLabel detailsLbl = new JLabel("<html><b>" + s.busCode + "</b> • " + s.time + "<br>" + s.route + "</html>");
            detailsLbl.setFont(new Font("SansSerif", Font.PLAIN, scale(14)));
            detailsLbl.setForeground(Color.decode("#8D4343"));

            // Reusable secondary action button for removal
            JButton cancelBtn = new RoundedButton("Cancel Shift", scale(15), Color.WHITE, Color.decode("#A54C4B"));
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, scale(12)));
            cancelBtn.setPreferredSize(new Dimension(scale(110), scale(30)));
            
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Are you sure you want to cancel the shift for " + s.driver + " on day " + s.day + "?", 
                    "Cancel Shift", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    bookedShifts.remove(s);
                    refreshCalendar();
                    refreshShiftsSidebar();
                }
            });

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);
            bottomPanel.add(detailsLbl, BorderLayout.CENTER);
            bottomPanel.add(cancelBtn, BorderLayout.SOUTH);

            card.add(driverLbl, BorderLayout.NORTH);
            card.add(bottomPanel, BorderLayout.CENTER);
            return card;
        }

        private List<Shift> getShiftsForDate(int day, int month, int year) {
            List<Shift> matches = new ArrayList<>();
            for (Shift s : bookedShifts) {
                if (s.day == day && s.month == month && s.year == year) {
                    matches.add(s);
                }
            }
            return matches;
        }

        public int getSelectedDay() { return selectedDay; }
        public int getCurrentMonth() { return currentMonth; }
        public int getCurrentYear() { return currentYear; }
        public void setSelectionDay(int d) { this.selectedDay = d; }
    }

    // =====================================================================
    // 6. TICKET RECENT DATABASE PANEL (TAB 4)
    // =====================================================================
    private JPanel buildTicketPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel title = createLabel("Ticket", TEXT_DARK, 45, true);
        title.setBounds(0, 0, scale(400), scale(50));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(title);

        JLabel ruleText = createLabel("DBMS LEDGER CONSOLE  •  REGISTRY QUERY VIEW", Color.decode("#A53B3B"), 18, true);
        ruleText.setBounds(0, scale(55), scale(800), scale(30));
        ruleText.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(ruleText);

        // Recent Ticket Frame Table
        JPanel tableContainer = new RoundedPanel(WHITE_BOX, scale(50));
        tableContainer.setBounds(0, scale(110), scale(1130), scale(620));
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(scale(30), scale(30), scale(30), scale(30)));

        String[] cols = {"TICKET ID", "TRIP DATE", "TIME", "SEAT", "LOCATION ROUTE", "FARE AMOUNT", "STATUS"};
        Object[][] data = {
            {"T-102001", "Jan 20, 2026", "3:20 AM", "#11", "Castillejos → Subic", "₱50.00", "PAID"},
            {"T-102002", "Jan 20, 2026", "3:25 AM", "#8", "Olongapo → Subic", "₱50.00", "PAID"},
            {"T-102003", "Jan 20, 2026", "3:30 AM", "#14", "Castillejos → Olongapo", "₱60.00", "PAID"},
            {"T-102004", "Jan 20, 2026", "3:40 AM", "#2", "Olongapo → Subic", "₱50.00", "PAID"}
        };

        JTable table = createStyledTable(data, cols);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE_BOX);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableContainer);
        return panel;
    }

    // =====================================================================
    // 7. CUSTOM DIALOG POPUP: SCHEDULE DRIVER DUTY SHIFT
    // =====================================================================
    class BookShiftDialog extends JDialog {
        public BookShiftDialog(JFrame parent, TripsPanel tripsPanel) {
            super(parent, "Schedule Driver Duty Shift", true);
            setSize(scale(700), scale(620));
            setLocationRelativeTo(parent);
            setUndecorated(true); // Complete custom styling wrapper

            // Main Background Panel
            JPanel panel = new RoundedPanel(WHITE_BOX, scale(50));
            panel.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), scale(2), true));
            panel.setLayout(null);

            JLabel title = createLabel("SCHEDULE DRIVER DUTY SHIFT", TEXT_DARK, 28, true);
            title.setBounds(scale(50), scale(40), scale(400), scale(40));
            title.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(title);

            // Exit Close Cross
            JButton closeBtn = new JButton("X");
            closeBtn.setFont(new Font("SansSerif", Font.BOLD, scale(20)));
            closeBtn.setForeground(Color.decode("#8A99AD"));
            closeBtn.setBounds(scale(610), scale(40), scale(40), scale(40));
            closeBtn.setContentAreaFilled(false); closeBtn.setBorderPainted(false); closeBtn.setFocusPainted(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.addActionListener(e -> dispose());
            panel.add(closeBtn);

            // 1. Driver Name Dropdown
            JLabel nameLbl = createLabel("Driver Name:", Color.decode("#5E72E4"), 18, true);
            nameLbl.setBounds(scale(50), scale(110), scale(200), scale(30));
            nameLbl.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(nameLbl);

            String[] drivers = {"Christian Mongkel", "Dhel Rikk", "Mateo Reyes", "Ariston Valdivia"};
            JComboBox<String> nameBox = new RoundedComboBox(drivers, FIELD_BG, scale(20));
            nameBox.setBounds(scale(50), scale(150), scale(600), scale(55));
            panel.add(nameBox);

            // 2. Bus Route Stop Dropdown
            JLabel routeLbl = createLabel("Bus Route Stop:", Color.decode("#5E72E4"), 18, true);
            routeLbl.setBounds(scale(50), scale(220), scale(200), scale(30));
            routeLbl.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(routeLbl);

            String[] routes = {"Olongapo → Subic", "Subic → Castillejos", "Castillejos → Olongapo"};
            JComboBox<String> routeBox = new RoundedComboBox(routes, FIELD_BG, scale(20));
            routeBox.setBounds(scale(50), scale(260), scale(600), scale(55));
            panel.add(routeBox);

            // 3. Day of Month Textfield
            JLabel dayLbl = createLabel("Day of Month:", Color.decode("#5E72E4"), 18, true);
            dayLbl.setBounds(scale(50), scale(330), scale(200), scale(30));
            dayLbl.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(dayLbl);

            // Pre-fills with the currently active calendar select day [1]
            JTextField dayTxt = new RoundedTextField(String.valueOf(tripsPanel.getSelectedDay()), FIELD_BG, scale(20));
            dayTxt.setBounds(scale(50), scale(370), scale(280), scale(55));
            panel.add(dayTxt);

            // 4. Bus Code Textfield
            JLabel codeLbl = createLabel("Bus Code:", Color.decode("#5E72E4"), 18, true);
            codeLbl.setBounds(scale(370), scale(330), scale(200), scale(30));
            codeLbl.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(codeLbl);

            JTextField codeTxt = new RoundedTextField("VL-882", FIELD_BG, scale(20));
            codeTxt.setBounds(scale(370), scale(370), scale(280), scale(55));
            panel.add(codeTxt);

            // 5. Departure Time Textfield
            JLabel depLbl = createLabel("Departure Time:", Color.decode("#5E72E4"), 18, true);
            depLbl.setBounds(scale(50), scale(440), scale(200), scale(30));
            depLbl.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(depLbl);

            JTextField depTxt = new RoundedTextField("08:00 AM", FIELD_BG, scale(20));
            depTxt.setBounds(scale(50), scale(480), scale(600), scale(55));
            panel.add(depTxt);

            // Action Buttons
            JButton cancelBtn = new RoundedButton("Cancel", scale(30), Color.decode("#ECF0F3"), TEXT_DARK);
            cancelBtn.setBounds(scale(50), scale(560), scale(280), scale(55));
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, scale(18)));
            cancelBtn.addActionListener(e -> dispose());
            panel.add(cancelBtn);

            JButton confirmBtn = new RoundedButton("Confirm Duty Log", scale(30), Color.decode("#A54C4B"), Color.WHITE);
            confirmBtn.setBounds(scale(370), scale(560), scale(280), scale(55));
            confirmBtn.setFont(new Font("SansSerif", Font.BOLD, scale(18)));
            
            confirmBtn.addActionListener(e -> {
                try {
                    int day = Integer.parseInt(dayTxt.getText().trim());
                    String driver = (String) nameBox.getSelectedItem();
                    String route = (String) routeBox.getSelectedItem();
                    String busCode = codeTxt.getText().trim();
                    String depTime = depTxt.getText().trim();

                    // Insert the new shift dynamically into the state tracker
                    bookedShifts.add(new Shift(driver, route, busCode, depTime, day, tripsPanel.getCurrentMonth(), tripsPanel.getCurrentYear()));
                    
                    JOptionPane.showMessageDialog(this, "Duty Shift successfully booked!");
                    
                    // Trigger interface-wide visual refresh
                    tripsPanel.setSelectionDay(day);
                    tripsPanel.monthCombo.setSelectedIndex(tripsPanel.getCurrentMonth() - 1);
                    tripsPanel.yearCombo.setSelectedItem(String.valueOf(tripsPanel.getCurrentYear()));
                    tripsPanel.refreshCalendar();
                    tripsPanel.refreshShiftsSidebar();
                    
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric day.", "Booking Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(confirmBtn);

            setContentPane(panel);
        }
    }

    // =====================================================================
    // 8. ADMIN LOGIN FORM CLASS (MATCHES TICKETING DEVICE STYLING)
    // =====================================================================
    public static class AdminLogin extends JFrame {
        private final Color INPUT_BG = new Color(255, 124, 124, 97); // #FF7C7C at 38% Opacity
        private final Color LINK_COLOR = Color.decode("#FF6767");
        private final Color TEXT_DARKPINK = Color.decode("#FF8B8B"); 

        public AdminLogin() {
            // Apply scale explicitly during launch [1]
            CURRENT_SCALE = 0.33; 

            setTitle("Victory Liner Admin Login");
            setSize(scale(1086), scale(2511));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(null); 
            mainPanel.setBackground(BG_PLATE);
            
            // VICTORY Text
            JLabel victoryLabel = new JLabel("VICTORY");
            victoryLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(220)));
            victoryLabel.setForeground(Color.WHITE);
            victoryLabel.setBounds(center(1150), scale(70), scale(1111), scale(243));
            victoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(victoryLabel);

            // Gradient Line
            JPanel gradientLine = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    float[] fractions = {0.0f, 0.5f, 1.0f};
                    Color[] colors = {Color.decode("#F01011"), Color.decode("#ED590F"), Color.decode("#EAEB0B")};
                    LinearGradientPaint paint = new LinearGradientPaint(0, 0, getWidth(), 0, fractions, colors);
                    g2.setPaint(paint);
                    int radius = scale(35);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                    g2.dispose();
                }
            };
            gradientLine.setBounds(center(984), scale(310), scale(984), scale(29));
            gradientLine.setOpaque(false);
            mainPanel.add(gradientLine);

            // Liner Text
            JLabel linerLabel = new JLabel("Liner");
            linerLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(96)));
            linerLabel.setForeground(Color.WHITE);
            linerLabel.setBounds(0, scale(345), scale(1086), scale(116));
            linerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(linerLabel);

            // White Account Login Plate 
            JPanel whitePlate = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    int radius = scale(93);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                    g2.dispose();
                }
            };
            whitePlate.setBounds(center(1012), scale(498), scale(1012), scale(1100));
            whitePlate.setOpaque(false);
            
            // LOG IN Text inside White Plate
            JLabel loginLabel = new JLabel("ADMIN LOGIN");
            loginLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(96)));
            loginLabel.setForeground(TEXT_DARKPINK); 
            loginLabel.setBounds(0, scale(20), scale(1012), scale(140)); 
            loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
            whitePlate.add(loginLabel);

            // ID Number Label
            whitePlate.add(createLabel("ID Number", 91, 170)); 

            // ID Number Input Field
            RoundedTextField idInput = new RoundedTextField("Enter your IDNumber", INPUT_BG, scale(93));
            idInput.setBounds(centerInPlate(955, 1012), scale(230), scale(955), scale(95));
            whitePlate.add(idInput);

            // Password Label
            whitePlate.add(createLabel("Password", 91, 380));

            // Password Input Field with wrapper for show/hide eye toggle button
            JPanel passWrapper = new JPanel(null);
            passWrapper.setOpaque(false);
            int pWrapW = scale(955);
            int pWrapH = scale(95);
            passWrapper.setBounds(centerInPlate(955, 1012), scale(440), pWrapW, pWrapH);

            RoundedPasswordField passInput = new RoundedPasswordField("Enter your password", INPUT_BG, scale(93));
            passInput.setBounds(0, 0, pWrapW, pWrapH);

            EyeToggleButton eyeBtn = new EyeToggleButton();
            int eyeSize = scale(65);
            eyeBtn.setBounds(pWrapW - eyeSize - scale(25), (pWrapH - eyeSize) / 2, eyeSize, eyeSize);
            
            eyeBtn.addActionListener(e -> {
                boolean newState = !eyeBtn.isShowPassword();
                eyeBtn.setShowPassword(newState);
                passInput.setPasswordVisible(newState);
            });

            passWrapper.add(eyeBtn);
            passWrapper.add(passInput);
            whitePlate.add(passWrapper);

            mainPanel.add(whitePlate);

            // Log In Button
            JButton loginBtn = new RoundedButton("LOGIN", scale(93), TEXT_DARKPINK, Color.WHITE);
            loginBtn.setBounds(center(890), scale(1619), scale(862), scale(150));
            loginBtn.setFont(new Font("SansSerif", Font.BOLD, scale(90)));
            loginBtn.addActionListener(e -> {
                String enteredID = idInput.getText().trim();
                String enteredPassword = new String(passInput.getPassword());

                // Credentials Verified
                if (enteredID.equals("202513847") && enteredPassword.equals("admin13847")) {
                    SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "wrong id number or password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            mainPanel.add(loginBtn);

            setContentPane(mainPanel);
        }

        private int center(double componentWidth) { return scale((1086 - componentWidth) / 2); }
        private int centerInPlate(double componentWidth, double plateWidth) { return scale((plateWidth - componentWidth) / 2); }
        private JLabel createLabel(String text, double x, double y) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(40)));
            lbl.setForeground(Color.BLACK);
            lbl.setBounds(scale(x), scale(y), scale(351), scale(50));
            return lbl;
        }
    }

    // =====================================================================
    // 9. REUSABLE CUSTOM RENDERING COMPONENTS
    // =====================================================================
    private static int scale(double value) { return (int) Math.round(value * CURRENT_SCALE); }

    private static JLabel createLabel(String text, Color color, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, scale(size)));
        l.setForeground(color);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    // Converts Month index integers to standard uppercase Strings
    public static String getMonthName(int m) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        if (m >= 1 && m <= 12) return months[m - 1];
        return "Unknown";
    }

    private static JTable createStyledTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(scale(85));
        table.setGridColor(Color.decode("#F0F0F0"));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFont(new Font("SansSerif", Font.BOLD, scale(20)));
        table.setSelectionBackground(Color.decode("#FFE7E6"));

        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(100, scale(70)));
        header.setBackground(Color.decode("#ECEFF1"));
        header.setFont(new Font("SansSerif", Font.BOLD, scale(20)));
        header.setReorderingAllowed(false);

        // Render cell alignments and status tags [2]
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel cellLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cellLabel.setForeground(TEXT_DARK);
                
                String valStr = (value == null) ? "" : value.toString();

                // Status Badge Renderers matching exactly with status configurations
                if (valStr.equals("ACTIVE") || valStr.equals("PAID")) {
                    cellLabel.setText("<html><div style='background-color: #FFE6E5; color: #FF6969; padding: 4px 15px; border-radius: 8px;'>" + valStr + "</div></html>");
                } else if (valStr.equals("INACTIVE (> 24 HRS)")) {
                    cellLabel.setText("<html><div style='background-color: #F1F3F5; color: #6E7C8C; padding: 4px 15px; border-radius: 8px;'>" + valStr + "</div></html>");
                } else if (valStr.equals("COMPLETED")) {
                    cellLabel.setText("<html><div style='background-color: #EBEFFF; color: #5E72E4; padding: 4px 15px; border-radius: 8px;'>" + valStr + "</div></html>");
                }
                
                return cellLabel;
            }
        });

        return table;
    }

    // Custom Components
    static class RoundedPanel extends JPanel {
        private Color bgColor; private int radius;
        public RoundedPanel(Color bgColor, int radius) { this.bgColor = bgColor; this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private int radius;
        public RoundedButton(String text, int radius, Color bg, Color fg) {
            super(text); this.radius = radius; setBackground(bg); setForeground(fg);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0,0,0,0)); 
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g); g2.dispose();
        }
    }

    static class RoundedTextField extends JTextField {
        private Color bgColor; private int radius; private String placeholder;
        public RoundedTextField(String placeholder, Color bgColor, int radius) {
            this.placeholder = placeholder; this.bgColor = bgColor; this.radius = radius;
            setOpaque(false); 
            setBorder(BorderFactory.createEmptyBorder(0, scale(40), 0, scale(40))); 
            setFont(new Font("SansSerif", Font.BOLD, scale(20)));
            setForeground(Color.decode("#FF8B8B")); setText(placeholder);
            addFocusListener(new FocusListener() {
                @Override public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) { setText(""); setForeground(Color.DARK_GRAY); }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) { setForeground(Color.decode("#FF8B8B")); setText(placeholder); }
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2); g2.dispose();
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        private Color bgColor; private int radius; private String placeholder;
        private boolean isPlaceholderActive = true; private char defaultEchoChar; private boolean passwordVisible = false;

        public RoundedPasswordField(String placeholder, Color bgColor, int radius) {
            this.placeholder = placeholder; this.bgColor = bgColor; this.radius = radius;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, scale(40), 0, scale(110)));
            setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(35)));
            setForeground(Color.decode("#FF8B8B"));
            this.defaultEchoChar = getEchoChar();
            setEchoChar((char) 0);
            setText(placeholder);

            addFocusListener(new FocusListener() {
                @Override public void focusGained(FocusEvent e) {
                    if (isPlaceholderActive) {
                        setText(""); setEchoChar(passwordVisible ? (char) 0 : defaultEchoChar);
                        setForeground(Color.DARK_GRAY); isPlaceholderActive = false;
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setEchoChar((char) 0); setForeground(Color.decode("#FF8B8B"));
                        setText(placeholder); isPlaceholderActive = true;
                    }
                }
            });
        }
        public void setPasswordVisible(boolean visible) {
            this.passwordVisible = visible;
            if (!isPlaceholderActive) setEchoChar(visible ? (char) 0 : defaultEchoChar);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2); g2.dispose();
        }
    }

    static class EyeToggleButton extends JButton {
        private boolean showPassword = false;
        public EyeToggleButton() {
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        public boolean isShowPassword() { return showPassword; }
        public void setShowPassword(boolean b) { this.showPassword = b; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - scale(15);
            int x = (w - size) / 2, y = (h - size) / 2;
            g2.setColor(Color.decode("#FF8B8B"));
            g2.setStroke(new BasicStroke(scale(4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int eyeW = size, eyeH = (int)(size * 0.6);
            int eyeX = x, eyeY = y + (size - eyeH) / 2;

            Path2D eyePath = new Path2D.Double();
            eyePath.moveTo(eyeX, eyeY + eyeH / 2.0);
            eyePath.quadTo(eyeX + eyeW / 2.0, eyeY - scale(3), eyeX + eyeW, eyeY + eyeH / 2.0);
            eyePath.quadTo(eyeX + eyeW / 2.0, eyeY + eyeH + scale(3), eyeX, eyeY + eyeH / 2.0);
            eyePath.closePath();
            g2.draw(eyePath);

            int pupilSize = (int)(eyeH * 0.5);
            g2.fillOval(eyeX + (eyeW - pupilSize) / 2, eyeY + (eyeH - pupilSize) / 2, pupilSize, pupilSize);

            if (!showPassword) {
                g2.setStroke(new BasicStroke(scale(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(eyeX + scale(5), eyeY + scale(5), eyeX + eyeW - scale(5), eyeY + eyeH - scale(5));
            }
            g2.dispose();
        }
    }

    static class RoundedComboBox extends JComboBox<String> {
        private Color bgColor; private int radius;
        public RoundedComboBox(String[] items, Color bgColor, int radius) {
            super(items); this.bgColor = bgColor; this.radius = radius;
            setOpaque(false);
            setFont(new Font("SansSerif", Font.BOLD, scale(20)));
            setForeground(Color.decode("#A54C4B"));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            setUI(new BasicComboBoxUI() {
                @Override protected JButton createArrowButton() {
                    JButton arrowBtn = new JButton("⌄ ");
                    arrowBtn.setFont(new Font("SansSerif", Font.BOLD, scale(24)));
                    arrowBtn.setForeground(Color.decode("#A54C4B"));
                    arrowBtn.setContentAreaFilled(false); arrowBtn.setBorderPainted(false); arrowBtn.setFocusPainted(false);
                    return arrowBtn;
                }
                @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {}
                @Override protected ComboPopup createPopup() {
                    return new BasicComboPopup(comboBox) {
                        @Override protected void configurePopup() {
                            super.configurePopup();
                            setOpaque(true); setBackground(Color.WHITE);
                            setBorder(new javax.swing.border.LineBorder(Color.decode("#CCCCCC"), scale(2), true));
                        }
                    };
                }
            });

            setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object val, int index, boolean isSel, boolean chf) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, val, index, isSel, chf);
                    label.setBorder(BorderFactory.createEmptyBorder(scale(10), scale(20), scale(10), scale(20)));
                    label.setForeground(Color.BLACK);
                    label.setBackground(isSel ? Color.decode("#FFF0EE") : Color.WHITE);
                    return label;
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2); g2.dispose();
        }
    }

    // System Main Runner
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        // Instantiates Login Frame out of the box
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}

