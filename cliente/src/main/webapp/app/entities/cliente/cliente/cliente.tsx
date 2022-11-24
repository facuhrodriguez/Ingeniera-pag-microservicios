import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICliente } from 'app/shared/model/cliente/cliente.model';
import { getEntities } from './cliente.reducer';

export const Cliente = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const clienteList = useAppSelector(state => state.cliente.cliente.entities);
  const loading = useAppSelector(state => state.cliente.cliente.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="cliente-heading" data-cy="ClienteHeading">
        <Translate contentKey="clienteApp.clienteCliente.home.title">Clientes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="clienteApp.clienteCliente.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/cliente/cliente/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="clienteApp.clienteCliente.home.createLabel">Create new Cliente</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {clienteList && clienteList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="clienteApp.clienteCliente.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="clienteApp.clienteCliente.nombre">Nombre</Translate>
                </th>
                <th>
                  <Translate contentKey="clienteApp.clienteCliente.apellido">Apellido</Translate>
                </th>
                <th>
                  <Translate contentKey="clienteApp.clienteCliente.direccion">Direccion</Translate>
                </th>
                <th>
                  <Translate contentKey="clienteApp.clienteCliente.activo">Activo</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {clienteList.map((cliente, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/cliente/cliente/${cliente.id}`} color="link" size="sm">
                      {cliente.id}
                    </Button>
                  </td>
                  <td>{cliente.nombre}</td>
                  <td>{cliente.apellido}</td>
                  <td>{cliente.direccion}</td>
                  <td>{cliente.activo}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/cliente/cliente/${cliente.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/cliente/cliente/${cliente.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/cliente/cliente/${cliente.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="clienteApp.clienteCliente.home.notFound">No Clientes found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Cliente;
