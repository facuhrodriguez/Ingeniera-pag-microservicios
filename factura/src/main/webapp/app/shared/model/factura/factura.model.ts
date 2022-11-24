import dayjs from 'dayjs';
import { IDetalleFactura } from 'app/shared/model/factura/detalle-factura.model';

export interface IFactura {
  id?: number;
  fecha?: string | null;
  totalSinIva?: number | null;
  iva?: number | null;
  totalConIva?: number | null;
  detalleFacturas?: IDetalleFactura[] | null;
}

export const defaultValue: Readonly<IFactura> = {};
