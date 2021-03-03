insert into tipo_categoria (tipo) values ('Producto');
insert into tipo_categoria (tipo) values ('Combo');

insert into categoria (estado, nombre, imagen, eliminated, tipo_id) values(true,'Bebidas', 'jar-loading.gif', false,1);
insert into categoria (estado, nombre, imagen, eliminated, tipo_id) values(true,'prueba', 'jar-loading.gif', false,1);
insert into categoria (estado, nombre, imagen, eliminated, tipo_id) values(true,'prueba cate1', 'jar-loading.gif', false,1);
insert into categoria (estado, nombre, imagen, eliminated, tipo_id) values(true,'prueba cate 2', 'jar-loading.gif', false,2);
insert into categoria (estado, nombre, imagen, eliminated, tipo_id) values(true,'prueba cate 3', 'jar-loading.gif', false,2);

insert into sexo(tipo)values('Masculino');
insert into sexo(tipo)values('Femenino');
insert into sexo(tipo)values('Otro');

insert into estado(nom_estado) values('Solicitado');
insert into estado(nom_estado) values('En espera'); 
insert into estado(nom_estado) values('En preparacion');
insert into estado(nom_estado) values('Entregado');
insert into estado(nom_estado) values('Anulado');

insert into mesa (estado, nombre, eliminated) values (true, 'mesa1', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa2', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa3', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa4', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa5', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa6', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa7', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa8', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa9', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa10', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa11', false);
insert into mesa (estado, nombre, eliminated) values (true, 'mesa12', false);
insert into mesa (estado, nombre, eliminated) values (false, 'mesa13', false);
insert into mesa (estado, nombre, eliminated) values (false, 'mesa14', false);

insert into role (nombre) values ('ROLE_ADMIN');
insert into role (nombre) values ('ROLE_COCINERO');

insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('sn', true, 'papas', 2.20, 3, 'jar-loading.gif', false);
insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('sn', true, 'papas 1', 2.20, 3, 'jar-loading.gif', false);
insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('sn', true, 'papas 2', 2.20, 2, 'jar-loading.gif', false);
insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('sn', true, 'papas 3', 2.20, 2, 'jar-loading.gif', false);

insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('200ml', true, 'Cocacola', 0.50, 1, 'jar-loading.gif', false);
insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('400ml', true, 'Pepsi', 0.50, 1, 'jar-loading.gif', false);
insert into producto (descripcion, estado, nombre, precio, categoria_id, imagen, eliminated) values('400ml', true, 'Pepsi ligth', 0.50, 1, 'jar-loading.gif', false);

insert into ciudad(ciudad) values ('Cuenca');

insert into cliente (apellidos, cedula, celular, direccion, email, nombres) values('Final', '9999999999', '9999999999', 'sn', 'consumidorfinal@none.com', 'Consumidor');
insert into cliente (apellidos, cedula, celular, direccion, email, nombres) values('Marcatoma', '0105354815', '0996935232', 'Checa', 'marcatoma99@gmail.com', 'Christian');