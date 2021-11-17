package be.vdab.fietsen.repository;

import be.vdab.fietsen.domain.Docent;

import java.math.BigDecimal;
import java.util.Optional;

public interface DocentRepository {
    Optional<Docent> findById(long id);
    public void create (Docent docent);
    void delete (long id);

}
