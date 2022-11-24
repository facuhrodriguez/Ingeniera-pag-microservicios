import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFactura } from 'app/shared/model/factura.model';
import { getEntities as getFacturas } from 'app/entities/factura/factura.reducer';
import { IDetalleFactura } from 'app/shared/model/detalle-factura.model';
import { getEntity, updateEntity, createEntity, reset } from './detalle-factura.reducer';

export const DetalleFacturaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const facturas = useAppSelector(state => state.gateway.factura.entities);
  const detalleFacturaEntity = useAppSelector(state => state.gateway.detalleFactura.entity);
  const loading = useAppSelector(state => state.gateway.detalleFactura.loading);
  const updating = useAppSelector(state => state.gateway.detalleFactura.updating);
  const updateSuccess = useAppSelector(state => state.gateway.detalleFactura.updateSuccess);

  const handleClose = () => {
    navigate('/detalle-factura');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getFacturas({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...detalleFacturaEntity,
      ...values,
      factura: facturas.find(it => it.id.toString() === values.factura.toString()),
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
          ...detalleFacturaEntity,
          factura: detalleFacturaEntity?.factura?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.detalleFactura.home.createOrEditLabel" data-cy="DetalleFacturaCreateUpdateHeading">
            <Translate contentKey="gatewayApp.detalleFactura.home.createOrEditLabel">Create or edit a DetalleFactura</Translate>
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
                  id="detalle-factura-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.detalleFactura.cantidad')}
                id="detalle-factura-cantidad"
                name="cantidad"
                data-cy="cantidad"
                type="text"
              />
              <ValidatedField
                label={translate('gatewayApp.detalleFactura.idProducto')}
                id="detalle-factura-idProducto"
                name="idProducto"
                data-cy="idProducto"
                type="text"
              />
              <ValidatedField
                id="detalle-factura-factura"
                name="factura"
                data-cy="factura"
                label={translate('gatewayApp.detalleFactura.factura')}
                type="select"
              >
                <option value="" key="0" />
                {facturas
                  ? facturas.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/detalle-factura" replace color="info">
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

export default DetalleFacturaUpdate;
