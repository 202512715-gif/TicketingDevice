package ticketingdevice;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Path2D;

public class TicketingDeviceLogin extends JFrame {

    private static final double SCALE = 0.35;

    private final Color BG_COLOR       = Color.decode("#FFCDC7");
    private final Color WHITE_PLATE    = Color.WHITE;
    private final Color INPUT_BG       = new Color(255, 124, 124, 97);
    private final Color LINK_COLOR     = Color.decode("#FF6767");
    private final Color BTN_BG         = Color.decode("#FFF6F6");
    private final Color TEXT_DARKPINK  = Color.decode("#FF8B8B");

    public TicketingDeviceLogin() {
        setTitle("Victory Liner Ticketing Device");
        setSize(scale(1086), scale(2511));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_COLOR);

        // VICTORY label
        JLabel victoryLabel = new JLabel("VICTORY");
        victoryLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(220)));
        victoryLabel.setForeground(Color.WHITE);
        victoryLabel.setBounds(center(1150), scale(70), scale(1111), scale(243));
        victoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(victoryLabel);

        // Gradient line
        JPanel gradientLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] fractions = {0.0f, 0.5f, 1.0f};
                Color[] colors = {Color.decode("#F01011"), Color.decode("#ED590F"), Color.decode("#EAEB0B")};
                LinearGradientPaint paint = new LinearGradientPaint(0, 0, getWidth(), 0, fractions, colors);
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(35), scale(35));
                g2.dispose();
            }
        };
        gradientLine.setBounds(center(984), scale(310), scale(984), scale(29));
        gradientLine.setOpaque(false);
        mainPanel.add(gradientLine);

        // Liner label
        JLabel linerLabel = new JLabel("Liner");
        linerLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(96)));
        linerLabel.setForeground(Color.WHITE);
        linerLabel.setBounds(0, scale(345), scale(1086), scale(116));
        linerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(linerLabel);

        // White card plate
        JPanel whitePlate = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE_PLATE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(93), scale(93));
                g2.dispose();
            }
        };
        whitePlate.setBounds(center(1012), scale(498), scale(1012), scale(1100));
        whitePlate.setOpaque(false);

        // LOG IN title
        JLabel loginLabel = new JLabel("LOG IN");
        loginLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(96)));
        loginLabel.setForeground(TEXT_DARKPINK);
        loginLabel.setBounds(0, scale(20), scale(1012), scale(140));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        whitePlate.add(loginLabel);

        // ID Number label + field
        whitePlate.add(createFieldLabel("ID Number", 91, 170));
        RoundedTextField idInput = new RoundedTextField("Enter your ID Number", INPUT_BG, scale(93));
        idInput.setBounds(centerInPlate(955, 1012), scale(230), scale(955), scale(95));
        whitePlate.add(idInput);

        // Password label + field + eye toggle
        whitePlate.add(createFieldLabel("Password", 91, 380));

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

        // Forgot password link
        JLabel forgotPass = new JLabel("<html><u>Forgot password?</u></html>");
        forgotPass.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(35)));
        forgotPass.setForeground(LINK_COLOR);
        forgotPass.setBounds(scale(630), scale(550), scale(300), scale(48));
        whitePlate.add(forgotPass);

        // Error label (hidden by default)
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, scale(28)));
        errorLabel.setForeground(Color.decode("#CC0000"));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBounds(0, scale(640), scale(1012), scale(40));
        whitePlate.add(errorLabel);

        mainPanel.add(whitePlate);

        // LOGIN button
        JButton loginBtn = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_DARKPINK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), scale(93), scale(93));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        loginBtn.setBounds(center(890), scale(1619), scale(862), scale(150));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, scale(90)));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> {
            String enteredID  = idInput.getText().trim();
            String enteredPwd = new String(passInput.getPassword());

            // ─── CREDENTIALS ─────────────────────────────────────
            // ID: 202513847   Password: pass13847
            // ─────────────────────────────────────────────────────
            if (enteredID.equals("202513847") && enteredPwd.equals("pass13847")) {
                errorLabel.setText(" ");
                SwingUtilities.invokeLater(() ->
                    new TicketingDevice("", "", "").setVisible(true)
                );
                dispose(); // close login — only called once
            } else {
                errorLabel.setText("Wrong ID number or password.");
                idInput.setBorder(BorderFactory.createLineBorder(Color.decode("#CC0000"), scale(3), true));
                passInput.setBorder(BorderFactory.createLineBorder(Color.decode("#CC0000"), scale(3), true));
            }
        });

        // Allow Enter key on password field
        passInput.addActionListener(e -> loginBtn.doClick());

        mainPanel.add(loginBtn);

        // Create Account button
        JButton createAccBtn = new JButton("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                int radius = scale(93);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(LINK_COLOR);
                g2.setStroke(new BasicStroke(scale(6)));
                g2.drawRoundRect(scale(3), scale(3), getWidth() - scale(6), getHeight() - scale(6), radius, radius);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        createAccBtn.setBounds(center(995), scale(1800), scale(955), scale(150));
        createAccBtn.setFont(new Font("SansSerif", Font.BOLD, scale(75)));
        createAccBtn.setForeground(LINK_COLOR);
        createAccBtn.setContentAreaFilled(false);
        createAccBtn.setBorderPainted(false);
        createAccBtn.setFocusPainted(false);
        createAccBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(createAccBtn);

        setContentPane(mainPanel);
    }

    // ── HELPERS ──

    private int scale(double v)                          { return (int) Math.round(v * SCALE); }
    private int center(double w)                         { return scale((1086 - w) / 2); }
    private int centerInPlate(double w, double plate)    { return scale((plate - w) / 2); }

    private JLabel createFieldLabel(String text, double x, double y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(40)));
        l.setForeground(Color.BLACK);
        l.setBounds(scale(x), scale(y), scale(351), scale(50));
        return l;
    }

    // ── INNER COMPONENTS ──

    class RoundedTextField extends JTextField {
        private final Color bg; private final int r; private final String ph;
        public RoundedTextField(String ph, Color bg, int r) {
            this.ph = ph; this.bg = bg; this.r = r;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, scale(40), 0, scale(40)));
            setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(35)));
            setForeground(TEXT_DARKPINK); setText(ph);
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) { if (getText().equals(ph)) { setText(""); setForeground(Color.DARK_GRAY); } }
                public void focusLost(FocusEvent e)   { if (getText().isEmpty())  { setForeground(TEXT_DARKPINK); setText(ph); } }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g2); g2.dispose();
        }
    }

    class RoundedPasswordField extends JPasswordField {
        private final Color bg; private final int r; private final String ph;
        private boolean placeholderActive = true;
        private final char defaultEcho;
        private boolean visible = false;

        public RoundedPasswordField(String ph, Color bg, int r) {
            this.ph = ph; this.bg = bg; this.r = r;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, scale(40), 0, scale(110)));
            setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, scale(35)));
            setForeground(TEXT_DARKPINK);
            defaultEcho = getEchoChar();
            setEchoChar((char) 0);
            setText(ph);
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                    if (placeholderActive) { setText(""); setEchoChar(visible ? (char)0 : defaultEcho); setForeground(Color.DARK_GRAY); placeholderActive = false; }
                }
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) { setEchoChar((char)0); setForeground(TEXT_DARKPINK); setText(ph); placeholderActive = true; }
                }
            });
        }
        public void setPasswordVisible(boolean v) { this.visible = v; if (!placeholderActive) setEchoChar(v ? (char)0 : defaultEcho); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g2); g2.dispose();
        }
    }

    class EyeToggleButton extends JButton {
        private boolean show = false;
        public EyeToggleButton() { setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        public boolean isShowPassword()       { return show; }
        public void setShowPassword(boolean b){ show = b; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - scale(15);
            int x = (w - size) / 2, y = (h - size) / 2;
            g2.setColor(TEXT_DARKPINK);
            g2.setStroke(new BasicStroke(scale(4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int eW = size, eH = (int)(size * 0.6), eX = x, eY = y + (size - eH) / 2;
            Path2D path = new Path2D.Double();
            path.moveTo(eX, eY + eH / 2.0);
            path.quadTo(eX + eW / 2.0, eY - scale(3), eX + eW, eY + eH / 2.0);
            path.quadTo(eX + eW / 2.0, eY + eH + scale(3), eX, eY + eH / 2.0);
            path.closePath();
            g2.draw(path);
            int ps = (int)(eH * 0.5);
            g2.fillOval(eX + (eW - ps)/2, eY + (eH - ps)/2, ps, ps);
            if (!show) { g2.setStroke(new BasicStroke(scale(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g2.drawLine(eX+scale(5), eY+scale(5), eX+eW-scale(5), eY+eH-scale(5)); }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new TicketingDeviceLogin().setVisible(true));
    }
}