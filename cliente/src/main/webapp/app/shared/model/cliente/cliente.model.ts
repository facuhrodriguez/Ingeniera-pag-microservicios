import { ITelefono } from 'app/shared/model/cliente/telefono.model';

export interface ICliente {
  id?: string;
  nombre?: string | null;
  apellido?: string | null;
  direccion?: string | null;
  activo?: number | null;
  telefono?: ITelefono | null;
}

export const defaultValue: Readonly<ICliente> = {};
