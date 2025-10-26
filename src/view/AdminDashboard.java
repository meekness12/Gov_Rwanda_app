package view;

import dao.ExchangeOfferDAO;
import model.ExchangeOffer;
import model.User;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboard extends Frame implements ActionListener {
    private final User user;
    private Button btnLogout, btnExport;
    private TextField txtSearch;
    private Choice chFilter;
    private Panel pnlOffers;
    private Canvas statsCanvas;

    private List<ExchangeOffer> allOffers;
    private ExchangeOfferDAO offerDAO;

    public AdminDashboard(User user) {
        this.user = user;
        offerDAO = new ExchangeOfferDAO();

        setTitle("Admin Dashboard - GVEI");
        setSize(850, 650);
        setLayout(null);
        setBackground(Color.ORANGE);

        // Welcome Label
        Label lblWelcome = new Label("Welcome, Admin " + user.getName());
        lblWelcome.setBounds(20, 20, 400, 30);
        add(lblWelcome);

        // Logout Button
        btnLogout = new Button("Logout");
        btnLogout.setBounds(720, 20, 100, 30);
        btnLogout.addActionListener(this);
        add(btnLogout);

        // Export CSV Button
        btnExport = new Button("Export CSV");
        btnExport.setBounds(600, 20, 100, 30);
        btnExport.addActionListener(this);
        add(btnExport);

        // Search field
        txtSearch = new TextField();
        txtSearch.setBounds(20, 70, 150, 30);
        txtSearch.addActionListener(e -> refreshOffers());
        add(txtSearch);

        // Filter by status
        chFilter = new Choice();
        chFilter.add("All");
        chFilter.add("pending");
        chFilter.add("approved");
        chFilter.add("rejected");
        chFilter.setBounds(180, 70, 120, 30);
        chFilter.addItemListener(e -> refreshOffers());
        add(chFilter);

        // Panel to hold offers
        pnlOffers = new Panel();
        pnlOffers.setLayout(new FlowLayout());
        pnlOffers.setBounds(20, 120, 800, 350);
        pnlOffers.setBackground(Color.LIGHT_GRAY);
        add(pnlOffers);

        // Statistics canvas
        statsCanvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                paintStats(g);
            }
        };
        statsCanvas.setBounds(20, 490, 800, 130);
        add(statsCanvas);

        // Load offers
        loadOffers();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void loadOffers() {
        try {
            allOffers = offerDAO.getAllOffers();
            refreshOffers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Label lblError = new Label("Error loading offers: " + ex.getMessage());
            pnlOffers.add(lblError);
        }
    }

    private void refreshOffers() {
        pnlOffers.removeAll();

        String search = txtSearch.getText().trim();
        String filter = chFilter.getSelectedItem();

        List<ExchangeOffer> filtered = allOffers.stream()
                .filter(o -> (search.isEmpty() || String.valueOf(o.getVehicleId()).contains(search)) &&
                        (filter.equals("All") || o.getStatus().equalsIgnoreCase(filter)))
                .collect(Collectors.toList());

        for (ExchangeOffer o : filtered) {
            Panel offerPanel = new Panel(new FlowLayout());
            offerPanel.setBackground(Color.WHITE);
            Label lbl = new Label("OfferID:" + o.getOfferId() +
                    " | VehicleID:" + o.getVehicleId() +
                    " | Value:" + o.getExchangeValue() +
                    " | Subsidy:" + o.getSubsidyPercent() +
                    " | Status:" + o.getStatus());

            Button btnApprove = new Button("Approve");
            Button btnReject = new Button("Reject");

            btnApprove.addActionListener(e -> {
                try {
                    offerDAO.updateStatus(o.getOfferId(), "approved");
                    allOffers.remove(o);
                    refreshOffers();
                } catch (SQLException ex) { ex.printStackTrace(); }
            });

            btnReject.addActionListener(e -> {
                try {
                    offerDAO.updateStatus(o.getOfferId(), "rejected");
                    allOffers.remove(o);
                    refreshOffers();
                } catch (SQLException ex) { ex.printStackTrace(); }
            });

            offerPanel.add(lbl);
            if (o.getStatus().equalsIgnoreCase("pending")) {
                offerPanel.add(btnApprove);
                offerPanel.add(btnReject);
            }

            pnlOffers.add(offerPanel);
        }

        pnlOffers.revalidate();
        pnlOffers.repaint();
        statsCanvas.repaint();
    }

    private void paintStats(Graphics g) {
        long total = allOffers.size();
        long pending = allOffers.stream().filter(o -> o.getStatus().equalsIgnoreCase("pending")).count();
        long approved = allOffers.stream().filter(o -> o.getStatus().equalsIgnoreCase("approved")).count();
        long rejected = allOffers.stream().filter(o -> o.getStatus().equalsIgnoreCase("rejected")).count();

        // Draw bar chart
        int startX = 50, baseY = 100, barWidth = 80, maxHeight = 80;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, statsCanvas.getWidth(), statsCanvas.getHeight());

        // Pending bar
        int hPending = (int) ((pending / (double) Math.max(total, 1)) * maxHeight);
        g.setColor(Color.YELLOW);
        g.fillRect(startX, baseY - hPending, barWidth, hPending);
        g.setColor(Color.BLACK);
        g.drawString("Pending: " + pending, startX, baseY + 20);

        // Approved bar
        int hApproved = (int) ((approved / (double) Math.max(total, 1)) * maxHeight);
        g.setColor(Color.GREEN);
        g.fillRect(startX + 150, baseY - hApproved, barWidth, hApproved);
        g.setColor(Color.BLACK);
        g.drawString("Approved: " + approved, startX + 150, baseY + 20);

        // Rejected bar
        int hRejected = (int) ((rejected / (double) Math.max(total, 1)) * maxHeight);
        g.setColor(Color.RED);
        g.fillRect(startX + 300, baseY - hRejected, barWidth, hRejected);
        g.setColor(Color.BLACK);
        g.drawString("Rejected: " + rejected, startX + 300, baseY + 20);

        // Total
        g.setColor(Color.BLUE);
        g.drawString("Total Offers: " + total, startX + 450, baseY - 50);
    }

    private void exportCSV() {
        try (FileWriter writer = new FileWriter("exchange_offers.csv")) {
            writer.write("offer_id,vehicle_id,exchange_value,subsidy_percent,status\n");
            for (ExchangeOffer o : allOffers) {
                writer.write(o.getOfferId() + "," + o.getVehicleId() + "," + o.getExchangeValue() + "," +
                        o.getSubsidyPercent() + "," + o.getStatus() + "\n");
            }
            Dialog dlg = new Dialog(this, "Exported Successfully", true);
            dlg.setSize(250, 100);
            dlg.add(new Label("CSV exported to exchange_offers.csv"));
            dlg.setVisible(true);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogout) {
            new LoginForm();
            dispose();
        } else if (e.getSource() == btnExport) {
            exportCSV();
        }
    }

    public static void main(String[] args) {
        User admin = new User();
        admin.setName("SuperAdmin");
        new AdminDashboard(admin);
    }
}
