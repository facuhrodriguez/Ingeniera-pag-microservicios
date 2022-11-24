package com.ingenieria.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ingenieria.gateway.IntegrationTest;
import com.ingenieria.gateway.domain.Telefono;
import com.ingenieria.gateway.repository.EntityManager;
import com.ingenieria.gateway.repository.TelefonoRepository;
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
 * Integration tests for the {@link TelefonoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TelefonoResourceIT {

    private static final Integer DEFAULT_CODIGO_AREA = 1;
    private static final Integer UPDATED_CODIGO_AREA = 2;

    private static final Integer DEFAULT_NRO_TELEFONO = 1;
    private static final Integer UPDATED_NRO_TELEFONO = 2;

    private static final String DEFAULT_TIPO = "AAAAAAAAAA";
    private static final String UPDATED_TIPO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/telefonos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TelefonoRepository telefonoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Telefono telefono;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Telefono createEntity(EntityManager em) {
        Telefono telefono = new Telefono().codigoArea(DEFAULT_CODIGO_AREA).nroTelefono(DEFAULT_NRO_TELEFONO).tipo(DEFAULT_TIPO);
        return telefono;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Telefono createUpdatedEntity(EntityManager em) {
        Telefono telefono = new Telefono().codigoArea(UPDATED_CODIGO_AREA).nroTelefono(UPDATED_NRO_TELEFONO).tipo(UPDATED_TIPO);
        return telefono;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Telefono.class).block();
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
        telefono = createEntity(em);
    }

    @Test
    void createTelefono() throws Exception {
        int databaseSizeBeforeCreate = telefonoRepository.findAll().collectList().block().size();
        // Create the Telefono
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeCreate + 1);
        Telefono testTelefono = telefonoList.get(telefonoList.size() - 1);
        assertThat(testTelefono.getCodigoArea()).isEqualTo(DEFAULT_CODIGO_AREA);
        assertThat(testTelefono.getNroTelefono()).isEqualTo(DEFAULT_NRO_TELEFONO);
        assertThat(testTelefono.getTipo()).isEqualTo(DEFAULT_TIPO);
    }

    @Test
    void createTelefonoWithExistingId() throws Exception {
        // Create the Telefono with an existing ID
        telefono.setId(1L);

        int databaseSizeBeforeCreate = telefonoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTelefonosAsStream() {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        List<Telefono> telefonoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Telefono.class)
            .getResponseBody()
            .filter(telefono::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(telefonoList).isNotNull();
        assertThat(telefonoList).hasSize(1);
        Telefono testTelefono = telefonoList.get(0);
        assertThat(testTelefono.getCodigoArea()).isEqualTo(DEFAULT_CODIGO_AREA);
        assertThat(testTelefono.getNroTelefono()).isEqualTo(DEFAULT_NRO_TELEFONO);
        assertThat(testTelefono.getTipo()).isEqualTo(DEFAULT_TIPO);
    }

    @Test
    void getAllTelefonos() {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        // Get all the telefonoList
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
            .value(hasItem(telefono.getId().intValue()))
            .jsonPath("$.[*].codigoArea")
            .value(hasItem(DEFAULT_CODIGO_AREA))
            .jsonPath("$.[*].nroTelefono")
            .value(hasItem(DEFAULT_NRO_TELEFONO))
            .jsonPath("$.[*].tipo")
            .value(hasItem(DEFAULT_TIPO));
    }

    @Test
    void getTelefono() {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        // Get the telefono
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, telefono.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(telefono.getId().intValue()))
            .jsonPath("$.codigoArea")
            .value(is(DEFAULT_CODIGO_AREA))
            .jsonPath("$.nroTelefono")
            .value(is(DEFAULT_NRO_TELEFONO))
            .jsonPath("$.tipo")
            .value(is(DEFAULT_TIPO));
    }

    @Test
    void getNonExistingTelefono() {
        // Get the telefono
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTelefono() throws Exception {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();

        // Update the telefono
        Telefono updatedTelefono = telefonoRepository.findById(telefono.getId()).block();
        updatedTelefono.codigoArea(UPDATED_CODIGO_AREA).nroTelefono(UPDATED_NRO_TELEFONO).tipo(UPDATED_TIPO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTelefono.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTelefono))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
        Telefono testTelefono = telefonoList.get(telefonoList.size() - 1);
        assertThat(testTelefono.getCodigoArea()).isEqualTo(UPDATED_CODIGO_AREA);
        assertThat(testTelefono.getNroTelefono()).isEqualTo(UPDATED_NRO_TELEFONO);
        assertThat(testTelefono.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void putNonExistingTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, telefono.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTelefonoWithPatch() throws Exception {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();

        // Update the telefono using partial update
        Telefono partialUpdatedTelefono = new Telefono();
        partialUpdatedTelefono.setId(telefono.getId());

        partialUpdatedTelefono.tipo(UPDATED_TIPO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelefono.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTelefono))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
        Telefono testTelefono = telefonoList.get(telefonoList.size() - 1);
        assertThat(testTelefono.getCodigoArea()).isEqualTo(DEFAULT_CODIGO_AREA);
        assertThat(testTelefono.getNroTelefono()).isEqualTo(DEFAULT_NRO_TELEFONO);
        assertThat(testTelefono.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void fullUpdateTelefonoWithPatch() throws Exception {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();

        // Update the telefono using partial update
        Telefono partialUpdatedTelefono = new Telefono();
        partialUpdatedTelefono.setId(telefono.getId());

        partialUpdatedTelefono.codigoArea(UPDATED_CODIGO_AREA).nroTelefono(UPDATED_NRO_TELEFONO).tipo(UPDATED_TIPO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTelefono.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTelefono))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
        Telefono testTelefono = telefonoList.get(telefonoList.size() - 1);
        assertThat(testTelefono.getCodigoArea()).isEqualTo(UPDATED_CODIGO_AREA);
        assertThat(testTelefono.getNroTelefono()).isEqualTo(UPDATED_NRO_TELEFONO);
        assertThat(testTelefono.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    void patchNonExistingTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, telefono.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTelefono() throws Exception {
        int databaseSizeBeforeUpdate = telefonoRepository.findAll().collectList().block().size();
        telefono.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(telefono))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Telefono in the database
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTelefono() {
        // Initialize the database
        telefonoRepository.save(telefono).block();

        int databaseSizeBeforeDelete = telefonoRepository.findAll().collectList().block().size();

        // Delete the telefono
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, telefono.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Telefono> telefonoList = telefonoRepository.findAll().collectList().block();
        assertThat(telefonoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
