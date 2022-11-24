package com.ingenieria.gateway.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.ingenieria.gateway.domain.Telefono;
import com.ingenieria.gateway.repository.rowmapper.ClienteRowMapper;
import com.ingenieria.gateway.repository.rowmapper.TelefonoRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Telefono entity.
 */
@SuppressWarnings("unused")
class TelefonoRepositoryInternalImpl extends SimpleR2dbcRepository<Telefono, Long> implements TelefonoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ClienteRowMapper clienteMapper;
    private final TelefonoRowMapper telefonoMapper;

    private static final Table entityTable = Table.aliased("telefono", EntityManager.ENTITY_ALIAS);
    private static final Table clienteTable = Table.aliased("cliente", "cliente");

    public TelefonoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ClienteRowMapper clienteMapper,
        TelefonoRowMapper telefonoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Telefono.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.clienteMapper = clienteMapper;
        this.telefonoMapper = telefonoMapper;
    }

    @Override
    public Flux<Telefono> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Telefono> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TelefonoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ClienteSqlHelper.getColumns(clienteTable, "cliente"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(clienteTable)
            .on(Column.create("cliente_id", entityTable))
            .equals(Column.create("id", clienteTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Telefono.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Telefono> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Telefono> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Telefono process(Row row, RowMetadata metadata) {
        Telefono entity = telefonoMapper.apply(row, "e");
        entity.setCliente(clienteMapper.apply(row, "cliente"));
        return entity;
    }

    @Override
    public <S extends Telefono> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
