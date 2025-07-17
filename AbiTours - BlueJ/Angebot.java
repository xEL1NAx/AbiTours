import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Angebot {
    protected static int aNaechsteAngebotsNr = 1;
    protected final int aAngebotNr;
    protected String aTitel;
    protected LocalDate aDatum;
    protected int aPreis;         // in Cent
    protected int aMaxAnzTN;
    protected int aAnzTN = 0;
    protected Zustand aZustand = Zustand.KEINE_TN;
    protected final List<Kunde> aTeilnehmer = new ArrayList<>();

    protected Angebot(String pTitel, LocalDate pDatum, int pPreis, int pMaxAnzTN) {
        this.aAngebotNr = aNaechsteAngebotsNr++;
        this.aTitel     = pTitel;
        this.aDatum     = pDatum;
        this.aPreis     = pPreis;
        this.aMaxAnzTN  = pMaxAnzTN;
    }

    public static Angebot erzeuge(String typ, String titel, LocalDate datum,
    int preis, int minTN, int maxTN, int dauer, String art) {
        if ("Kurs".equalsIgnoreCase(typ)) {
            return new Kurs(titel, datum, dauer, preis, minTN, maxTN);
        } else {
            if (art == null || art.isEmpty()) art = "Aktivität";
            return new Aktivitaet(titel, datum, art, preis, maxTN);
        }
    }

    public boolean anmeldenTN(Kunde k) {
        if (!kannTeilnehmen() || aTeilnehmer.contains(k)) return false;
        aTeilnehmer.add(k);
        aAnzTN++;
        aktualisiereZustand();
        return true;
    }

    public boolean abmeldenTN(int kundenNr) {
        for (Kunde k : new ArrayList<>(aTeilnehmer)) {
            if (k.gibKundenNr() == kundenNr) {
                aTeilnehmer.remove(k);
                aAnzTN--;
                aktualisiereZustandNachAbgang();
                return true;
            }
        }
        return false;
    }

    protected abstract void aktualisiereZustand();

    protected void aktualisiereZustandNachAbgang() {
        if (aZustand != Zustand.BEENDET && aZustand != Zustand.ABGESAGT) {
            aktualisiereZustand();
        }
    }

    protected boolean kannTeilnehmen() {
        return aAnzTN < aMaxAnzTN
        && aZustand != Zustand.ABGESAGT
        && aZustand != Zustand.BEENDET;
    }

    public enum Zustand {
        KEINE_TN, ZU_WENIG_TN, FINDET_STATT, AUSGEBUCHT, ABGESAGT, BEENDET
    }

    public void absagen() {
        if (aZustand != Zustand.BEENDET && aZustand != Zustand.ABGESAGT) {
            aZustand = Zustand.ABGESAGT;
        }
    }

    public void durchfuehren() {
        aZustand = Zustand.BEENDET;
    }

    public int gibAngebotNr()                    { return aAngebotNr; }

    public String gibTitel()                     { return aTitel; }

    public int gibPreis()                        { return aPreis; }

    public int gibMaxAnzTN()                     { return aMaxAnzTN; }

    public int gibAnzahlTN()                     { return aAnzTN; }

    public Zustand getZustand()                  { return aZustand; }

    public boolean istAbgesagt()                 { return aZustand == Zustand.ABGESAGT; }

    public String getTyp()                       { return getClass().getSimpleName(); }

    public String getDatumText(DateTimeFormatter df) { return aDatum.format(df); }

    public int gibMinAnzTeiln()                  { return 0; }

    public int gibDauer()                        { return 0; }

    public String gibArt()                       { return ""; }

    public void setMinAnzTeiln(int min)          {}

    public void setDauer(int d)                  {}

    public void setArt(String art)               {}

    public void setTitel(String t)               { this.aTitel = t; }

    public void setDatum(LocalDate d)            { this.aDatum = d; }

    public void setPreis(int p)                  { this.aPreis = p; }

    public void setMaxAnzTN(int m)               { this.aMaxAnzTN = m; }

    public abstract String gibAlleDatenAlsTextzeile();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Angebot)) return false;
        return aAngebotNr == ((Angebot) o).aAngebotNr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aAngebotNr);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s am %s", aAngebotNr, aTitel, aDatum);
    }
}