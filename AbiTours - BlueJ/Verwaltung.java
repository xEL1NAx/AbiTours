import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import javax.swing.*;

public class Verwaltung {

    private final List<Kunde> kunden     = new ArrayList<>();
    private final List<Angebot> angebote = new ArrayList<>();
    private final DateTimeFormatter df   = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Verwaltung() {
        GUI mainFrame = new GUI(this);
        mainFrame.setTitle("ABI-Tours - Buchungsverwaltung");
        mainFrame.setSize(900, 450);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // --- Kundenverwaltung ---

    public String kundeErstellen(String name) {
        if (name.isEmpty()) return "Fehler: Kein Name eingegeben.";
        Kunde k = new Kunde(name);
        kunden.add(k);
        return "Kunde angelegt: " + name;
    }

    public record KundenDaten(String name, List<String> buchungen) {}

    public KundenDaten getKundenDaten(int nr) {
        Kunde k = sucheKunde(nr);
        if (k == null) return null;
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < k.gibAnzBuchungen(); i++) {
            lines.add(k.gibBuchungAlsText(i));
        }
        return new KundenDaten(k.gibName(), lines);
    }

    public String setzeKundenName(int nr, String neu) {
        Kunde k = sucheKunde(nr);
        if (k == null) return "Kunde nicht gefunden.";
        k.setName(neu);
        return "Name geändert.";
    }

    public String buchungenAbrechnen(int nr) {
        Kunde k = sucheKunde(nr);
        if (k == null) return "Kunde nicht gefunden.";
        int cents = k.rechneBuchungenAb();
        return "Gesamtbetrag: " + String.format("%.2f €", cents / 100.0);
    }

    public Kunde sucheKunde(int nr) {
        return kunden.stream()
        .filter(k -> k.gibKundenNr() == nr)
        .findFirst()
        .orElse(null);
    }

    public List<String> listKunden() {
        List<String> list = new ArrayList<>();
        for (Kunde k : kunden) {
            list.add(k.gibKundenNr() + " - " + k.gibName());
        }
        return list;
    }

    // --- Angebotverwaltung ---

    public String erstelleAngebot(String typ, String titel, String datumText,
    Object preisVal, Object maxVal,
    Object minVal, Object dauerVal,
    String art) {
        if (titel.isEmpty()) return "Fehler: Titel darf nicht leer sein!";
        LocalDate datum;
        try {
            datum = LocalDate.parse(datumText, df);
        } catch (DateTimeParseException e) {
            return "Fehler: Ungültiges Datumsformat! Bitte TT.MM.JJJJ verwenden.";
        }
        int preis = (int) (((Number) preisVal).doubleValue() * 100);
        int maxTN = (int) maxVal;
        int minTN = (int) minVal;
        int dauer = (int) dauerVal;

        Angebot a = Angebot.erzeuge(typ, titel, datum, preis, minTN, maxTN, dauer, art);
        angebote.add(a);

        return typ.equalsIgnoreCase("Kurs")
        ? "Kurs angelegt: " + titel
        : "Aktivität angelegt: " + titel;
    }

    public Angebot sucheAngebot(int nr) {
        return angebote.stream()
        .filter(a -> a.gibAngebotNr() == nr)
        .findFirst()
        .orElse(null);
    }

    public String buche(String angebotNrText, String kundenNrText) {
        int aNr = parseInt(angebotNrText), kNr = parseInt(kundenNrText);
        if (aNr < 0 || kNr < 0) return "Ungültige Eingabe bei Buchung.";

        Kunde k = sucheKunde(kNr);
        Angebot a = sucheAngebot(aNr);

        if (k == null || a == null) {
            return "Buchung fehlgeschlagen: Ungültige Daten.";
        }

        if (!a.anmeldenTN(k)) {
            return "Buchung fehlgeschlagen: Angebot voll oder bereits gebucht.";
        }

        k.eintragenBuchung(a);
        return "Buchung erfolgreich: Kunde " + k.gibName() + " für " + a.gibTitel();
    }

    public String loescheBuchung(String angebotNrText, String kundenNrText) {
        int aNr = parseInt(angebotNrText), kNr = parseInt(kundenNrText);
        if (aNr < 0 || kNr < 0) return "Ungültige Eingabe für Löschung.";

        Kunde k = sucheKunde(kNr);
        Angebot a = sucheAngebot(aNr);

        if (k == null || a == null) {
            return "Buchung konnte nicht gelöscht werden.";
        }

        boolean b1 = a.abmeldenTN(kNr);
        boolean b2 = k.loescheBuchung(a);
        return (b1 && b2)
        ? "Buchung gelöscht: Kunde-Nr " + kNr + " von Angebot-Nr " + aNr
        : "Buchung konnte nicht gelöscht werden.";
    }

    public List<String> listAngebote() {
        List<String> list = new ArrayList<>();
        for (Angebot a : angebote) {
            list.add(a.gibAngebotNr() + " - " + a.gibAlleDatenAlsTextzeile());
        }
        return list;
    }

    public record AngebotDaten(String typ, String titel, String datum,
    double preisEuro, int maxTN, int minTN,
    int dauer, String art) {}

    public AngebotDaten getAngebotDaten(int id) {
        Angebot a = sucheAngebot(id);
        if (a == null) return null;
        return new AngebotDaten(
            a.getTyp(),
            a.gibTitel(),
            a.getDatumText(df),
            a.gibPreis() / 100.0,
            a.gibMaxAnzTN(),
            a.gibMinAnzTeiln(),
            a.gibDauer(),
            a.gibArt()
        );
    }

    public String aktualisiereAngebot(String idText, String titel, String datumText,
    Object preisVal, Object maxVal,
    Object minVal, Object dauerVal,
    String art) {
        int id = parseInt(idText);
        Angebot a = sucheAngebot(id);
        if (a == null) return "Hinweis: Angebot nicht gefunden.";

        try {
            LocalDate datum = LocalDate.parse(datumText, df);
            a.setTitel(titel);
            a.setDatum(datum);
            a.setPreis((int) (((Number) preisVal).doubleValue() * 100));
            a.setMaxAnzTN((int) maxVal);
            a.setMinAnzTeiln((int) minVal);
            a.setDauer((int) dauerVal);
            a.setArt(art);
            return "Angebot geändert: " + titel;
        } catch (DateTimeParseException e) {
            return "Fehler: Ungültiges Datumsformat!";
        } catch (Exception e) {
            return "Fehler: Ungültige Eingaben beim Speichern.";
        }
    }

    // --- Angebot-Statusverwaltung über Allgemeine API ---

    public String angebotAbsagen(int angebotNr) {
        Angebot a = sucheAngebot(angebotNr);
        if (a == null) {
            return "Fehler: Angebot nicht gefunden.";
        }
        a.absagen();
        return "Angebot wurde abgesagt.";
    }

    public String angebotDurchfuehren(int angebotNr) {
        Angebot a = sucheAngebot(angebotNr);
        if (a == null) {
            return "Fehler: Angebot nicht gefunden.";
        }
        a.durchfuehren();
        return "Angebot wurde als durchgeführt markiert.";
    }

    // --- Kostenberechnung für Abrechnung ---

    public int berechneKundenKosten(int kundenNr, boolean nurDurchgefuehrt) {
        Kunde k = sucheKunde(kundenNr);
        if (k == null) return 0;
        return k.berechneKosten(nurDurchgefuehrt);
    }

    // In Verwaltung.java
    public String buchungenAbrechnen(int kundenNr, String angebotNrText) {
        Kunde k = sucheKunde(kundenNr);
        if (k == null) return "Kunde nicht gefunden.";

        int angebotNr = parseInt(angebotNrText);
        if (angebotNr < 0) return "Ungültige Angebotsnummer.";

        Angebot a = sucheAngebot(angebotNr);
        if (a == null) return "Angebot nicht gefunden.";

        // Prüfe ob das Angebot beendet ist
        if (a.getZustand() != Angebot.Zustand.BEENDET) {
            return "Angebot muss beendet sein, um abgerechnet zu werden.";
        }

        // Lösche die Buchung (trennt Verbindung zwischen Kunde und Angebot)
        boolean b1 = a.abmeldenTN(kundenNr);
        boolean b2 = k.loescheBuchung(a);

        if (b1 && b2) {
            int preis = a.gibPreis();
            return "Buchung abgerechnet: " + String.format("%.2f €", preis / 100.0);
        } else {
            return "Abrechnung fehlgeschlagen.";
        }
    }
}