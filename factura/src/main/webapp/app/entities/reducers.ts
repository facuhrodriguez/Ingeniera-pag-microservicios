import detalleFactura from 'app/entities/factura/detalle-factura/detalle-factura.reducer';
import factura from 'app/entities/factura/factura/factura.reducer';
import producto from 'app/entities/factura/producto/producto.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  detalleFactura,
  factura,
  producto,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
