-- Ejecutar una vez sobre una base existente antes de usar el nuevo JAR.
-- Los usuarios anteriores conservan sus datos y pueden recibir una clave desde Swing.
ALTER TABLE usuario ADD COLUMN PASSWORD_HASH VARCHAR(255) NULL;
