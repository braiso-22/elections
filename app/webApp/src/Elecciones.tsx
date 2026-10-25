import { useEffect, useState } from 'react';
import type { EleccionResumenDto } from 'shared';

// Las clases exportadas por Kotlin/JS tipan los campos, pero fetch devuelve objetos planos:
// los enum llegan como string y no hay métodos (copy, equals…). Usa solo los campos.
export function Elecciones() {
  const [elecciones, setElecciones] = useState<EleccionResumenDto[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch('/v1/elecciones')
      .then((respuesta) => respuesta.json())
      .then((json) => setElecciones(json as EleccionResumenDto[]))
      .catch((e: unknown) => setError(String(e)));
  }, []);

  if (error) return <p>No se pudieron cargar las elecciones: {error}</p>;
  if (!elecciones) return <p>Cargando…</p>;
  if (elecciones.length === 0) return <p>No hay elecciones convocadas.</p>;

  return (
    <ul>
      {elecciones.map((eleccion) => (
        <li key={eleccion.id}>{eleccion.nombre}</li>
      ))}
    </ul>
  );
}
