package javaproject;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HostelGUI extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public HostelGUI() {
        setTitle("Hostel Management System - Professional Edition");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Sidebar Menu ---
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 10, 10));
        sidebar.setBackground(new Color(26, 37, 47));
        sidebar.setPreferredSize(new Dimension(220, 700));

        String[] menuItems = {"Home", "Add Student", "View Students", "Update Room", "Remove Student", "Search", "Exit"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(44, 62, 80));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.addActionListener(e -> handleMenuClick(item));
            sidebar.add(btn);
        }

        // --- Main Content Area ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        refreshAllPanels();

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private void handleMenuClick(String item) {
        if (item.equals("Exit")) System.exit(0);
        if (item.equals("Home") || item.equals("View Students")) {
            refreshAllPanels(); // Reload data from DB
        }
        cardLayout.show(cardPanel, item);
    }

    private void refreshAllPanels() {
        cardPanel.removeAll();
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(createAddPanel(), "Add Student");
        cardPanel.add(createViewPanel(), "View Students");
        cardPanel.add(createUpdatePanel(), "Update Room");
        cardPanel.add(createRemovePanel(), "Remove Student");
        cardPanel.add(createSearchPanel(), "Search");
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // 1. HOME PANEL (With Count Stats)
 // 1. HOME PANEL (With Exact Counts for Students and Fees)
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245)); // Light gray background
        
        JLabel header = new JLabel("Hostel Management System", JLabel.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(header, BorderLayout.NORTH);

        // Grid with 3 cards
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(new Color(245, 245, 245));
        stats.setBorder(BorderFactory.createEmptyBorder(50, 30, 100, 30));

        // Card 1: Total Students
        stats.add(createStatCard("Total Students", 
            getCount("SELECT COUNT(*) FROM students"), 
            new Color(52, 152, 219))); // Blue

        // Card 2: Fees Paid
        stats.add(createStatCard("Fees Paid", 
            getCount("SELECT COUNT(*) FROM students WHERE fee_status = 'Paid'"), 
            new Color(46, 204, 113))); // Green

        // Card 3: Fees Unpaid
        stats.add(createStatCard("Fees Unpaid", 
            getCount("SELECT COUNT(*) FROM students WHERE fee_status = 'Unpaid'"), 
            new Color(231, 76, 60))); // Red
        
        panel.add(stats, BorderLayout.CENTER);
        return panel;
    }
    // 2. ADD STUDENT (Functional)
    private JPanel createAddPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField id = new JTextField(15), name = new JTextField(15), room = new JTextField(15), contact = new JTextField(15);
        JComboBox<String> fee = new JComboBox<>(new String[]{"Paid", "Unpaid"});
        JButton btn = new JButton("Save Record");

        addToGrid(p, new JLabel("ID:"), id, g, 0);
        addToGrid(p, new JLabel("Name:"), name, g, 1);
        addToGrid(p, new JLabel("Room:"), room, g, 2);
        addToGrid(p, new JLabel("Contact:"), contact, g, 3);
        addToGrid(p, new JLabel("Status:"), fee, g, 4);
        
        g.gridx = 1; g.gridy = 5; p.add(btn, g);

        btn.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO students VALUES(?,?,?,?,?)");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, name.getText());
                ps.setInt(3, Integer.parseInt(room.getText()));
                ps.setString(4, contact.getText());
                ps.setString(5, fee.getSelectedItem().toString());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Added!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        return p;
    }

    // 3. VIEW STUDENTS (Table)
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM students");
            JTable table = new JTable(buildTableModel(rs));
            panel.add(new JScrollPane(table));
        } catch (Exception e) { panel.add(new JLabel("DB Error")); }
        return panel;
    }

    // 4. UPDATE ROOM (Functional)
    private JPanel createUpdatePanel() {
        JPanel p = new JPanel(new FlowLayout());
        JTextField id = new JTextField(5), room = new JTextField(5);
        JButton btn = new JButton("Update Room");
        p.add(new JLabel("Student ID:")); p.add(id);
        p.add(new JLabel("New Room:")); p.add(room);
        p.add(btn);

        btn.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("UPDATE students SET room_number=? WHERE id=?");
                ps.setInt(1, Integer.parseInt(room.getText()));
                ps.setInt(2, Integer.parseInt(id.getText()));
                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(this, rows > 0 ? "Updated!" : "Not Found");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        return p;
    }

    // 5. REMOVE STUDENT (Functional)
    private JPanel createRemovePanel() {
        JPanel p = new JPanel(new FlowLayout());
        JTextField id = new JTextField(10);
        JButton btn = new JButton("Delete Student");
        btn.setBackground(Color.RED); btn.setForeground(Color.WHITE);
        p.add(new JLabel("Enter ID to Delete:")); p.add(id); p.add(btn);

        btn.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted Successfully!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        return p;
    }

    // 6. SEARCH PANEL
    private JPanel createSearchPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JTextField searchId = new JTextField(10);
        JButton btn = new JButton("Search");
        JTextArea res = new JTextArea(10, 30);
        res.setEditable(false);

        top.add(new JLabel("ID:")); top.add(searchId); top.add(btn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(res), BorderLayout.CENTER);

        btn.addActionListener(e -> {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM students WHERE id=?");
                ps.setInt(1, Integer.parseInt(searchId.getText()));
                ResultSet rs = ps.executeQuery();
                if(rs.next()) res.setText("Name: " + rs.getString(2) + "\nRoom: " + rs.getInt(3) + "\nContact: " + rs.getString(4));
                else res.setText("No record found.");
            } catch (Exception ex) { res.setText("Error search."); }
        });
        return p;
    }

    // --- HELPER METHODS ---
    private String getCount(String query) {
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(query);
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        } catch (Exception e) { return "0"; }
    }

    private JPanel createStatCard(String title, String val, Color c) {
        JPanel card = new JPanel();
        card.setBackground(c);
        card.setLayout(new GridLayout(2, 1));
        JLabel l1 = new JLabel(title, JLabel.CENTER); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel(val, JLabel.CENTER); l2.setFont(new Font("Arial", Font.BOLD, 40)); l2.setForeground(Color.WHITE);
        card.add(l1); card.add(l2);
        return card;
    }

    private void addToGrid(JPanel p, JLabel l, JComponent c, GridBagConstraints g, int row) {
        g.gridx = 0; g.gridy = row; p.add(l, g);
        g.gridx = 1; p.add(c, g);
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        Vector<String> col = new Vector<>();
        for (int i = 1; i <= md.getColumnCount(); i++) col.add(md.getColumnName(i));
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> v = new Vector<>();
            for (int i = 1; i <= md.getColumnCount(); i++) v.add(rs.getObject(i));
            data.add(v);
        }
        return new DefaultTableModel(data, col);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HostelGUI().setVisible(true));
    }
}