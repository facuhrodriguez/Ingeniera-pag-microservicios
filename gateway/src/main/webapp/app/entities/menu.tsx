import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/detalle-factura">
        <Translate contentKey="global.menu.entities.detalleFactura" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cliente">
        <Translate contentKey="global.menu.entities.cliente" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/producto">
        <Translate contentKey="global.menu.entities.producto" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/telefono">
        <Translate contentKey="global.menu.entities.telefono" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/factura">
        <Translate contentKey="global.menu.entities.factura" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
