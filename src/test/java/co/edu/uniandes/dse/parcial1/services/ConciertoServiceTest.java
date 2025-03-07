package co.edu.uniandes.dse.parcial1.services;

import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import co.edu.uniandes.dse.parcial1.entities.ConciertoEntity;
import co.edu.uniandes.dse.parcial1.entities.EstadioEntity;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.ConciertoRepository;

import co.edu.uniandes.dse.parcial1.services.ConciertoService;

@Transactional
@Import(ConciertoService.class)
@DataJpaTest
class ConciertoServiceTest {

    @Autowired
    private ConciertoService conciertoService;

    @Autowired
    private TestEntityManager entityManager;

    private EstadioEntity estadio;

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
    }

    @Test
    void testCreateConciertoSuccess() throws IllegalOperationException {
        ConciertoEntity concierto = new ConciertoEntity();
        concierto.setNombre("Concierto Test");
        concierto.setArtista("Artista Test");
        concierto.setPresupuesto(5000L);
        concierto.setFecha(new Date(System.currentTimeMillis() + 900000000)); 
        concierto.setCapacidadAforo(100);
        concierto.setEstadio(estadio);

        ConciertoEntity result = conciertoService.createConcierto(concierto);
        assertNotNull(result);
        assertEquals(concierto.getNombre(), result.getNombre());
    }

    @Test
    void testCreateConciertoFailPastDate() {
        ConciertoEntity concierto = new ConciertoEntity();
        concierto.setNombre("Concierto Test");
        concierto.setArtista("Artista Test");
        concierto.setPresupuesto(5000L);
        concierto.setFecha(new Date(System.currentTimeMillis() - 900000000)); 
        concierto.setCapacidadAforo(100);
        concierto.setEstadio(estadio);

        assertThrows(IllegalOperationException.class, () -> {
            conciertoService.createConcierto(concierto);
        });
    }
}
