import detalleFactura from 'app/entities/detalle-factura/detalle-factura.reducer';
import cliente from 'app/entities/cliente/cliente.reducer';
import producto from 'app/entities/producto/producto.reducer';
import telefono from 'app/entities/telefono/telefono.reducer';
import factura from 'app/entities/factura/factura.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  detalleFactura,
  cliente,
  producto,
  telefono,
  factura,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
