import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IProducto } from 'app/shared/model/producto/producto.model';
import { getEntity, updateEntity, createEntity, reset } from './producto.reducer';

export const ProductoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const productoEntity = useAppSelector(state => state.producto.producto.entity);
  const loading = useAppSelector(state => state.producto.producto.loading);
  const updating = useAppSelector(state => state.producto.producto.updating);
  const updateSuccess = useAppSelector(state => state.producto.producto.updateSuccess);

  const handleClose = () => {
    navigate('/producto/producto');
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
      ...productoEntity,
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
          ...productoEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="productoApp.productoProducto.home.createOrEditLabel" data-cy="ProductoCreateUpdateHeading">
            <Translate contentKey="productoApp.productoProducto.home.createOrEditLabel">Create or edit a Producto</Translate>
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
                  id="producto-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('productoApp.productoProducto.marca')}
                id="producto-marca"
                name="marca"
                data-cy="marca"
                type="text"
              />
              <ValidatedField
                label={translate('productoApp.productoProducto.nombre')}
                id="producto-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
              />
              <ValidatedField
                label={translate('productoApp.productoProducto.descripcion')}
                id="producto-descripcion"
                name="descripcion"
                data-cy="descripcion"
                type="text"
              />
              <ValidatedField
                label={translate('productoApp.productoProducto.precio')}
                id="producto-precio"
                name="precio"
                data-cy="precio"
                type="text"
              />
              <ValidatedField
                label={translate('productoApp.productoProducto.stock')}
                id="producto-stock"
                name="stock"
                data-cy="stock"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/producto/producto" replace color="info">
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

export default ProductoUpdate;
