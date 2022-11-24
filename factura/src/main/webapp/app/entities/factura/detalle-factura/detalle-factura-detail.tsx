import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './detalle-factura.reducer';

export const DetalleFacturaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const detalleFacturaEntity = useAppSelector(state => state.factura.detalleFactura.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="detalleFacturaDetailsHeading">
          <Translate contentKey="facturaApp.facturaDetalleFactura.detail.title">DetalleFactura</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{detalleFacturaEntity.id}</dd>
          <dt>
            <span id="cantidad">
              <Translate contentKey="facturaApp.facturaDetalleFactura.cantidad">Cantidad</Translate>
            </span>
          </dt>
          <dd>{detalleFacturaEntity.cantidad}</dd>
          <dt>
            <span id="idProducto">
              <Translate contentKey="facturaApp.facturaDetalleFactura.idProducto">Id Producto</Translate>
            </span>
          </dt>
          <dd>{detalleFacturaEntity.idProducto}</dd>
          <dt>
            <Translate contentKey="facturaApp.facturaDetalleFactura.factura">Factura</Translate>
          </dt>
          <dd>{detalleFacturaEntity.factura ? detalleFacturaEntity.factura.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/factura/detalle-factura" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/factura/detalle-factura/${detalleFacturaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DetalleFacturaDetail;
