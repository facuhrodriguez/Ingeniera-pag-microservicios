import { ITelefono } from 'app/shared/model/telefono.model';

export interface ICliente {
  id?: number;
  nombre?: string | null;
  apellido?: string | null;
  direccion?: string | null;
  activo?: number | null;
  telefono?: ITelefono | null;
}

export const defaultValue: Readonly<ICliente> = {};
