export interface IProducto {
  id?: number;
  marca?: string | null;
  nombre?: string | null;
  descripcion?: string | null;
  precio?: number | null;
  stock?: number | null;
}

export const defaultValue: Readonly<IProducto> = {};
