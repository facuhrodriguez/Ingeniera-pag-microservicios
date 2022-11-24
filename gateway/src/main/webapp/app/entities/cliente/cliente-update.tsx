import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITelefono } from 'app/shared/model/telefono.model';
import { getEntities as getTelefonos } from 'app/entities/telefono/telefono.reducer';
import { ICliente } from 'app/shared/model/cliente.model';
import { getEntity, updateEntity, createEntity, reset } from './cliente.reducer';

export const ClienteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const telefonos = useAppSelector(state => state.gateway.telefono.entities);
  const clienteEntity = useAppSelector(state => state.gateway.cliente.entity);
  const loading = useAppSelector(state => state.gateway.cliente.loading);
  const updating = useAppSelector(state => state.gateway.cliente.updating);
  const updateSuccess = useAppSelector(state => state.gateway.cliente.updateSuccess);

  const handleClose = () => {
    navigate('/cliente');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTelefonos({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...clienteEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...clienteEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.cliente.home.createOrEditLabel" data-cy="ClienteCreateUpdateHeading">
            <Translate contentKey="gatewayApp.cliente.home.createOrEditLabel">Create or edit a Cliente</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="cliente-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.cliente.nombre')}
                id="cliente-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.cliente.apellido')}
                id="cliente-apellido"
                name="apellido"
                data-cy="apellido"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.cliente.direccion')}
                id="cliente-direccion"
                name="direccion"
                data-cy="direccion"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.cliente.activo')}
                id="cliente-activo"
                name="activo"
                data-cy="activo"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/cliente" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ClienteUpdate;
