import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './producto.reducer';

export const ProductoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const productoEntity = useAppSelector(state => state.factura.producto.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productoDetailsHeading">
          <Translate contentKey="facturaApp.facturaProducto.detail.title">Producto</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productoEntity.id}</dd>
          <dt>
            <span id="marca">
              <Translate contentKey="facturaApp.facturaProducto.marca">Marca</Translate>
            </span>
          </dt>
          <dd>{productoEntity.marca}</dd>
          <dt>
            <span id="nombre">
              <Translate contentKey="facturaApp.facturaProducto.nombre">Nombre</Translate>
            </span>
          </dt>
          <dd>{productoEntity.nombre}</dd>
          <dt>
            <span id="descripcion">
              <Translate contentKey="facturaApp.facturaProducto.descripcion">Descripcion</Translate>
            </span>
          </dt>
          <dd>{productoEntity.descripcion}</dd>
          <dt>
            <span id="precio">
              <Translate contentKey="facturaApp.facturaProducto.precio">Precio</Translate>
            </span>
          </dt>
          <dd>{productoEntity.precio}</dd>
          <dt>
            <span id="stock">
              <Translate contentKey="facturaApp.facturaProducto.stock">Stock</Translate>
            </span>
          </dt>
          <dd>{productoEntity.stock}</dd>
        </dl>
        <Button tag={Link} to="/factura/producto" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/factura/producto/${productoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductoDetail;
