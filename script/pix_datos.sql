-- datos de prueba del proyecto pix
-- correr DESPUES de pix.sql (necesita que las tablas ya existan)
-- cumple con "al menos 15 datos de cada clase": cada tabla tiene 15 registros activos
-- y ademas 1 registro dado de baja logica (activo = 0) para poder probar esa funcionalidad
-- OJO: este script borra el contenido actual de las tablas y lo reemplaza por los datos de prueba
-- las imagenes (.pix) de las obras van en la carpeta imagenes/ del proyecto (obra_01.pix ... obra_16.pix)
-- todos los usuarios tienen password 1234
use pix;

delete from colecciones_obras;
delete from tags_publicaciones;
delete from comentarios;
delete from colecciones;
delete from publicaciones;
delete from obras;
delete from usuarios;
alter table usuarios auto_increment = 1;
alter table obras auto_increment = 1;
alter table publicaciones auto_increment = 1;
alter table comentarios auto_increment = 1;
alter table colecciones auto_increment = 1;

-- usuarios (3 administradores, 12 artistas activos y 1 artista dado de baja)
insert into usuarios (id, nombre, email, password, rol, bio, nivel_acceso, activo, fecha_registro) values
(1, 'admin', 'admin@pix.com', '1234', 'ADMINISTRADOR', null, 2, 1, '2026-08-01'),
(2, 'Lucia Fernandez', 'lucia.admin@pix.com', '1234', 'ADMINISTRADOR', null, 1, 1, '2026-08-03'),
(3, 'Martin Gomez', 'martin.admin@pix.com', '1234', 'ADMINISTRADOR', null, 1, 1, '2026-08-05'),
(4, 'Valentina Rios', 'valentina@mail.com', '1234', 'ARTISTA', 'Pixel art de paisajes y atardeceres.', null, 1, '2026-08-10'),
(5, 'Joaquin Perez', 'joaquin@mail.com', '1234', 'ARTISTA', 'Me gustan los sprites retro de 8 bits.', null, 1, '2026-08-12'),
(6, 'Camila Sosa', 'camila@mail.com', '1234', 'ARTISTA', 'Dibujo flores, animales y cosas lindas.', null, 1, '2026-08-15'),
(7, 'Santiago Ruiz', 'santiago@mail.com', '1234', 'ARTISTA', 'Ciencia ficcion y espacio en 20x20.', null, 1, '2026-08-18'),
(8, 'Martina Lopez', 'martina@mail.com', '1234', 'ARTISTA', 'Ilustradora, ahora probando el pixel art.', null, 1, '2026-08-20'),
(9, 'Facundo Diaz', 'facundo@mail.com', '1234', 'ARTISTA', 'Coleccionista de pixeles y de cafe.', null, 1, '2026-08-22'),
(10, 'Sofia Castro', 'sofia@mail.com', '1234', 'ARTISTA', 'Paletas de colores y mucho arcoiris.', null, 1, '2026-08-25'),
(11, 'Tomas Herrera', 'tomas@mail.com', '1234', 'ARTISTA', 'Ciudades de noche y luces de neon.', null, 1, '2026-08-28'),
(12, 'Agustina Molina', 'agustina@mail.com', '1234', 'ARTISTA', 'Gatos, perros y mas gatos.', null, 1, '2026-09-01'),
(13, 'Nicolas Vega', 'nicolas@mail.com', '1234', 'ARTISTA', 'Juegos clasicos y arte minimalista.', null, 1, '2026-09-04'),
(14, 'Julieta Navarro', 'julieta@mail.com', '1234', 'ARTISTA', 'Naturaleza y montanas.', null, 1, '2026-09-07'),
(15, 'Bruno Acosta', 'bruno@mail.com', '1234', 'ARTISTA', 'Aprendiendo a dibujar pixel por pixel.', null, 1, '2026-09-10'),
(16, 'Usuario Baja', 'baja@mail.com', '1234', 'ARTISTA', 'Cuenta dada de baja (para probar la baja logica).', null, 0, '2026-09-12');

-- obras (la imagen es un archivo .pix, aca se guarda solo la ruta)
insert into obras (id, id_artista, titulo, descripcion, ruta_archivo, fecha_publicacion) values
(1, 4, 'Atardecer en la playa', 'Un sol que se esconde en el mar.', 'imagenes/obra_01.pix', '2026-09-01 18:30:00'),
(2, 5, 'Corazon retro', 'Corazon clasico de videojuego.', 'imagenes/obra_02.pix', '2026-09-02 12:10:00'),
(3, 6, 'Sonrisa', 'Una carita feliz para empezar el dia.', 'imagenes/obra_03.pix', '2026-09-03 09:45:00'),
(4, 14, 'Casita del bosque', 'La casa de mis suenios.', 'imagenes/obra_04.pix', '2026-09-04 16:20:00'),
(5, 14, 'Arbol solitario', 'Un arbol en medio del campo.', 'imagenes/obra_05.pix', '2026-09-05 11:00:00'),
(6, 11, 'Luna de medianoche', 'Luna creciente con estrellas.', 'imagenes/obra_06.pix', '2026-09-06 23:15:00'),
(7, 14, 'Montanas nevadas', 'Dos picos con nieve en la cima.', 'imagenes/obra_07.pix', '2026-09-07 08:05:00'),
(8, 6, 'Flor rosa', 'Una flor que encontre en el jardin.', 'imagenes/obra_08.pix', '2026-09-08 15:40:00'),
(9, 10, 'Pez naranja', 'Un pez nadando en el mar profundo.', 'imagenes/obra_09.pix', '2026-09-09 10:25:00'),
(10, 7, 'Rumbo a las estrellas', 'Cohete listo para despegar.', 'imagenes/obra_10.pix', '2026-09-10 19:50:00'),
(11, 13, 'Invasor', 'Homenaje a los arcades de los 80.', 'imagenes/obra_11.pix', '2026-09-11 14:30:00'),
(12, 9, 'Cafe de la manana', 'Lo primero que hago al despertar.', 'imagenes/obra_12.pix', '2026-09-12 07:55:00'),
(13, 13, 'Tablero', 'Un tablero de ajedrez con una pieza roja.', 'imagenes/obra_13.pix', '2026-09-13 20:10:00'),
(14, 10, 'Despues de la lluvia', 'Arcoiris sobre el pasto.', 'imagenes/obra_14.pix', '2026-09-14 17:00:00'),
(15, 12, 'Gato negro', 'Mi gato Oliver en pixeles.', 'imagenes/obra_15.pix', '2026-09-15 13:35:00'),
(16, 4, 'Ciudad nocturna', 'Obra dada de baja (para probar la baja logica).', 'imagenes/obra_16.pix', '2026-09-16 22:00:00');

-- publicaciones (una por obra, la 16 esta dada de baja)
insert into publicaciones (id, id_obra, activo) values
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1),
(7, 7, 1),
(8, 8, 1),
(9, 9, 1),
(10, 10, 1),
(11, 11, 1),
(12, 12, 1),
(13, 13, 1),
(14, 14, 1),
(15, 15, 1),
(16, 16, 0);

-- tags de cada publicacion (en minuscula y sin #, como los guarda el service)
insert into tags_publicaciones (id_publicacion, tag) values
(1, 'paisaje'),
(1, 'atardecer'),
(1, 'playa'),
(2, 'retro'),
(2, 'corazon'),
(2, '8bit'),
(3, 'carita'),
(3, 'amarillo'),
(4, 'casa'),
(4, 'bosque'),
(4, 'paisaje'),
(5, 'arbol'),
(5, 'naturaleza'),
(5, 'paisaje'),
(6, 'luna'),
(6, 'noche'),
(6, 'estrellas'),
(7, 'montanas'),
(7, 'nieve'),
(7, 'paisaje'),
(8, 'flor'),
(8, 'naturaleza'),
(9, 'pez'),
(9, 'mar'),
(9, 'animales'),
(10, 'espacio'),
(10, 'cohete'),
(10, 'estrellas'),
(11, 'retro'),
(11, 'arcade'),
(11, '8bit'),
(12, 'cafe'),
(12, 'comida'),
(13, 'ajedrez'),
(13, 'minimalista'),
(14, 'arcoiris'),
(14, 'lluvia'),
(14, 'paisaje'),
(15, 'gato'),
(15, 'animales'),
(16, 'ciudad'),
(16, 'noche');

-- comentarios (el 16 esta dado de baja)
insert into comentarios (id, id_obra, id_usuario, contenido, fecha, activo) values
(1, 1, 5, 'Que colores tan lindos, me encanto el sol!', '2026-09-01 19:00:00', 1),
(2, 1, 8, 'Parece una foto de mis vacaciones.', '2026-09-01 20:15:00', 1),
(3, 2, 4, 'El mejor corazon que vi por aca.', '2026-09-02 13:00:00', 1),
(4, 3, 9, 'Me sacaste una sonrisa :)', '2026-09-03 10:30:00', 1),
(5, 4, 12, 'Quiero vivir ahi!', '2026-09-04 17:45:00', 1),
(6, 6, 7, 'La luna quedo buenisima.', '2026-09-07 00:10:00', 1),
(7, 7, 15, 'Las montanas con nieve estan geniales.', '2026-09-07 09:20:00', 1),
(8, 8, 10, 'Que flor mas delicada.', '2026-09-08 16:30:00', 1),
(9, 9, 6, 'Me encanta el naranja del pez.', '2026-09-09 11:00:00', 1),
(10, 10, 11, 'Cuenta regresiva... 3, 2, 1!', '2026-09-10 20:30:00', 1),
(11, 11, 5, 'Los arcades nunca mueren.', '2026-09-11 15:10:00', 1),
(12, 12, 13, 'Yo tambien necesito mi cafe.', '2026-09-12 08:40:00', 1),
(13, 14, 4, 'El arcoiris tiene todos los colores.', '2026-09-14 18:05:00', 1),
(14, 15, 14, 'Oliver es hermoso!', '2026-09-15 14:00:00', 1),
(15, 5, 9, 'Simple y muy bien logrado.', '2026-09-05 12:20:00', 1),
(16, 2, 15, 'Comentario inapropiado dado de baja (prueba de baja logica).', '2026-09-02 14:00:00', 0);

-- colecciones (la 16 esta dada de baja)
insert into colecciones (id, id_usuario, nombre, descripcion, activo) values
(1, 4, 'Atardeceres', 'Los mejores cielos de la tarde.', 1),
(2, 5, 'Retro 8 bits', 'Todo lo que parece sacado de un arcade.', 1),
(3, 6, 'Naturaleza', 'Flores, arboles y bichos.', 1),
(4, 7, 'Cielo y espacio', 'Lunas, estrellas y cohetes.', 1),
(5, 8, 'Favoritas', 'Mis obras preferidas del sitio.', 1),
(6, 9, 'Para inspirarme', 'Referencias para mis proximas obras.', 1),
(7, 10, 'Colores vivos', 'Obras con paletas muy coloridas.', 1),
(8, 11, 'Noche', 'Todo lo que pasa cuando se va el sol.', 1),
(9, 12, 'Animales', 'Gatos, peces y otros amigos.', 1),
(10, 13, 'Minimalismo', 'Pocas formas, mucho estilo.', 1),
(11, 14, 'Paisajes', 'Montanas, bosques y playas.', 1),
(12, 15, 'Para aprender', 'Obras para estudiar tecnicas.', 1),
(13, 4, 'Caritas y corazones', 'Obras tiernas.', 1),
(14, 9, 'Cosas de todos los dias', 'Cafe y mas cafe.', 1),
(15, 12, 'Mis gatos', 'Colecciones de gatos pixelados.', 1),
(16, 5, 'Coleccion borrada', 'Coleccion dada de baja (prueba de baja logica).', 0);

-- obras dentro de cada coleccion (tabla que une colecciones con obras)
insert into colecciones_obras (id_coleccion, id_obra) values
(1, 1),
(1, 14),
(2, 2),
(2, 11),
(3, 5),
(3, 8),
(4, 6),
(4, 10),
(5, 1),
(5, 3),
(5, 9),
(6, 12),
(6, 13),
(7, 14),
(7, 8),
(8, 6),
(8, 16),
(9, 9),
(9, 15),
(10, 13),
(10, 2),
(11, 4),
(11, 7),
(11, 5),
(12, 3),
(12, 11),
(13, 2),
(13, 3),
(14, 12),
(15, 15),
(16, 1);
