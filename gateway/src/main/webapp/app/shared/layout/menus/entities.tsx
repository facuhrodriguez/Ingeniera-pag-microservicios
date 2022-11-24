import React, { Suspense } from 'react';
import { translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

const EntitiesMenuItems = React.lazy(() => import('app/entities/menu').catch(() => import('app/shared/error/error-loading')));
const FacturaEntitiesMenuItems = React.lazy(() => import('@factura/entities-menu').catch(() => import('app/shared/error/error-loading')));
const ClienteEntitiesMenuItems = React.lazy(() => import('@cliente/entities-menu').catch(() => import('app/shared/error/error-loading')));

export const EntitiesMenu = () => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <Suspense fallback={<div>loading...</div>}>
      <EntitiesMenuItems />
    </Suspense>
    <Suspense fallback={<div>loading...</div>}>
      <FacturaEntitiesMenuItems />
    </Suspense>
    <Suspense fallback={<div>loading...</div>}>
      <ClienteEntitiesMenuItems />
    </Suspense>
  </NavDropdown>
);
