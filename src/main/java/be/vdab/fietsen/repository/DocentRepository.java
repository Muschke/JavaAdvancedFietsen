package be.vdab.fietsen.repository;

import be.vdab.fietsen.domain.Docent;
import be.vdab.fietsen.projections.AantalDocentenPerWedde;
import be.vdab.fietsen.projections.IdeneMail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DocentRepository {
    Optional<Docent> findById(long id);
    public void create(Docent docent);
    void delete(long id);
    List<Docent> findAll();
    List<Docent> findByWeddeBetween(BigDecimal van, BigDecimal tot);
    List<String> findemailAdressen();
    List<IdeneMail> findIdenMail();
    BigDecimal findGrootsteWedde();
    List<AantalDocentenPerWedde> findAantalDocentenPerWedde();
    int algemeneOpslag(BigDecimal percentage);
}
