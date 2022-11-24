import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Telefono from './telefono';
import TelefonoDetail from './telefono-detail';
import TelefonoUpdate from './telefono-update';
import TelefonoDeleteDialog from './telefono-delete-dialog';

const TelefonoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Telefono />} />
    <Route path="new" element={<TelefonoUpdate />} />
    <Route path=":id">
      <Route index element={<TelefonoDetail />} />
      <Route path="edit" element={<TelefonoUpdate />} />
      <Route path="delete" element={<TelefonoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TelefonoRoutes;
