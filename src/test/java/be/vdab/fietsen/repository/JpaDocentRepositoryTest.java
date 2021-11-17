package be.vdab.fietsen.repository;

import be.vdab.fietsen.domain.Docent;
import be.vdab.fietsen.domain.Geslacht;
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

    //aparte functies
    private long idVanTestMan() {
        return jdbcTemplate.queryForObject("select id from docenten where voornaam = 'testM'", long.class);
    }
    private long idVanTestVrouw() {
        return jdbcTemplate.queryForObject(
                "select id from docenten where voornaam = 'testV'", Long.class);
    }
}