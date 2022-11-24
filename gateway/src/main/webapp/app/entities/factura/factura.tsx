import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IFactura } from 'app/shared/model/factura.model';
import { getEntities } from './factura.reducer';

export const Factura = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const facturaList = useAppSelector(state => state.gateway.factura.entities);
  const loading = useAppSelector(state => state.gateway.factura.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="factura-heading" data-cy="FacturaHeading">
        <Translate contentKey="gatewayApp.factura.home.title">Facturas</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.factura.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/factura/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.factura.home.createLabel">Create new Factura</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {facturaList && facturaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.factura.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.factura.fecha">Fecha</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.factura.totalSinIva">Total Sin Iva</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.factura.iva">Iva</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.factura.totalConIva">Total Con Iva</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {facturaList.map((factura, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/factura/${factura.id}`} color="link" size="sm">
                      {factura.id}
                    </Button>
                  </td>
                  <td>{factura.fecha ? <TextFormat type="date" value={factura.fecha} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{factura.totalSinIva}</td>
                  <td>{factura.iva}</td>
                  <td>{factura.totalConIva}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/factura/${factura.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/factura/${factura.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/factura/${factura.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="gatewayApp.factura.home.notFound">No Facturas found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Factura;
