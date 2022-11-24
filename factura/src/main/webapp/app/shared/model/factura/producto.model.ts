import { IDetalleFactura } from 'app/shared/model/factura/detalle-factura.model';

export interface IProducto {
  id?: number;
  marca?: string | null;
  nombre?: string | null;
  descripcion?: string | null;
  precio?: number | null;
  stock?: number | null;
  detalleFacturas?: IDetalleFactura[] | null;
}

export const defaultValue: Readonly<IProducto> = {};
