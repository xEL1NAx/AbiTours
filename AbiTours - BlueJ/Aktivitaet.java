import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Aktivitaet extends Angebot {
    private String aArt;

    public Aktivitaet(String pTitel, LocalDate pDatum,
    String pArt, int pPreis, int pMaxAnzTN) {
        super(pTitel, pDatum, pPreis, pMaxAnzTN);
        this.aArt = pArt;
        aktualisiereZustand();
    }

    @Override
    protected void aktualisiereZustand() {
        if (getZustand() == Zustand.BEENDET || getZustand() == Zustand.ABGESAGT) return;
        if      (gibAnzahlTN() == 0)             aZustand = Zustand.KEINE_TN;
        else if (gibAnzahlTN() < gibMaxAnzTN())  aZustand = Zustand.FINDET_STATT;
        else                                     aZustand = Zustand.AUSGEBUCHT;
    }

    @Override public void setArt(String art) { this.aArt = art; }

    @Override public String gibArt()         { return aArt; }

    @Override
    public String gibAlleDatenAlsTextzeile() {
        String status = switch (getZustand()) {
                case FINDET_STATT -> "findet statt";
                case AUSGEBUCHT   -> "ausgebucht";
                default           -> "keine TN";
            };
        String datumText = getDatumText(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return String.format("%d - %s - %s - %s - %.2f€ * %s",
            gibAngebotNr(), gibTitel(), datumText,
            aArt, gibPreis() / 100.0, status);
    }
}