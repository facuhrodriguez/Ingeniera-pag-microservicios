import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './telefono.reducer';

export const TelefonoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const telefonoEntity = useAppSelector(state => state.cliente.telefono.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="telefonoDetailsHeading">
          <Translate contentKey="clienteApp.clienteTelefono.detail.title">Telefono</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{telefonoEntity.id}</dd>
          <dt>
            <span id="codigoArea">
              <Translate contentKey="clienteApp.clienteTelefono.codigoArea">Codigo Area</Translate>
            </span>
          </dt>
          <dd>{telefonoEntity.codigoArea}</dd>
          <dt>
            <span id="nroTelefono">
              <Translate contentKey="clienteApp.clienteTelefono.nroTelefono">Nro Telefono</Translate>
            </span>
          </dt>
          <dd>{telefonoEntity.nroTelefono}</dd>
          <dt>
            <span id="tipo">
              <Translate contentKey="clienteApp.clienteTelefono.tipo">Tipo</Translate>
            </span>
          </dt>
          <dd>{telefonoEntity.tipo}</dd>
          <dt>
            <Translate contentKey="clienteApp.clienteTelefono.cliente">Cliente</Translate>
          </dt>
          <dd>{telefonoEntity.cliente ? telefonoEntity.cliente.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/cliente/telefono" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cliente/telefono/${telefonoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TelefonoDetail;
