package be.vdab.fietsen.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
//voor bij single_table: @DiscriminatorValue("I")
@Table(name = "individuelecursussen")
public class IndividueleCursus extends Cursus{
    private int duurtijd;

    protected IndividueleCursus() {};

    public IndividueleCursus(String naam, int duurtijd) {
        super(naam);
        this.duurtijd = duurtijd;
    }


    public int getDuurtijd() {
        return duurtijd;
    }
}
