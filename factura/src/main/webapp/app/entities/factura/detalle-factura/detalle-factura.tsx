import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IDetalleFactura } from 'app/shared/model/factura/detalle-factura.model';
import { getEntities } from './detalle-factura.reducer';

export const DetalleFactura = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const detalleFacturaList = useAppSelector(state => state.factura.detalleFactura.entities);
  const loading = useAppSelector(state => state.factura.detalleFactura.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="detalle-factura-heading" data-cy="DetalleFacturaHeading">
        <Translate contentKey="facturaApp.facturaDetalleFactura.home.title">Detalle Facturas</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="facturaApp.facturaDetalleFactura.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/factura/detalle-factura/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="facturaApp.facturaDetalleFactura.home.createLabel">Create new Detalle Factura</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {detalleFacturaList && detalleFacturaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="facturaApp.facturaDetalleFactura.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="facturaApp.facturaDetalleFactura.cantidad">Cantidad</Translate>
                </th>
                <th>
                  <Translate contentKey="facturaApp.facturaDetalleFactura.producto">Producto</Translate>
                </th>
                <th>
                  <Translate contentKey="facturaApp.facturaDetalleFactura.factura">Factura</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {detalleFacturaList.map((detalleFactura, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/factura/detalle-factura/${detalleFactura.id}`} color="link" size="sm">
                      {detalleFactura.id}
                    </Button>
                  </td>
                  <td>{detalleFactura.cantidad}</td>
                  <td>
                    {detalleFactura.producto ? (
                      <Link to={`/factura/producto/${detalleFactura.producto.id}`}>{detalleFactura.producto.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {detalleFactura.factura ? (
                      <Link to={`/factura/factura/${detalleFactura.factura.id}`}>{detalleFactura.factura.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/factura/detalle-factura/${detalleFactura.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/factura/detalle-factura/${detalleFactura.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/factura/detalle-factura/${detalleFactura.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
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
              <Translate contentKey="facturaApp.facturaDetalleFactura.home.notFound">No Detalle Facturas found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default DetalleFactura;
