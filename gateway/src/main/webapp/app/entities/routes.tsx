import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';

import entitiesReducers from './reducers';

import DetalleFactura from './detalle-factura';
import Cliente from './cliente';
import Producto from './producto';
import Telefono from './telefono';
import Factura from './factura';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const store = getStore();
  store.injectReducer('gateway', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="detalle-factura/*" element={<DetalleFactura />} />
        <Route path="cliente/*" element={<Cliente />} />
        <Route path="producto/*" element={<Producto />} />
        <Route path="telefono/*" element={<Telefono />} />
        <Route path="factura/*" element={<Factura />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
