import { ICliente } from 'app/shared/model/cliente.model';

export interface ITelefono {
  id?: number;
  codigoArea?: number | null;
  nroTelefono?: number | null;
  tipo?: string | null;
  cliente?: ICliente | null;
}

export const defaultValue: Readonly<ITelefono> = {};
