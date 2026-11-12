-- Migración para las funcionalidades de evaluaciones.
-- Ejecutar una sola vez sobre una base con el esquema anterior.
-- No ejecutar si las columnas ya existen.

ALTER TABLE EDICIONCURSO
ADD COLUMN CERRADA TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE INSCRIPCION
ADD COLUMN CALIFICACION INT NULL DEFAULT NULL,
ADD COLUMN FECHA_CALIFICACION DATE NULL DEFAULT NULL;
