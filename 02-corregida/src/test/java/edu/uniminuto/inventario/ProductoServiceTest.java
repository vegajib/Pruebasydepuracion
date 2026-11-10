package edu.uniminuto.inventario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProductoServiceTest {

    @Test
    void actualizaElStockSolicitado() {
        ProductoService servicio = new ProductoService();
        Producto original = servicio.crear(new ProductoSolicitud("Teclado", 10));

        Producto actualizado = servicio.actualizar(
                original.id(), new ProductoSolicitud("Teclado USB", 15)).orElseThrow();

        assertEquals(15, actualizado.stock(), "Stock devuelto");
        assertEquals(actualizado, servicio.buscar(original.id()).orElseThrow(),
                "La actualización también debe quedar en memoria");
    }
}
