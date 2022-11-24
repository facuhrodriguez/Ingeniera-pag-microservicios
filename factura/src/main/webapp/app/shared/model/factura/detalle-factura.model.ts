import { IFactura } from 'app/shared/model/factura/factura.model';

export interface IDetalleFactura {
  id?: number;
  cantidad?: number | null;
  idProducto?: number | null;
  factura?: IFactura | null;
}

export const defaultValue: Readonly<IDetalleFactura> = {};
