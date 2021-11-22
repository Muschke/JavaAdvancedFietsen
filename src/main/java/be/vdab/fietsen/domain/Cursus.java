package be.vdab.fietsen.domain;

import javax.persistence.*;

@Entity
//gebruik SINGLE_TABLE als je inherited classes in 1 tabel worden bijgehouden
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "cursussen")
// deze is enkel nodig bij SINGLE_TABLE: @DiscriminatorColumn(name = "soort")
public abstract class Cursus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String naam;

    protected Cursus() {};

    public Cursus(String naam) {
        this.naam = naam;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }
}
