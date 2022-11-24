package com.ingenieria.producto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ingenieria.producto.IntegrationTest;
import com.ingenieria.producto.domain.Producto;
import com.ingenieria.producto.repository.EntityManager;
import com.ingenieria.producto.repository.ProductoRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProductoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductoResourceIT {

    private static final String DEFAULT_MARCA = "AAAAAAAAAA";
    private static final String UPDATED_MARCA = "BBBBBBBBBB";

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRECIO = 1F;
    private static final Float UPDATED_PRECIO = 2F;

    private static final Integer DEFAULT_STOCK = 1;
    private static final Integer UPDATED_STOCK = 2;

    private static final String ENTITY_API_URL = "/api/productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Producto producto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createEntity(EntityManager em) {
        Producto producto = new Producto()
            .marca(DEFAULT_MARCA)
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .precio(DEFAULT_PRECIO)
            .stock(DEFAULT_STOCK);
        return producto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createUpdatedEntity(EntityManager em) {
        Producto producto = new Producto()
            .marca(UPDATED_MARCA)
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precio(UPDATED_PRECIO)
            .stock(UPDATED_STOCK);
        return producto;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Producto.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        producto = createEntity(em);
    }

    @Test
    void createProducto() throws Exception {
        int databaseSizeBeforeCreate = productoRepository.findAll().collectList().block().size();
        // Create the Producto
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeCreate + 1);
        Producto testProducto = productoList.get(productoList.size() - 1);
        assertThat(testProducto.getMarca()).isEqualTo(DEFAULT_MARCA);
        assertThat(testProducto.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testProducto.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testProducto.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testProducto.getStock()).isEqualTo(DEFAULT_STOCK);
    }

    @Test
    void createProductoWithExistingId() throws Exception {
        // Create the Producto with an existing ID
        producto.setId(1L);

        int databaseSizeBeforeCreate = productoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProductosAsStream() {
        // Initialize the database
        productoRepository.save(producto).block();

        List<Producto> productoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Producto.class)
            .getResponseBody()
            .filter(producto::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(productoList).isNotNull();
        assertThat(productoList).hasSize(1);
        Producto testProducto = productoList.get(0);
        assertThat(testProducto.getMarca()).isEqualTo(DEFAULT_MARCA);
        assertThat(testProducto.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testProducto.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testProducto.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testProducto.getStock()).isEqualTo(DEFAULT_STOCK);
    }

    @Test
    void getAllProductos() {
        // Initialize the database
        productoRepository.save(producto).block();

        // Get all the productoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(producto.getId().intValue()))
            .jsonPath("$.[*].marca")
            .value(hasItem(DEFAULT_MARCA))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].descripcion")
            .value(hasItem(DEFAULT_DESCRIPCION))
            .jsonPath("$.[*].precio")
            .value(hasItem(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.[*].stock")
            .value(hasItem(DEFAULT_STOCK));
    }

    @Test
    void getProducto() {
        // Initialize the database
        productoRepository.save(producto).block();

        // Get the producto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, producto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(producto.getId().intValue()))
            .jsonPath("$.marca")
            .value(is(DEFAULT_MARCA))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.descripcion")
            .value(is(DEFAULT_DESCRIPCION))
            .jsonPath("$.precio")
            .value(is(DEFAULT_PRECIO.doubleValue()))
            .jsonPath("$.stock")
            .value(is(DEFAULT_STOCK));
    }

    @Test
    void getNonExistingProducto() {
        // Get the producto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProducto() throws Exception {
        // Initialize the database
        productoRepository.save(producto).block();

        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();

        // Update the producto
        Producto updatedProducto = productoRepository.findById(producto.getId()).block();
        updatedProducto
            .marca(UPDATED_MARCA)
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precio(UPDATED_PRECIO)
            .stock(UPDATED_STOCK);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProducto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProducto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
        Producto testProducto = productoList.get(productoList.size() - 1);
        assertThat(testProducto.getMarca()).isEqualTo(UPDATED_MARCA);
        assertThat(testProducto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testProducto.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testProducto.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testProducto.getStock()).isEqualTo(UPDATED_STOCK);
    }

    @Test
    void putNonExistingProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, producto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        productoRepository.save(producto).block();

        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto.marca(UPDATED_MARCA).nombre(UPDATED_NOMBRE).stock(UPDATED_STOCK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
        Producto testProducto = productoList.get(productoList.size() - 1);
        assertThat(testProducto.getMarca()).isEqualTo(UPDATED_MARCA);
        assertThat(testProducto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testProducto.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testProducto.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testProducto.getStock()).isEqualTo(UPDATED_STOCK);
    }

    @Test
    void fullUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        productoRepository.save(producto).block();

        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto
            .marca(UPDATED_MARCA)
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .precio(UPDATED_PRECIO)
            .stock(UPDATED_STOCK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
        Producto testProducto = productoList.get(productoList.size() - 1);
        assertThat(testProducto.getMarca()).isEqualTo(UPDATED_MARCA);
        assertThat(testProducto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testProducto.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testProducto.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testProducto.getStock()).isEqualTo(UPDATED_STOCK);
    }

    @Test
    void patchNonExistingProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, producto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProducto() throws Exception {
        int databaseSizeBeforeUpdate = productoRepository.findAll().collectList().block().size();
        producto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(producto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Producto in the database
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProducto() {
        // Initialize the database
        productoRepository.save(producto).block();

        int databaseSizeBeforeDelete = productoRepository.findAll().collectList().block().size();

        // Delete the producto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, producto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Producto> productoList = productoRepository.findAll().collectList().block();
        assertThat(productoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
