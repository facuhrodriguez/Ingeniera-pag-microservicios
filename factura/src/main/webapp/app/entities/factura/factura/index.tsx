import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Factura from './factura';
import FacturaDetail from './factura-detail';
import FacturaUpdate from './factura-update';
import FacturaDeleteDialog from './factura-delete-dialog';

const FacturaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Factura />} />
    <Route path="new" element={<FacturaUpdate />} />
    <Route path=":id">
      <Route index element={<FacturaDetail />} />
      <Route path="edit" element={<FacturaUpdate />} />
      <Route path="delete" element={<FacturaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default FacturaRoutes;
