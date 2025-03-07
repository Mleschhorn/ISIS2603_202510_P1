package co.edu.uniandes.dse.parcial1.services;

import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import co.edu.uniandes.dse.parcial1.entities.ConciertoEntity;
import co.edu.uniandes.dse.parcial1.entities.EstadioEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;

@Transactional
@Import({ConciertoService.class, EstadioService.class, ConciertoEstadioService.class})
@DataJpaTest
class ConciertoEstadioServiceTest {

    @Autowired
    private ConciertoEstadioService conciertoEstadioService;

    @Autowired
    private TestEntityManager entityManager;

    private EstadioEntity estadio;
    private ConciertoEntity concierto;

    @BeforeEach
    public void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("DELETE FROM ConciertoEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM EstadioEntity").executeUpdate();
    }

    private void insertData() {
        estadio = new EstadioEntity();
        estadio.setNombre("Estadio Test");
        estadio.setCiudad("Ciudad Test");
        estadio.setPrecioAlquiler(200000L);
        estadio.setCapacidadMaxima(50000);
        entityManager.persist(estadio);

        concierto = new ConciertoEntity();
        concierto.setNombre("Concierto Test");
        concierto.setArtista("Artista Test");
        concierto.setPresupuesto(5000L);
        concierto.setFecha(new Date(System.currentTimeMillis() + 900000000));
        concierto.setCapacidadAforo(100);
        entityManager.persist(concierto);
    }

    @Test
    void testAddConciertoToEstadioSuccess() throws EntityNotFoundException, IllegalOperationException {
        ConciertoEntity result = conciertoEstadioService.addConciertoToEstadio(estadio.getId(), concierto);
        assertNotNull(result);
        assertEquals(concierto.getNombre(), result.getNombre());
    }

    @Test
    void testAddConciertoToEstadioFailEstadioNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            conciertoEstadioService.addConciertoToEstadio(999L, concierto);
        });
    }

    @Test
    void testAddConciertoToEstadioFailConciertoNotFound() {
        ConciertoEntity nonExistentConcierto = new ConciertoEntity();
        nonExistentConcierto.setId(999L);
        assertThrows(EntityNotFoundException.class, () -> {
            conciertoEstadioService.addConciertoToEstadio(estadio.getId(), nonExistentConcierto);
        });
    }

    @Test
    void testAddConciertoToEstadioFailDateRule() {
        ConciertoEntity anotherConcierto = new ConciertoEntity();
        anotherConcierto.setNombre("Another Concierto");
        anotherConcierto.setArtista("Another Artista");
        anotherConcierto.setPresupuesto(6000L);
        anotherConcierto.setFecha(new Date(System.currentTimeMillis() + 900000000));
        anotherConcierto.setCapacidadAforo(200);
        anotherConcierto.setEstadio(estadio);
        entityManager.persist(anotherConcierto);

        ConciertoEntity conflictingConcierto = new ConciertoEntity();
        conflictingConcierto.setNombre("Conflicting Concierto");
        conflictingConcierto.setArtista("Conflicting Artista");
        conflictingConcierto.setPresupuesto(7000L);
        conflictingConcierto.setFecha(new Date(System.currentTimeMillis() + 900000000 + 900000000)); 
        conflictingConcierto.setCapacidadAforo(300);

        assertThrows(IllegalOperationException.class, () -> {
            conciertoEstadioService.addConciertoToEstadio(estadio.getId(), conflictingConcierto);
        });
    }
}
