package edu.uniminuto.inventario;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final Map<Long, Producto> productos = new TreeMap<>();
    private long siguienteId = 1;

    public synchronized Producto crear(ProductoSolicitud solicitud) {
        long id = siguienteId++;
        Producto producto = new Producto(id, solicitud.nombre(), solicitud.stock());
        productos.put(id, producto);
        return producto;
    }

    public synchronized List<Producto> listar() {
        return List.copyOf(productos.values());
    }

    public synchronized Optional<Producto> buscar(long id) {
        return Optional.ofNullable(productos.get(id));
    }

    public synchronized Optional<Producto> actualizar(long id, ProductoSolicitud solicitud) {
        Producto actual = productos.get(id);
        if (actual == null) {
            return Optional.empty();
        }

        Producto actualizado = new Producto(id, solicitud.nombre(), actual.stock());
        productos.put(id, actualizado);
        return Optional.of(actualizado);
    }

    public synchronized boolean eliminar(long id) {
        return productos.remove(id) != null;
    }
}
