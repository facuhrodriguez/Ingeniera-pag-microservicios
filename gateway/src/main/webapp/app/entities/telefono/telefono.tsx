import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ITelefono } from 'app/shared/model/telefono.model';
import { getEntities } from './telefono.reducer';

export const Telefono = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const telefonoList = useAppSelector(state => state.gateway.telefono.entities);
  const loading = useAppSelector(state => state.gateway.telefono.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="telefono-heading" data-cy="TelefonoHeading">
        <Translate contentKey="gatewayApp.telefono.home.title">Telefonos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.telefono.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/telefono/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.telefono.home.createLabel">Create new Telefono</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {telefonoList && telefonoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.telefono.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.telefono.codigoArea">Codigo Area</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.telefono.nroTelefono">Nro Telefono</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.telefono.tipo">Tipo</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.telefono.cliente">Cliente</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {telefonoList.map((telefono, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/telefono/${telefono.id}`} color="link" size="sm">
                      {telefono.id}
                    </Button>
                  </td>
                  <td>{telefono.codigoArea}</td>
                  <td>{telefono.nroTelefono}</td>
                  <td>{telefono.tipo}</td>
                  <td>{telefono.cliente ? <Link to={`/cliente/${telefono.cliente.id}`}>{telefono.cliente.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/telefono/${telefono.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/telefono/${telefono.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/telefono/${telefono.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="gatewayApp.telefono.home.notFound">No Telefonos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Telefono;
