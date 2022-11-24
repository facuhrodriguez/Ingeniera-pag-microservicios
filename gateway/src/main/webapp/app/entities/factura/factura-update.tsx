import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFactura } from 'app/shared/model/factura.model';
import { getEntity, updateEntity, createEntity, reset } from './factura.reducer';

export const FacturaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const facturaEntity = useAppSelector(state => state.gateway.factura.entity);
  const loading = useAppSelector(state => state.gateway.factura.loading);
  const updating = useAppSelector(state => state.gateway.factura.updating);
  const updateSuccess = useAppSelector(state => state.gateway.factura.updateSuccess);

  const handleClose = () => {
    navigate('/factura');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...facturaEntity,
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
          ...facturaEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.factura.home.createOrEditLabel" data-cy="FacturaCreateUpdateHeading">
            <Translate contentKey="gatewayApp.factura.home.createOrEditLabel">Create or edit a Factura</Translate>
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
                  id="factura-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('gatewayApp.factura.fecha')} id="factura-fecha" name="fecha" data-cy="fecha" type="date" />
              <ValidatedField
                label={translate('gatewayApp.factura.totalSinIva')}
                id="factura-totalSinIva"
                name="totalSinIva"
                data-cy="totalSinIva"
                type="text"
              />
              <ValidatedField label={translate('gatewayApp.factura.iva')} id="factura-iva" name="iva" data-cy="iva" type="text" />
              <ValidatedField
                label={translate('gatewayApp.factura.totalConIva')}
                id="factura-totalConIva"
                name="totalConIva"
                data-cy="totalConIva"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/factura" replace color="info">
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

export default FacturaUpdate;
