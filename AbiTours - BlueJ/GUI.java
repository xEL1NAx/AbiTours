import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private final Verwaltung verwaltung;
    private JTextArea konsolenAusgabe;

    public GUI(Verwaltung verwaltung) {
        this.verwaltung = verwaltung;
        setTitle("Abi-Tours");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setSize(900, 450);

        JLabel titleLabel = new JLabel("Abi-Tours");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(20, 10, 200, 30);
        add(titleLabel);

        addButton("Angebot anlegen",       50,  e -> zeigeAngebotAnlegenDialog());
        addButton("Kunde anlegen",         90,  e -> zeigeKundenAnlegenDialog());
        addButton("Buchung eintragen",    130,  e -> zeigeBuchungDialog());
        addButton("Buchung löschen",      170,  e -> zeigeBuchungLoeschenDialog());
        addButton("Kundendaten verwalten",210,  e -> zeigeKundenVerwaltenDialog());
        addButton("Angebot verwalten",    250,  e -> zeigeAngebotVerwaltenDialog());

        konsolenAusgabe = new JTextArea();
        konsolenAusgabe.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(konsolenAusgabe);
        scrollPane.setBounds(220, 50, 640, 350);
        add(scrollPane);

        setVisible(true);
    }

    private void addButton(String text, int y, ActionListener handler) {
        JButton button = new JButton(text);
        button.setBounds(20, y, 180, 30);
        button.addActionListener(handler);
        add(button);
    }

    private void zeigeHinweis(String text) {
        JOptionPane.showMessageDialog(this, text);
        konsolenAusgabe.append("Hinweis: " + text + "\n");
    }

    private void zeigeKundenAnlegenDialog() {
        JDialog dialog = new JDialog(this, "Neuen Kunden anlegen", true);
        dialog.setLayout(new GridLayout(2,2,10,10));
        dialog.setSize(350,120);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JButton speichern = new JButton("Speichern");
        speichern.addActionListener(e -> {
                    String msg = verwaltung.kundeErstellen(nameField.getText().trim());
                    konsolenAusgabe.append(msg + "\n");
                    dialog.dispose();
            });

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel());
        dialog.add(speichern);
        dialog.setVisible(true);
    }

    private void zeigeKundenVerwaltenDialog() {
        JDialog dialog = new JDialog(this, "Kundendaten anzeigen", true);
        dialog.setSize(600, 400);
        dialog.setLayout(null);

        JLabel lblKundenNr = new JLabel("Kundennummer");
        lblKundenNr.setBounds(20, 20, 120, 25);
        dialog.add(lblKundenNr);

        JTextField kundenNrField = new JTextField();
        kundenNrField.setBounds(150, 20, 100, 25);
        dialog.add(kundenNrField);

        JLabel lblName = new JLabel("Name");
        lblName.setBounds(20, 60, 120, 25);
        dialog.add(lblName);

        JTextField nameField = new JTextField();
        nameField.setBounds(150, 60, 200, 25);
        dialog.add(nameField);

        JLabel lblBuchungen = new JLabel("Buchungen");
        lblBuchungen.setBounds(20, 100, 120, 25);
        dialog.add(lblBuchungen);

        JTextArea buchungenArea = new JTextArea();
        buchungenArea.setEditable(false);
        buchungenArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(buchungenArea);
        scroll.setBounds(20, 130, 540, 150);
        dialog.add(scroll);

        JButton btnAnzeigen = new JButton("Kundendaten anzeigen");
        btnAnzeigen.setBounds(390, 20, 180, 25);
        dialog.add(btnAnzeigen);

        JButton btnSpeichern = new JButton("Änderungen speichern");
        btnSpeichern.setBounds(390, 60, 180, 25);
        dialog.add(btnSpeichern);

        JButton btnAbrechnen = new JButton("Buchungen abrechnen");
        btnAbrechnen.setBounds(390, 100, 180, 25);
        dialog.add(btnAbrechnen);

        btnAnzeigen.addActionListener(e -> {
                    int nr = verwaltung.parseInt(kundenNrField.getText());
                    if (nr < 0) { zeigeHinweis("Ungültige Nummer."); return; }
                    var data = verwaltung.getKundenDaten(nr);
                    if (data == null) {
                        zeigeHinweis("Kunde nicht gefunden.");
                    } else {
                        nameField.setText(data.name());
                        buchungenArea.setText(String.join("\n", data.buchungen()));
                    }
            });

        btnSpeichern.addActionListener(e -> {
                    int nr = verwaltung.parseInt(kundenNrField.getText());
                    if (nr < 0) { zeigeHinweis("Ungültige Nummer."); return; }
                    String msg = verwaltung.setzeKundenName(nr, nameField.getText().trim());
                    zeigeHinweis(msg);
            });

        btnAbrechnen.addActionListener(e -> {
                    int nr = verwaltung.parseInt(kundenNrField.getText());
                    if (nr < 0) { zeigeHinweis("Ungültige Nummer."); return; }
                    zeigeAbrechnungsDialog(nr);
            });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void zeigeAbrechnungsDialog(int kundenNr) {
        JDialog dialog = new JDialog(this, "Buchungen abrechnen", true);
        dialog.setLayout(null);
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);

        double ausstehend = verwaltung.berechneKundenKosten(kundenNr, false) / 100.0;
        double bezahlt    = verwaltung.berechneKundenKosten(kundenNr, true)  / 100.0;

        JLabel lblAusstehend = new JLabel("Ausstehende Kosten:");
        lblAusstehend.setBounds(20, 20, 160, 25);
        dialog.add(lblAusstehend);

        JTextField feldAusstehend = new JTextField(String.format("%.2f €", ausstehend));
        feldAusstehend.setEditable(false);
        feldAusstehend.setBounds(200, 20, 140, 25);
        dialog.add(feldAusstehend);

        JLabel lblBezahlt = new JLabel("Zu bezahlen:");
        lblBezahlt.setBounds(20, 60, 160, 25);
        dialog.add(lblBezahlt);

        JTextField feldBezahlt = new JTextField(String.format("%.2f €", bezahlt));
        feldBezahlt.setEditable(false);
        feldBezahlt.setBounds(200, 60, 140, 25);
        dialog.add(feldBezahlt);

        JButton btnAbrechnen = new JButton("Abrechnen");
        btnAbrechnen.setBounds(140, 120, 120, 30);
        dialog.add(btnAbrechnen);

        btnAbrechnen.addActionListener(e -> {
                    JDialog nrDialog = new JDialog(this, "Angebot auswählen", true);
                    nrDialog.setLayout(new GridLayout(2,2,10,10));
                    nrDialog.setSize(350, 120);
                    nrDialog.setLocationRelativeTo(this);

                    nrDialog.add(new JLabel("Angebotsnummer:"));
                    JTextField angebotField = new JTextField();
                    nrDialog.add(angebotField);

                    JButton bestätigen = new JButton("Bestätigen");
                    nrDialog.add(new JLabel());
                    nrDialog.add(bestätigen);

                    bestätigen.addActionListener(ev -> {
                                String msg = verwaltung.buchungenAbrechnen(kundenNr, angebotField.getText().trim());
                                konsolenAusgabe.append(msg + "\n");

                                // Aktualisierte Buchungsliste anzeigen
                                var data = verwaltung.getKundenDaten(kundenNr);
                                konsolenAusgabe.append("Verbleibende offene Buchungen:\n");
                                for (String line : data.buchungen()) {
                                    if (!line.isEmpty()) {
                                        konsolenAusgabe.append("  " + line + "\n");
                                    }
                                }

                                nrDialog.dispose();
                                dialog.dispose();
                        });

                    nrDialog.setVisible(true);
            });

        dialog.setVisible(true);
    }

    private void zeigeAngebotVerwaltenDialog() {
        JDialog dialog = new JDialog(this, "Angebot bearbeiten", true);
        dialog.setSize(500, 650);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        panel.add(new JLabel("Angebot-Nr.:"));
        JTextField idField = new JTextField(); panel.add(idField);

        panel.add(new JLabel("Typ (Kurs/Aktivität):"));
        JComboBox<String> typCombo = new JComboBox<>(new String[]{"Kurs","Aktivität"});
        typCombo.setEnabled(false); panel.add(typCombo);

        panel.add(new JLabel("Titel:"));
        JTextField titelField = new JTextField(); panel.add(titelField);

        panel.add(new JLabel("Datum (TT.MM.JJJJ):"));
        JTextField datumField = new JTextField(); panel.add(datumField);

        panel.add(new JLabel("Preis (€):"));
        JSpinner preisSpinner = new JSpinner(
                new SpinnerNumberModel(0.0,0.0,10000.0,0.5));
        panel.add(preisSpinner);

        panel.add(new JLabel("Max. Teilnehmer:"));
        JSpinner maxTnSpinner = new JSpinner(
                new SpinnerNumberModel(1,1,100,1));
        panel.add(maxTnSpinner);

        panel.add(new JLabel("Min. Teilnehmer (Kurs):"));
        JSpinner minTnSpinner = new JSpinner(
                new SpinnerNumberModel(1,1,100,1));
        panel.add(minTnSpinner);

        panel.add(new JLabel("Dauer (Tage, Kurs):"));
        JSpinner dauerSpinner = new JSpinner(
                new SpinnerNumberModel(1,1,30,1));
        panel.add(dauerSpinner);

        panel.add(new JLabel("Art (Aktivität):"));
        JTextField artField = new JTextField(); panel.add(artField);

        JButton ladenButton = new JButton("Angebot laden");
        panel.add(ladenButton);
        panel.add(new JLabel());

        JButton btnAbsagen = new JButton("Angebot absagen");
        JButton btnDurchfuehren = new JButton("Angebot durchgeführt");
        btnAbsagen.setEnabled(false);
        btnDurchfuehren.setEnabled(false);
        panel.add(btnAbsagen);
        panel.add(btnDurchfuehren);

        dialog.add(panel, BorderLayout.CENTER);

        JButton speichern = new JButton("Änderungen speichern");
        speichern.setEnabled(false);
        dialog.add(speichern, BorderLayout.SOUTH);

        ladenButton.addActionListener(e -> {
                    int id = verwaltung.parseInt(idField.getText());
                    var opt = verwaltung.getAngebotDaten(id);
                    if (opt == null) {
                        konsolenAusgabe.append("Hinweis: Angebot nicht gefunden.\n");
                    } else {
                        typCombo.setSelectedItem(opt.typ());
                        titelField.setText(opt.titel());
                        datumField.setText(opt.datum());
                        preisSpinner.setValue(opt.preisEuro());
                        maxTnSpinner.setValue(opt.maxTN());
                        if ("Kurs".equals(opt.typ())) {
                            minTnSpinner.setValue(opt.minTN());
                            dauerSpinner.setValue(opt.dauer());
                        } else {
                            artField.setText(opt.art());
                        }
                        // Buttons für alle Angebote aktivieren
                        btnAbsagen.setEnabled(true);
                        btnDurchfuehren.setEnabled(true);
                        speichern.setEnabled(true);
                    }
            });

        speichern.addActionListener(e -> {
                    String msg = verwaltung.aktualisiereAngebot(
                            idField.getText(), titelField.getText().trim(),
                            datumField.getText().trim(), preisSpinner.getValue(),
                            maxTnSpinner.getValue(), minTnSpinner.getValue(),
                            dauerSpinner.getValue(), artField.getText().trim());
                    konsolenAusgabe.append(msg + "\n");
                    if (msg.startsWith("Angebot geändert")) dialog.dispose();
            });

        btnAbsagen.addActionListener(e -> {
                    int id = verwaltung.parseInt(idField.getText());
                    String msg = verwaltung.angebotAbsagen(id);
                    konsolenAusgabe.append(msg + "\n");
            });

        btnDurchfuehren.addActionListener(e -> {
                    int id = verwaltung.parseInt(idField.getText());
                    String msg = verwaltung.angebotDurchfuehren(id);
                    konsolenAusgabe.append(msg + "\n");
            });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void zeigeAngebotAnlegenDialog() {
        JDialog dialog = new JDialog(this, "Neues Angebot anlegen", true);
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Angebotstyp:"));
        JComboBox<String> typCombo = new JComboBox<>(new String[]{"Kurs", "Aktivität"});
        inputPanel.add(typCombo);

        inputPanel.add(new JLabel("Titel:"));
        JTextField titelField = new JTextField(); inputPanel.add(titelField);

        inputPanel.add(new JLabel("Datum (TT.MM.JJJJ):"));
        JTextField datumField = new JTextField(); inputPanel.add(datumField);

        inputPanel.add(new JLabel("Preis (€):"));
        JSpinner preisSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.5));
        inputPanel.add(preisSpinner);

        inputPanel.add(new JLabel("Max. Teilnehmer:"));
        JSpinner maxTnSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(maxTnSpinner);

        inputPanel.add(new JLabel("Min. Teilnehmer (nur Kurs):"));
        JSpinner minTnSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(minTnSpinner);

        inputPanel.add(new JLabel("Dauer (Tage, nur Kurs):"));
        JSpinner dauerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        inputPanel.add(dauerSpinner);

        inputPanel.add(new JLabel("Art (nur Aktivität):"));
        JTextField artField = new JTextField(); inputPanel.add(artField);

        dialog.add(inputPanel, BorderLayout.CENTER);

        JButton speichernButton = new JButton("Speichern");
        JPanel buttonPanel = new JPanel(); buttonPanel.add(speichernButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        speichernButton.addActionListener(e -> {
                    String result = verwaltung.erstelleAngebot(
                            (String) typCombo.getSelectedItem(),
                            titelField.getText().trim(),
                            datumField.getText().trim(),
                            preisSpinner.getValue(),
                            maxTnSpinner.getValue(),
                            minTnSpinner.getValue(),
                            dauerSpinner.getValue(),
                            artField.getText().trim()
                        );
                    konsolenAusgabe.append(result + "\n");
                    dialog.dispose();
            });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void zeigeBuchungDialog() {
        JDialog dialog = new JDialog(this, "Buchung eintragen", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        dialog.add(new JLabel("Angebot-Nr  -  Name  -  Datum  -  Dauer  -  Preis  -  Anz. Teilnehmer"), gbc);

        DefaultListModel<String> angeboteModel = new DefaultListModel<>();
        for (String line : verwaltung.listAngebote()) angeboteModel.addElement(line);
        JList<String> angebotList = new JList<>(angeboteModel);
        JScrollPane angebotScroll = new JScrollPane(angebotList);
        angebotScroll.setPreferredSize(new Dimension(550, 100));
        gbc.gridy = 1; dialog.add(angebotScroll, gbc);

        gbc.gridy = 2;
        dialog.add(new JLabel("Kunden-Nr  -  Name"), gbc);
        DefaultListModel<String> kundenModel = new DefaultListModel<>();
        for (String line : verwaltung.listKunden()) kundenModel.addElement(line);
        JList<String> kundenList = new JList<>(kundenModel);
        JScrollPane kundenScroll = new JScrollPane(kundenList);
        kundenScroll.setPreferredSize(new Dimension(550, 100));
        gbc.gridy = 3; dialog.add(kundenScroll, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4; gbc.gridx = 0; dialog.add(new JLabel("Angebot-Nr."), gbc);
        JTextField angebotNrField = new JTextField(); gbc.gridx = 1; dialog.add(angebotNrField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; dialog.add(new JLabel("Kunden-Nr."), gbc);
        JTextField kundenNrField = new JTextField(); gbc.gridx = 1; dialog.add(kundenNrField, gbc);

        JButton buchenButton = new JButton("Angebot buchen");
        gbc.gridx = 2; gbc.gridy = 4; dialog.add(buchenButton, gbc);

        JButton statusButton = new JButton("Prüfung");
        statusButton.setEnabled(false);
        statusButton.setPreferredSize(new Dimension(120, 30));
        statusButton.setMinimumSize(new Dimension(120, 30));
        statusButton.setMaximumSize(new Dimension(120, 30));
        gbc.gridy = 5; dialog.add(statusButton, gbc);

        angebotList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting() && angebotList.getSelectedValue() != null) {
                        angebotNrField.setText(angebotList.getSelectedValue().split(" - ")[0]);
                    }
            });
        kundenList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting() && kundenList.getSelectedValue() != null) {
                        kundenNrField.setText(kundenList.getSelectedValue().split(" - ")[0]);
                    }
            });

        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
                private void update() {
                    String aText = angebotNrField.getText().trim();
                    String kText = kundenNrField.getText().trim();
                    boolean enable = !aText.isEmpty() && !kText.isEmpty();
                    statusButton.setEnabled(enable);
                    if (enable) {
                        statusButton.setText("Prüfung");
                    }
                }

                public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

                public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }

                public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            };
        angebotNrField.getDocument().addDocumentListener(dl);
        kundenNrField.getDocument().addDocumentListener(dl);

        statusButton.addActionListener(e -> {
                    String aNr = angebotNrField.getText().trim();
                    String kNr = kundenNrField.getText().trim();
                    String msg = verwaltung.buche(aNr, kNr);
                    if (msg.startsWith("Buchung erfolgreich")) {
                        verwaltung.loescheBuchung(aNr, kNr);
                        statusButton.setText("Buchung möglich");
                    } else {
                        statusButton.setText("Buchung nicht möglich");
                    }
            });

        buchenButton.addActionListener(e -> {
                    String msg = verwaltung.buche(
                            angebotNrField.getText().trim(),
                            kundenNrField.getText().trim()
                        );
                    konsolenAusgabe.append(msg + "\n");
                    if (msg.startsWith("Buchung erfolgreich")) {
                        dialog.dispose();
                    }
            });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void zeigeBuchungLoeschenDialog() {
        JDialog dialog = new JDialog(this, "Buchung löschen", true);
        dialog.setLayout(new GridLayout(3,2,10,10));
        dialog.setSize(400,160);
        dialog.setLocationRelativeTo(this);

        JTextField angebotField = new JTextField();
        JTextField kundeField = new JTextField();
        JButton loeschen = new JButton("Löschen");

        loeschen.addActionListener(e -> {
                    String msg = verwaltung.loescheBuchung(
                            angebotField.getText().trim(),
                            kundeField.getText().trim()
                        );
                    konsolenAusgabe.append(msg + "\n");
                    dialog.dispose();
            });

        dialog.add(new JLabel("Angebot-Nr.:")); dialog.add(angebotField);
        dialog.add(new JLabel("Kunden-Nr.:")); dialog.add(kundeField);
        dialog.add(new JLabel("")); dialog.add(loeschen);

        dialog.setVisible(true);
    }
}