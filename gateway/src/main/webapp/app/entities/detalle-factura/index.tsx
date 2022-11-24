import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DetalleFactura from './detalle-factura';
import DetalleFacturaDetail from './detalle-factura-detail';
import DetalleFacturaUpdate from './detalle-factura-update';
import DetalleFacturaDeleteDialog from './detalle-factura-delete-dialog';

const DetalleFacturaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DetalleFactura />} />
    <Route path="new" element={<DetalleFacturaUpdate />} />
    <Route path=":id">
      <Route index element={<DetalleFacturaDetail />} />
      <Route path="edit" element={<DetalleFacturaUpdate />} />
      <Route path="delete" element={<DetalleFacturaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DetalleFacturaRoutes;
