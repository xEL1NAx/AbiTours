import java.util.ArrayList;
import java.util.List;

public class Kunde {
    private static int aNaechsteKundenNr = 101;
    private final int aKundenNr;
    private String aName;
    private final List<Angebot> aBuchungen = new ArrayList<>();

    public Kunde(String pName) {
        this.aKundenNr = aNaechsteKundenNr++;
        this.aName     = pName;
    }

    public int gibKundenNr()        { return aKundenNr; }

    public String gibName()         { return aName; }

    public void setName(String neu) { this.aName = neu; }

    public int gibAnzBuchungen()    { return aBuchungen.size(); }

    public void eintragenBuchung(Angebot pAngebot) {
        if (!aBuchungen.contains(pAngebot)) {
            aBuchungen.add(pAngebot);
        }
    }

    public boolean loescheBuchung(Angebot pAngebot) {
        return aBuchungen.remove(pAngebot);
    }

    public String gibBuchungAlsText(int idx) {
        if (idx < 0 || idx >= aBuchungen.size()) return "";
        return aBuchungen.get(idx).gibAlleDatenAlsTextzeile();
    }

    public int berechneKosten(boolean nurDurchgefuehrte) {
        int summe = 0, anz = 0;
        for (Angebot a : aBuchungen) {
            if (!a.istAbgesagt()
            && (!nurDurchgefuehrte || a.getZustand() == Angebot.Zustand.BEENDET)) {
                summe += a.gibPreis();
                anz++;
            }
        }
        if (anz >= 5) summe = (int) Math.round(summe * 0.95);
        else if (anz >= 3) summe = (int) Math.round(summe * 0.97);
        return summe;
    }

    public int rechneBuchungenAb() {
        return berechneKosten(true);
    }

    public List<Angebot> gibAlleBuchungen() {
        return new ArrayList<>(aBuchungen);
    }
}