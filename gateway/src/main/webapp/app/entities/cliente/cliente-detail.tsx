import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cliente.reducer';

export const ClienteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clienteEntity = useAppSelector(state => state.gateway.cliente.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clienteDetailsHeading">
          <Translate contentKey="gatewayApp.cliente.detail.title">Cliente</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clienteEntity.id}</dd>
          <dt>
            <span id="nombre">
              <Translate contentKey="gatewayApp.cliente.nombre">Nombre</Translate>
            </span>
          </dt>
          <dd>{clienteEntity.nombre}</dd>
          <dt>
            <span id="apellido">
              <Translate contentKey="gatewayApp.cliente.apellido">Apellido</Translate>
            </span>
          </dt>
          <dd>{clienteEntity.apellido}</dd>
          <dt>
            <span id="direccion">
              <Translate contentKey="gatewayApp.cliente.direccion">Direccion</Translate>
            </span>
          </dt>
          <dd>{clienteEntity.direccion}</dd>
          <dt>
            <span id="activo">
              <Translate contentKey="gatewayApp.cliente.activo">Activo</Translate>
            </span>
          </dt>
          <dd>{clienteEntity.activo}</dd>
        </dl>
        <Button tag={Link} to="/cliente" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cliente/${clienteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClienteDetail;
