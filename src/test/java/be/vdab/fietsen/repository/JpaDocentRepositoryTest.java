package be.vdab.fietsen.repository;

import be.vdab.fietsen.domain.Docent;
import be.vdab.fietsen.domain.Geslacht;
import be.vdab.fietsen.projections.AantalDocentenPerWedde;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
//het gedeelte '(showSql = false)' is om de dubbele log van de sql statements tegen te gaan
@Sql("/insertDocent.sql")
@Import(JpaDocentRepository.class)

class JpaDocentRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
private final JpaDocentRepository jpaDocentRepository;
private static final String DOCENTEN = "docenten";
private Docent docent;
private final EntityManager entityManager;

    JpaDocentRepositoryTest(JpaDocentRepository jpaDocentRepository, EntityManager entityManager) {
        this.jpaDocentRepository = jpaDocentRepository;
        this.entityManager = entityManager;
    }

    //test persist (create) methode --> toevoegen DOCENTEN & private docent docent
    @BeforeEach
    void beforeEach() {
        docent = new Docent("test", "test", BigDecimal.TEN, "test@test.be", Geslacht.MAN);
    }
    @Test
    void create() {
        jpaDocentRepository.create(docent);
        assertThat(docent.getId()).isPositive();
        assertThat(countRowsInTableWhere(DOCENTEN, "id=" + docent.getId())).isOne();
    }
    //verwijderen --> toevoegen Entitymanager
    @Test
    void delete() {
        var id = idVanTestMan();
        jpaDocentRepository.delete(id);
        entityManager.flush(); // delete methode werkt als soort van batch, dit sluit de batch af
        assertThat(countRowsInTableWhere(DOCENTEN, "id =" + id)).isZero();
    }
    //vinden op basis van id
    @Test void findById() {
        assertThat(jpaDocentRepository.findById(idVanTestMan()))
                .hasValueSatisfying(docent -> assertThat(docent.getVoornaam()).isEqualTo("testM"));
    }
    @Test void findByOnbestaandeId() {
        assertThat(jpaDocentRepository.findById(-1)).isNotPresent();
    }

    //testen enum
    @Test void man() {
        assertThat(jpaDocentRepository.findById(idVanTestMan()))
                .hasValueSatisfying(docent ->  assertThat(docent.getGeslacht()).isEqualTo(Geslacht.MAN));
    }
    @Test void vrouw() {
        assertThat(jpaDocentRepository.findById(idVanTestVrouw()))
                .hasValueSatisfying(docent -> assertThat(docent.getGeslacht()).isEqualTo(Geslacht.VROUW));
    }

    //findall
    @Test void findall() {
        assertThat(jpaDocentRepository.findAll())
                .hasSize(countRowsInTable(DOCENTEN))
                .extracting(Docent::getWedde)
                .isSorted();
    }
    //findBetween
    @Test void findByWeddeBetween() {
        var duizend = BigDecimal.valueOf(1000);
        var tweeduizend = BigDecimal.valueOf(2000);
        var docenten = jpaDocentRepository.findByWeddeBetween(duizend, tweeduizend);
        assertThat(docenten)
                .hasSize(countRowsInTableWhere(DOCENTEN, "wedde between 1000 and 2000"))
                .allSatisfy(docent -> assertThat(docent.getWedde()).isBetween(duizend, tweeduizend));
    }
    //test mailadressen terughalen
    @Test void findEmailadres() {
        assertThat(jpaDocentRepository.findemailAdressen())
                .hasSize(countRowsInTable(DOCENTEN))
                .allSatisfy(emailadres -> assertThat(emailadres).contains("@"));
    }
    //test projectie
    @Test void findIdenEmailadressen() {
        assertThat(jpaDocentRepository.findIdenMail())
                .hasSize(countRowsInTable(DOCENTEN));
    }
    //aggregate methode  test
    @Test void findGrootsteWedde() {
        assertThat(jpaDocentRepository.findGrootsteWedde()).isEqualByComparingTo(
                jdbcTemplate.queryForObject("select max(wedde) from docenten", BigDecimal.class));
    }
    @Test void findAantalDocentenPerWedde() {
        var duizend = BigDecimal.valueOf(1_000);
        assertThat(jpaDocentRepository.findAantalDocentenPerWedde())
                .hasSize(jdbcTemplate.queryForObject(
                        "select count(distinct wedde) from docenten", Integer.class))
                .filteredOn(aantalperwedde-> aantalperwedde.getWedde().compareTo(duizend) == 0)
                .singleElement()
                .extracting(AantalDocentenPerWedde::getAantal)
                .isEqualTo((long) super.countRowsInTableWhere(DOCENTEN, "wedde = 1000"));
    }
    @Test void algemeneOpslag() {
        //eerste controle is dat je evenveel rijen opslag hebt gegeven als er rijen zijn
        assertThat(jpaDocentRepository.algemeneOpslag(BigDecimal.TEN))
                .isEqualTo(countRowsInTable(DOCENTEN));
        assertThat(countRowsInTableWhere(DOCENTEN, "wedde = 1100 and id = " + idVanTestMan())).isOne();
    }

    //aparte functies
    private long idVanTestMan() {
        return jdbcTemplate.queryForObject("select id from docenten where voornaam = 'testM'", long.class);
    }
    private long idVanTestVrouw() {
        return jdbcTemplate.queryForObject(
                "select id from docenten where voornaam = 'testV'", Long.class);
    }

}