package be.vdab.fietsen.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "docenten")
//Je mag @Table weglaten als de table naam gelijk is aan de class naam.
@NamedQuery(name = "Docent.findByWeddeBetween", query = "select d from Docent d where d.wedde between :van and :tot order by d.wedde, d.id")
public class Docent {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id private long id;
    private String voornaam;
    private String familienaam;
    private BigDecimal wedde;
    private String emailAdres;

    //Deze  domeinen hebben default constructor nodig ook:
    protected Docent() {};

    public Docent(String voornaam, String familienaam, BigDecimal wedde, String emailAdres, Geslacht geslacht) {
        this.voornaam = voornaam;
        this.familienaam = familienaam;
        this.wedde = wedde;
        this.emailAdres = emailAdres;
        this.geslacht = geslacht;
    }

    @Enumerated(EnumType.STRING) private Geslacht geslacht;

    //om methode wijzigen uit te leggen - je moet met JPA Géén update statement sturen naar database
    public void opslag(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
        var factor = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
        wedde = wedde.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        /*dus in feite, deze laatste lijn code wijzigt ons wedde. JPA weet gewoon dat hij dat in de database moet gaan doen.
        * als je opslag uitvoert, stuurt JPA zelf update statement naar database. Het testen van dit statement gebeurt wel degelijk
        * in docentTest in niet in de repository*/
    }


    public Geslacht getGeslacht() {
        return geslacht;
    }
    public long getId() {
        return id;
    }
    public String getVoornaam() {
        return voornaam;
    }
    public String getFamilienaam() {
        return familienaam;
    }
    public BigDecimal getWedde() {
        return wedde;
    }
    public String getEmailAdres() {
        return emailAdres;
    }
}
