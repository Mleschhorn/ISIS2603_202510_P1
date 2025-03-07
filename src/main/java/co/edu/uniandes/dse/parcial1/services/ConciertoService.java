package co.edu.uniandes.dse.parcial1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.uniandes.dse.parcial1.entities.ConciertoEntity;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.ConciertoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;

@Slf4j
@Service
public class ConciertoService {

    @Autowired
    private ConciertoRepository conciertoRepository;

    @Transactional
    public ConciertoEntity createConcierto(ConciertoEntity conciertoEntity) throws IllegalOperationException {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime conciertoDate = conciertoEntity.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        if (Duration.between(today, conciertoDate).isNegative()) {
            throw new IllegalOperationException("La fecha del concierto no puede estar en el pasado.");
        }

        if (conciertoEntity.getCapacidadAforo() <= 10) {
            throw new IllegalOperationException("La capacidad del concierto debe ser superior a 10 personas.");
        }

        if (conciertoEntity.getPresupuesto() <= 1000) {
            throw new IllegalOperationException("El presupuesto del concierto debe ser mayor a 1000 dólares.");
        }

        return conciertoRepository.save(conciertoEntity);
    }
}
