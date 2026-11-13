-- base de datos del proyecto pix. correr este script en mysql workbench antes de usar la app
create database if not exists pix;
use pix;

-- usuarios: la bio es solo de artistas y el nivel_acceso solo de administradores
create table if not exists usuarios (
    id int auto_increment primary key,
    nombre varchar(100) not null,
    email varchar(150) not null unique,
    password varchar(100) not null,
    rol varchar(20) not null,
    bio varchar(500) null,
    nivel_acceso int null,
    activo tinyint(1) not null default 1,
    fecha_registro date not null
);

-- obras: la imagen no va en la base, solo la ruta del archivo .pix
create table if not exists obras (
    id int auto_increment primary key,
    id_artista int not null,
    titulo varchar(200) not null,
    descripcion varchar(500),
    ruta_archivo varchar(300) not null,
    fecha_publicacion datetime not null
);

-- publicaciones: lo que se ve en el feed
create table if not exists publicaciones (
    id int auto_increment primary key,
    id_obra int not null,
    activo tinyint(1) not null default 1
);

-- tags de cada publicacion
create table if not exists tags_publicaciones (
    id_publicacion int not null,
    tag varchar(50) not null,
    primary key (id_publicacion, tag)
);

-- comentarios de las obras
create table if not exists comentarios (
    id int auto_increment primary key,
    id_obra int not null,
    id_usuario int not null,
    contenido varchar(500) not null,
    fecha datetime not null,
    activo tinyint(1) not null default 1
);

-- colecciones (albumes) que arman los usuarios
create table if not exists colecciones (
    id int auto_increment primary key,
    id_usuario int not null,
    nombre varchar(100) not null,
    descripcion varchar(500),
    activo tinyint(1) not null default 1
);

-- tabla que une colecciones con obras
create table if not exists colecciones_obras (
    id_coleccion int not null,
    id_obra int not null,
    primary key (id_coleccion, id_obra)
);

-- administrador para poder entrar la primera vez (email admin@pix.com, password 1234)
insert into usuarios (nombre, email, password, rol, nivel_acceso, activo, fecha_registro)
select 'admin', 'admin@pix.com', '1234', 'ADMINISTRADOR', 2, 1, curdate()
where not exists (select 1 from usuarios where email = 'admin@pix.com');
