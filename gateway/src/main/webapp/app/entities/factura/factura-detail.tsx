import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './factura.reducer';

export const FacturaDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const facturaEntity = useAppSelector(state => state.gateway.factura.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="facturaDetailsHeading">
          <Translate contentKey="gatewayApp.factura.detail.title">Factura</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.id}</dd>
          <dt>
            <span id="fecha">
              <Translate contentKey="gatewayApp.factura.fecha">Fecha</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.fecha ? <TextFormat value={facturaEntity.fecha} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="totalSinIva">
              <Translate contentKey="gatewayApp.factura.totalSinIva">Total Sin Iva</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.totalSinIva}</dd>
          <dt>
            <span id="iva">
              <Translate contentKey="gatewayApp.factura.iva">Iva</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.iva}</dd>
          <dt>
            <span id="totalConIva">
              <Translate contentKey="gatewayApp.factura.totalConIva">Total Con Iva</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.totalConIva}</dd>
          <dt>
            <span id="idCliente">
              <Translate contentKey="gatewayApp.factura.idCliente">Id Cliente</Translate>
            </span>
          </dt>
          <dd>{facturaEntity.idCliente}</dd>
        </dl>
        <Button tag={Link} to="/factura" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/factura/${facturaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FacturaDetail;
