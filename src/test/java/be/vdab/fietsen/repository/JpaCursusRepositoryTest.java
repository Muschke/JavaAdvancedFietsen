package be.vdab.fietsen.repository;

import be.vdab.fietsen.domain.Groepscursus;
import be.vdab.fietsen.domain.IndividueleCursus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import(JpaCursusRepository.class)
@Sql("/insertCursus.sql")
class JpaCursusRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String CURSUSSEN = "cursussen";
    private static final LocalDate EEN_DATUM = LocalDate.of(2019, 1, 1);
    private final JpaCursusRepository jpaCursusRepository;
    //toevoegen voor bij seperate tables
    private static final String GROEPSCURSUSSEN = "groepscursussen";
    private static final String INDIVIDUELE_CURSUSSEN = "individuelecursussen";

    JpaCursusRepositoryTest(JpaCursusRepository jpaCursusRepository) {
        this.jpaCursusRepository = jpaCursusRepository;
    }

    @Test
    void findGroepById() {
        assertThat(jpaCursusRepository.findById(idVanTestGCursus()))
                .containsInstanceOf(Groepscursus.class)
                .hasValueSatisfying(cursus -> assertThat(cursus.getNaam()).isEqualTo("testGroep"));
    }
    @Test
    void findIndById() {
        assertThat(jpaCursusRepository.findById(idVanTestICursus()))
                .containsInstanceOf(IndividueleCursus.class)
                .hasValueSatisfying(cursus -> assertThat(cursus.getNaam()).isEqualTo("testIndividueel"));
    }
    @Test
    void findUnexistingIdFails() {
        assertThat(jpaCursusRepository.findById(-1)).isNotPresent();
    }

    @Test
    void createGroepCursus() {
        var gcursus = new Groepscursus("testGroepTwee", EEN_DATUM, EEN_DATUM);
        jpaCursusRepository.create(gcursus);
        assertThat(countRowsInTableWhere(CURSUSSEN, "id = '" + gcursus.getId() + "'")).isOne();
        //onderstaande asserthat moet er niet bij bij single_table
        assertThat(countRowsInTableWhere(GROEPSCURSUSSEN, "id = '" + gcursus.getId() + "'"));

    }
    @Test
    void createIndCursus() {
        var icursus = new IndividueleCursus("testIndividueelTwee", 7);
        jpaCursusRepository.create(icursus);
        assertThat(countRowsInTableWhere(CURSUSSEN, "id = '" + icursus.getId() + "'")).isOne();
        //onderstaande asserthat moet er niet bij bij single_table
        assertThat(countRowsInTableWhere(INDIVIDUELE_CURSUSSEN, "id = '" + icursus.getId() + "'"));
    }

    private long idVanTestICursus() {
        return jdbcTemplate.queryForObject(
                "select id from cursussen where naam = 'testIndividueel'", Long.class);
    }
    private long idVanTestGCursus() {
        return jdbcTemplate.queryForObject(
                "select id from cursussen where naam = 'testGroep'", Long.class);
    }
}