import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICliente } from 'app/shared/model/cliente.model';
import { getEntities as getClientes } from 'app/entities/cliente/cliente.reducer';
import { ITelefono } from 'app/shared/model/telefono.model';
import { getEntity, updateEntity, createEntity, reset } from './telefono.reducer';

export const TelefonoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clientes = useAppSelector(state => state.gateway.cliente.entities);
  const telefonoEntity = useAppSelector(state => state.gateway.telefono.entity);
  const loading = useAppSelector(state => state.gateway.telefono.loading);
  const updating = useAppSelector(state => state.gateway.telefono.updating);
  const updateSuccess = useAppSelector(state => state.gateway.telefono.updateSuccess);

  const handleClose = () => {
    navigate('/telefono');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getClientes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...telefonoEntity,
      ...values,
      cliente: clientes.find(it => it.id.toString() === values.cliente.toString()),
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
          ...telefonoEntity,
          cliente: telefonoEntity?.cliente?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.telefono.home.createOrEditLabel" data-cy="TelefonoCreateUpdateHeading">
            <Translate contentKey="gatewayApp.telefono.home.createOrEditLabel">Create or edit a Telefono</Translate>
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
                  id="telefono-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.telefono.codigoArea')}
                id="telefono-codigoArea"
                name="codigoArea"
                data-cy="codigoArea"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.telefono.nroTelefono')}
                id="telefono-nroTelefono"
                name="nroTelefono"
                data-cy="nroTelefono"
                type="text"
              />
              <ValidatedField label={translate('gatewayApp.telefono.tipo')} id="telefono-tipo" name="tipo" data-cy="tipo" type="text" />
              <ValidatedField
                id="telefono-cliente"
                name="cliente"
                data-cy="cliente"
                label={translate('gatewayApp.telefono.cliente')}
                type="select"
              >
                <option value="" key="0" />
                {clientes
                  ? clientes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/telefono" replace color="info">
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

export default TelefonoUpdate;
