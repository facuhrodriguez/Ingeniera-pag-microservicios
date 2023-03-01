package com.ingenieria.factura.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ingenieria.factura.IntegrationTest;
import com.ingenieria.factura.domain.Factura;
import com.ingenieria.factura.repository.EntityManager;
import com.ingenieria.factura.repository.FacturaRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FacturaResourceIT {

    private static final LocalDate DEFAULT_FECHA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_TOTAL_SIN_IVA = 1D;
    private static final Double UPDATED_TOTAL_SIN_IVA = 2D;

    private static final Double DEFAULT_IVA = 1D;
    private static final Double UPDATED_IVA = 2D;

    private static final Double DEFAULT_TOTAL_CON_IVA = 1D;
    private static final Double UPDATED_TOTAL_CON_IVA = 2D;

    private static final String DEFAULT_ID_CLIENTE = "1";
    private static final String UPDATED_ID_CLIENTE = "2";

    private static final String ENTITY_API_URL = "/api/facturas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Factura factura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity(EntityManager em) {
        Factura factura = new Factura()
            .fecha(DEFAULT_FECHA)
            .totalSinIva(DEFAULT_TOTAL_SIN_IVA)
            .iva(DEFAULT_IVA)
            .totalConIva(DEFAULT_TOTAL_CON_IVA)
            .idCliente(DEFAULT_ID_CLIENTE);
        return factura;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity(EntityManager em) {
        Factura factura = new Factura()
            .fecha(UPDATED_FECHA)
            .totalSinIva(UPDATED_TOTAL_SIN_IVA)
            .iva(UPDATED_IVA)
            .totalConIva(UPDATED_TOTAL_CON_IVA)
            .idCliente(UPDATED_ID_CLIENTE);
        return factura;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Factura.class).block();
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
        factura = createEntity(em);
    }

    @Test
    void createFactura() throws Exception {
        int databaseSizeBeforeCreate = facturaRepository.findAll().collectList().block().size();
        // Create the Factura
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate + 1);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testFactura.getTotalSinIva()).isEqualTo(DEFAULT_TOTAL_SIN_IVA);
        assertThat(testFactura.getIva()).isEqualTo(DEFAULT_IVA);
        assertThat(testFactura.getTotalConIva()).isEqualTo(DEFAULT_TOTAL_CON_IVA);
        assertThat(testFactura.getIdCliente()).isEqualTo(DEFAULT_ID_CLIENTE);
    }

    @Test
    void createFacturaWithExistingId() throws Exception {
        // Create the Factura with an existing ID
        factura.setId(1L);

        int databaseSizeBeforeCreate = facturaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFacturasAsStream() {
        // Initialize the database
        facturaRepository.save(factura).block();

        List<Factura> facturaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Factura.class)
            .getResponseBody()
            .filter(factura::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(facturaList).isNotNull();
        assertThat(facturaList).hasSize(1);
        Factura testFactura = facturaList.get(0);
        assertThat(testFactura.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testFactura.getTotalSinIva()).isEqualTo(DEFAULT_TOTAL_SIN_IVA);
        assertThat(testFactura.getIva()).isEqualTo(DEFAULT_IVA);
        assertThat(testFactura.getTotalConIva()).isEqualTo(DEFAULT_TOTAL_CON_IVA);
        assertThat(testFactura.getIdCliente()).isEqualTo(DEFAULT_ID_CLIENTE);
    }

    @Test
    void getAllFacturas() {
        // Initialize the database
        facturaRepository.save(factura).block();

        // Get all the facturaList
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
            .value(hasItem(factura.getId().intValue()))
            .jsonPath("$.[*].fecha")
            .value(hasItem(DEFAULT_FECHA.toString()))
            .jsonPath("$.[*].totalSinIva")
            .value(hasItem(DEFAULT_TOTAL_SIN_IVA.doubleValue()))
            .jsonPath("$.[*].iva")
            .value(hasItem(DEFAULT_IVA.doubleValue()))
            .jsonPath("$.[*].totalConIva")
            .value(hasItem(DEFAULT_TOTAL_CON_IVA.doubleValue()))
            .jsonPath("$.[*].idCliente")
            .value(hasItem(Integer.valueOf(DEFAULT_ID_CLIENTE)));
    }

    @Test
    void getFactura() {
        // Initialize the database
        facturaRepository.save(factura).block();

        // Get the factura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(factura.getId().intValue()))
            .jsonPath("$.fecha")
            .value(is(DEFAULT_FECHA.toString()))
            .jsonPath("$.totalSinIva")
            .value(is(DEFAULT_TOTAL_SIN_IVA.doubleValue()))
            .jsonPath("$.iva")
            .value(is(DEFAULT_IVA.doubleValue()))
            .jsonPath("$.totalConIva")
            .value(is(DEFAULT_TOTAL_CON_IVA.doubleValue()))
            .jsonPath("$.idCliente")
            .value(is(Integer.valueOf(DEFAULT_ID_CLIENTE)));
    }

    @Test
    void getNonExistingFactura() {
        // Get the factura
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFactura() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).block();
        updatedFactura
            .fecha(UPDATED_FECHA)
            .totalSinIva(UPDATED_TOTAL_SIN_IVA)
            .iva(UPDATED_IVA)
            .totalConIva(UPDATED_TOTAL_CON_IVA)
            .idCliente(UPDATED_ID_CLIENTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFactura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testFactura.getTotalSinIva()).isEqualTo(UPDATED_TOTAL_SIN_IVA);
        assertThat(testFactura.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFactura.getTotalConIva()).isEqualTo(UPDATED_TOTAL_CON_IVA);
        assertThat(testFactura.getIdCliente()).isEqualTo(UPDATED_ID_CLIENTE);
    }

    @Test
    void putNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura.iva(UPDATED_IVA).totalConIva(UPDATED_TOTAL_CON_IVA).idCliente(UPDATED_ID_CLIENTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testFactura.getTotalSinIva()).isEqualTo(DEFAULT_TOTAL_SIN_IVA);
        assertThat(testFactura.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFactura.getTotalConIva()).isEqualTo(UPDATED_TOTAL_CON_IVA);
        assertThat(testFactura.getIdCliente()).isEqualTo(UPDATED_ID_CLIENTE);
    }

    @Test
    void fullUpdateFacturaWithPatch() throws Exception {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();

        // Update the factura using partial update
        Factura partialUpdatedFactura = new Factura();
        partialUpdatedFactura.setId(factura.getId());

        partialUpdatedFactura
            .fecha(UPDATED_FECHA)
            .totalSinIva(UPDATED_TOTAL_SIN_IVA)
            .iva(UPDATED_IVA)
            .totalConIva(UPDATED_TOTAL_CON_IVA)
            .idCliente(UPDATED_ID_CLIENTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFactura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFactura))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testFactura.getTotalSinIva()).isEqualTo(UPDATED_TOTAL_SIN_IVA);
        assertThat(testFactura.getIva()).isEqualTo(UPDATED_IVA);
        assertThat(testFactura.getTotalConIva()).isEqualTo(UPDATED_TOTAL_CON_IVA);
        assertThat(testFactura.getIdCliente()).isEqualTo(UPDATED_ID_CLIENTE);
    }

    @Test
    void patchNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().collectList().block().size();
        factura.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(factura))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFactura() {
        // Initialize the database
        facturaRepository.save(factura).block();

        int databaseSizeBeforeDelete = facturaRepository.findAll().collectList().block().size();

        // Delete the factura
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, factura.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Factura> facturaList = facturaRepository.findAll().collectList().block();
        assertThat(facturaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
