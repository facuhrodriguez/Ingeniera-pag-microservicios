package com.ingenieria.factura.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.ingenieria.factura.domain.DetalleFactura;
import com.ingenieria.factura.repository.rowmapper.DetalleFacturaRowMapper;
import com.ingenieria.factura.repository.rowmapper.FacturaRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the DetalleFactura entity.
 */
@SuppressWarnings("unused")
class DetalleFacturaRepositoryInternalImpl extends SimpleR2dbcRepository<DetalleFactura, Long> implements DetalleFacturaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FacturaRowMapper facturaMapper;
    private final DetalleFacturaRowMapper detallefacturaMapper;

    private static final Table entityTable = Table.aliased("detalle_factura", EntityManager.ENTITY_ALIAS);
    private static final Table facturaTable = Table.aliased("factura", "factura");

    public DetalleFacturaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FacturaRowMapper facturaMapper,
        DetalleFacturaRowMapper detallefacturaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(DetalleFactura.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.facturaMapper = facturaMapper;
        this.detallefacturaMapper = detallefacturaMapper;
    }

    @Override
    public Flux<DetalleFactura> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<DetalleFactura> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DetalleFacturaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(FacturaSqlHelper.getColumns(facturaTable, "factura"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(facturaTable)
            .on(Column.create("factura_id", entityTable))
            .equals(Column.create("id", facturaTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, DetalleFactura.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<DetalleFactura> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<DetalleFactura> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private DetalleFactura process(Row row, RowMetadata metadata) {
        DetalleFactura entity = detallefacturaMapper.apply(row, "e");
        entity.setFactura(facturaMapper.apply(row, "factura"));
        return entity;
    }

    @Override
    public <S extends DetalleFactura> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
