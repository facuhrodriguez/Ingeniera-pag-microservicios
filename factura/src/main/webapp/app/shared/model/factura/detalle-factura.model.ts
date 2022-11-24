import { IProducto } from 'app/shared/model/factura/producto.model';
import { IFactura } from 'app/shared/model/factura/factura.model';

export interface IDetalleFactura {
  id?: number;
  cantidad?: number | null;
  producto?: IProducto | null;
  factura?: IFactura | null;
}

export const defaultValue: Readonly<IDetalleFactura> = {};
