insert into categoria (estado, nombre, imagen) values(true,'prueba', 'jar-loading.gif');
insert into categoria (estado, nombre, imagen) values(true,'prueba cate1', 'jar-loading.gif');
insert into categoria (estado, nombre, imagen) values(true,'prueba cate 2', 'jar-loading.gif');
insert into categoria (estado, nombre, imagen) values(true,'prueba cate 3', 'jar-loading.gif');

insert into mesa (estado, nombre) values (true, 'mesa1');
insert into mesa (estado, nombre) values (true, 'mesa2');
insert into mesa (estado, nombre) values (true, 'mesa3');
insert into mesa (estado, nombre) values (true, 'mesa4');
insert into mesa (estado, nombre) values (true, 'mesa5');
insert into mesa (estado, nombre) values (true, 'mesa6');
insert into mesa (estado, nombre) values (true, 'mesa7');
insert into mesa (estado, nombre) values (true, 'mesa8');
insert into mesa (estado, nombre) values (true, 'mesa9');
insert into mesa (estado, nombre) values (true, 'mesa10');
insert into mesa (estado, nombre) values (true, 'mesa11');
insert into mesa (estado, nombre) values (true, 'mesa12');
insert into mesa (estado, nombre) values (false, 'mesa13');
insert into mesa (estado, nombre) values (false, 'mesa14');

insert into producto (descripcion, estado, nombre, precio, categoria_id) values('sn', true, 'papas', 2.20, 1);

insert into ciudad(ciudad) values ('Cuenca');

insert into cliente (apellidos, cedula, celular, direccion, email, nombres, telefono, ciudad_id) values('Marcatoma', '0105354815', '0996935232', 'Checa', 'marcatoma99@gmail.com', 'Christian', '07-2897411', 1);