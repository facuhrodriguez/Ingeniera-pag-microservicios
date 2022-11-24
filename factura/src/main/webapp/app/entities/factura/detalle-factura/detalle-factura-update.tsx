import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IProducto } from 'app/shared/model/factura/producto.model';
import { getEntities as getProductos } from 'app/entities/factura/producto/producto.reducer';
import { IFactura } from 'app/shared/model/factura/factura.model';
import { getEntities as getFacturas } from 'app/entities/factura/factura/factura.reducer';
import { IDetalleFactura } from 'app/shared/model/factura/detalle-factura.model';
import { getEntity, updateEntity, createEntity, reset } from './detalle-factura.reducer';

export const DetalleFacturaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productos = useAppSelector(state => state.factura.producto.entities);
  const facturas = useAppSelector(state => state.factura.factura.entities);
  const detalleFacturaEntity = useAppSelector(state => state.factura.detalleFactura.entity);
  const loading = useAppSelector(state => state.factura.detalleFactura.loading);
  const updating = useAppSelector(state => state.factura.detalleFactura.updating);
  const updateSuccess = useAppSelector(state => state.factura.detalleFactura.updateSuccess);

  const handleClose = () => {
    navigate('/factura/detalle-factura');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProductos({}));
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
      producto: productos.find(it => it.id.toString() === values.producto.toString()),
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
          producto: detalleFacturaEntity?.producto?.id,
          factura: detalleFacturaEntity?.factura?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="facturaApp.facturaDetalleFactura.home.createOrEditLabel" data-cy="DetalleFacturaCreateUpdateHeading">
            <Translate contentKey="facturaApp.facturaDetalleFactura.home.createOrEditLabel">Create or edit a DetalleFactura</Translate>
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
                label={translate('facturaApp.facturaDetalleFactura.cantidad')}
                id="detalle-factura-cantidad"
                name="cantidad"
                data-cy="cantidad"
                type="text"
              />
              <ValidatedField
                id="detalle-factura-producto"
                name="producto"
                data-cy="producto"
                label={translate('facturaApp.facturaDetalleFactura.producto')}
                type="select"
              >
                <option value="" key="0" />
                {productos
                  ? productos.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="detalle-factura-factura"
                name="factura"
                data-cy="factura"
                label={translate('facturaApp.facturaDetalleFactura.factura')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/factura/detalle-factura" replace color="info">
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
