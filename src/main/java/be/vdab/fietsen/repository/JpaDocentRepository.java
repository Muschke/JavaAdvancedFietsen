package be.vdab.fietsen.repository;


import be.vdab.fietsen.domain.Docent;
import be.vdab.fietsen.projections.AantalDocentenPerWedde;
import be.vdab.fietsen.projections.IdeneMail;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
class JpaDocentRepository implements DocentRepository{
    private final EntityManager manager;
    JpaDocentRepository(EntityManager manager) {
        this.manager = manager;
    }
    @Override
    public Optional<Docent> findById(long id) {
        return Optional.ofNullable(manager.find(Docent.class, id));
    }
    @Override
    public void create (Docent docent) {
        manager.persist(docent);
    }
    @Override
    public void delete(long id) {
        findById(id).ifPresent(docent -> manager.remove(docent));
    }
    @Override
    public List<Docent> findAll() {
        return manager.createQuery("select d from Docent d order by d.wedde", Docent.class).getResultList();
    }
    /* Onderstaande methode wordt vervangen door named Query, start onder de comment. Je ziet hem in entity Docent
    @Override
    public List<Docent> findByWeddeBetween(BigDecimal van, BigDecimal tot) {
        return manager.createQuery("select d from Docent d where d.wedde between :van and :tot", Docent.class)
                .setParameter("van", van)
                .setParameter("tot", tot)
                .getResultList();
    } */
    @Override
    public List<Docent> findByWeddeBetween(BigDecimal van, BigDecimal tot) {
        return manager.createNamedQuery("Docent.findByWeddeBetween", Docent.class);
    }
    @Override
    public List<String> findemailAdressen() {
        return manager.createQuery("select d.emailAdres from Docent d", String.class).getResultList();
    }
    @Override
    public List<IdeneMail> findIdenMail() {
        return manager.createQuery("select new be.vdab.fietsen.projections.IdeneMail(d.id, d.emailAdres) " +
                "from Docent d", IdeneMail.class).getResultList();
    }
    @Override
    public BigDecimal findGrootsteWedde() {
        return manager.createQuery("select max(d.wedde) from Docent d", BigDecimal.class).getSingleResult();
    }
    @Override
    public List<AantalDocentenPerWedde> findAantalDocentenPerWedde() {
        //Volgende query geeft per wedde het aantal docenten met die wedde. Zo kan je groepen tellen dus
        return manager.createQuery("select new be.vdab.fietsen.projections.AantalDocentenPerWedde" +
                "(d.wedde, count(d)) from  Docent d group by d.wedde", AantalDocentenPerWedde.class).getResultList();
    }
}
