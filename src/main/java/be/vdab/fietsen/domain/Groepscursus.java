package be.vdab.fietsen.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
//voor bij single_table: @DiscriminatorValue("G")
@Table(name = "groepscursussen")
public class Groepscursus extends Cursus{
    private LocalDate van;
    private LocalDate tot;

    protected Groepscursus() {};
    public Groepscursus(String naam, LocalDate van, LocalDate tot) {
        super(naam);
        this.van = van;
        this.tot = tot;
    }


    public LocalDate getVan() {
        return van;
    }

    public LocalDate getTot() {
        return tot;
    }
}
