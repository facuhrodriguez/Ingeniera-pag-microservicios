import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cliente from './cliente';
import ClienteDetail from './cliente-detail';
import ClienteUpdate from './cliente-update';
import ClienteDeleteDialog from './cliente-delete-dialog';

const ClienteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Cliente />} />
    <Route path="new" element={<ClienteUpdate />} />
    <Route path=":id">
      <Route index element={<ClienteDetail />} />
      <Route path="edit" element={<ClienteUpdate />} />
      <Route path="delete" element={<ClienteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClienteRoutes;
