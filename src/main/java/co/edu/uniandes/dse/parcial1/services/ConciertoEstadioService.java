package co.edu.uniandes.dse.parcial1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.uniandes.dse.parcial1.entities.ConciertoEntity;
import co.edu.uniandes.dse.parcial1.entities.EstadioEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.ConciertoRepository;
import co.edu.uniandes.dse.parcial1.repositories.EstadioRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.ZoneId;

@Slf4j
@Service
public class ConciertoEstadioService {

    @Autowired
    private ConciertoRepository conciertoRepository;

    @Autowired
    private EstadioRepository estadioRepository;

    @Transactional
    public ConciertoEntity addConciertoToEstadio(Long estadioId, ConciertoEntity conciertoEntity) throws EntityNotFoundException, IllegalOperationException {
        EstadioEntity estadioEntity = estadioRepository.findById(estadioId)
                .orElseThrow(() -> new EntityNotFoundException("Estadio not found"));

        
        if (conciertoEntity.getCapacidadAforo() > estadioEntity.getCapacidadMaxima()) {
            throw new IllegalOperationException("La capacidad del concierto no debe superar la capacidad del estadio.");
        }

        
        if (estadioEntity.getPrecioAlquiler() > conciertoEntity.getPresupuesto()) {
            throw new IllegalOperationException("El precio de alquiler del estadio no debe superar el presupuesto del concierto.");
        }

        
        for (ConciertoEntity existingConcierto : estadioEntity.getConciertos()) {
            long daysBetween = Duration.between(
                    existingConcierto.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    conciertoEntity.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            ).toDays();
            if (Math.abs(daysBetween) < 2) {
                throw new IllegalOperationException("Debe existir un tiempo mínimo de 2 días entre los conciertos asociados a un estadio.");
            }
        }

        conciertoEntity.setEstadio(estadioEntity);
        return conciertoRepository.save(conciertoEntity);
    }
}
