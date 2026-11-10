package edu.uniminuto.inventario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProductoControllerTest {

    private static final String PRODUCTOS = "/api/productos";
    private MockMvc mvc;

    @BeforeEach
    void iniciarConInventarioVacio() {
        mvc = MockMvcBuilders.standaloneSetup(
                new ProductoController(new ProductoService())).build();
    }

    @Test
    void creaListaConsultaYElimina() throws Exception {
        crearTeclado();
        mvc.perform(get(PRODUCTOS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"));
        mvc.perform(get(PRODUCTOS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));
        mvc.perform(delete(PRODUCTOS + "/1")).andExpect(status().isNoContent());
        mvc.perform(get(PRODUCTOS + "/1")).andExpect(status().isNotFound());
        mvc.perform(get(PRODUCTOS)).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void putGuardaElStockEnMemoria() throws Exception {
        crearTeclado();
        mvc.perform(put(PRODUCTOS + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado USB","stock":15}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(15));
        mvc.perform(get(PRODUCTOS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado USB"))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    void aceptaCeroComoStockAlActualizar() throws Exception {
        crearTeclado();
        mvc.perform(put(PRODUCTOS + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","stock":0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));
    }

    @Test
    void rechazaStockNegativoSinModificarElProducto() throws Exception {
        crearTeclado();
        mvc.perform(put(PRODUCTOS + "/1").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","stock":-1}
                                """))
                .andExpect(status().isBadRequest());
        mvc.perform(get(PRODUCTOS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void rechazaNombreEnBlancoYStockOmitido() throws Exception {
        mvc.perform(post(PRODUCTOS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":" ","stock":4}
                                """))
                .andExpect(status().isBadRequest());
        mvc.perform(post(PRODUCTOS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devuelve404ParaUnIdInexistente() throws Exception {
        mvc.perform(get(PRODUCTOS + "/99")).andExpect(status().isNotFound());
        mvc.perform(put(PRODUCTOS + "/99").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","stock":4}
                                """))
                .andExpect(status().isNotFound());
        mvc.perform(delete(PRODUCTOS + "/99")).andExpect(status().isNotFound());
    }

    private void crearTeclado() throws Exception {
        mvc.perform(post(PRODUCTOS).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", PRODUCTOS + "/1"))
                .andExpect(jsonPath("$.id").value(1));
    }
}
