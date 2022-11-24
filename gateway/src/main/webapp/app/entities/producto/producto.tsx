import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IProducto } from 'app/shared/model/producto.model';
import { getEntities } from './producto.reducer';

export const Producto = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const productoList = useAppSelector(state => state.gateway.producto.entities);
  const loading = useAppSelector(state => state.gateway.producto.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="producto-heading" data-cy="ProductoHeading">
        <Translate contentKey="gatewayApp.producto.home.title">Productos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.producto.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/producto/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.producto.home.createLabel">Create new Producto</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {productoList && productoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.producto.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.producto.marca">Marca</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.producto.nombre">Nombre</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.producto.descripcion">Descripcion</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.producto.precio">Precio</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.producto.stock">Stock</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {productoList.map((producto, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/producto/${producto.id}`} color="link" size="sm">
                      {producto.id}
                    </Button>
                  </td>
                  <td>{producto.marca}</td>
                  <td>{producto.nombre}</td>
                  <td>{producto.descripcion}</td>
                  <td>{producto.precio}</td>
                  <td>{producto.stock}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/producto/${producto.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/producto/${producto.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/producto/${producto.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gatewayApp.producto.home.notFound">No Productos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Producto;
