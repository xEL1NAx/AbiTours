import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Kurs extends Angebot {
    private int aMinAnzTN, aAnzTage;

    public Kurs(String pTitel, LocalDate pDatum, int pAnzTage,
    int pPreis, int pMinAnzTN, int pMaxAnzTN) {
        super(pTitel, pDatum, pPreis, pMaxAnzTN);
        this.aMinAnzTN = pMinAnzTN;
        this.aAnzTage  = pAnzTage;
        aktualisiereZustand();
    }

    @Override
    protected void aktualisiereZustand() {
        if (getZustand() == Zustand.BEENDET || getZustand() == Zustand.ABGESAGT) return;
        if      (gibAnzahlTN() == 0)               aZustand = Zustand.KEINE_TN;
        else if (gibAnzahlTN() < aMinAnzTN)        aZustand = Zustand.ZU_WENIG_TN;
        else if (gibAnzahlTN() < aMaxAnzTN)        aZustand = Zustand.FINDET_STATT;
        else /* == max */                          aZustand = Zustand.AUSGEBUCHT;
    }

    @Override public void setMinAnzTeiln(int min) { this.aMinAnzTN = min; }

    @Override public void setDauer(int d)         { this.aAnzTage  = d; }

    @Override public int  gibMinAnzTeiln()        { return aMinAnzTN; }

    @Override public int  gibDauer()              { return aAnzTage; }

    @Override
    public String gibAlleDatenAlsTextzeile() {
        String status = switch (getZustand()) {
                case FINDET_STATT -> "findet statt";
                case ZU_WENIG_TN  -> "zu wenig TN";
                case AUSGEBUCHT   -> "ausgebucht";
                case ABGESAGT     -> "abgesagt";
                case BEENDET      -> "beendet";
                default           -> "keine TN";
            };
        String datumText = getDatumText(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return String.format("%d - %s - %s - Dauer %d Tage - %.2f€ * %s",
            gibAngebotNr(), gibTitel(), datumText,
            aAnzTage, gibPreis() / 100.0, status);
    }
}