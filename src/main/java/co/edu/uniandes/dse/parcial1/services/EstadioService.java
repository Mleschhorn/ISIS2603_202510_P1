package co.edu.uniandes.dse.parcial1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import co.edu.uniandes.dse.parcial1.entities.EstadioEntity;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.EstadioRepository;

@Slf4j
@Service
public class EstadioService {

    @Autowired
    private EstadioRepository estadioRepository;

    public EstadioEntity createEstadio(EstadioEntity estadio) throws IllegalOperationException {
        if (estadio.getCiudad().length() < 3) {
            throw new IllegalOperationException("El nombre de la ciudad debe tener al menos 3 letras.");
        }
        if (estadio.getCapacidadMaxima() <= 1000) {
            throw new IllegalOperationException("La capacidad del estadio debe ser superior a 1000 personas.");
        }
        if (estadio.getPrecioAlquiler() <= 100000) {
            throw new IllegalOperationException("El precio de alquiler debe ser superior a 100000 dólares.");
        }
        return estadioRepository.save(estadio);
    }
}
