-- =========================================
-- BASE DE DATOS: TechStore
-- =========================================
CREATE DATABASE TechStore;
GO

USE TechStore;
GO

-- =========================================
-- TABLA: USUARIOS
-- =========================================
CREATE TABLE USUARIOS
(
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL
        CHECK (rol IN ('ADMIN','VENDEDOR')),

    estado BIT NOT NULL DEFAULT 1,

    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_edicion DATETIME NULL,
    fecha_eliminacion DATETIME NULL,
    fecha_restauracion DATETIME NULL
);
GO

-- =========================================
-- TABLA: CLIENTES
-- =========================================
CREATE TABLE CLIENTES
(
    id_cliente INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni CHAR(8) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    email VARCHAR(100),

    estado BIT NOT NULL DEFAULT 1,

    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_edicion DATETIME NULL,
    fecha_eliminacion DATETIME NULL,
    fecha_restauracion DATETIME NULL
);
GO

-- =========================================
-- TABLA: PRODUCTOS
-- =========================================
CREATE TABLE PRODUCTOS
(
    id_producto INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL CHECK(precio>=0),
    stock INT NOT NULL CHECK(stock>=0),

    estado BIT NOT NULL DEFAULT 1,

    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_edicion DATETIME NULL,
    fecha_eliminacion DATETIME NULL,
    fecha_restauracion DATETIME NULL
);
GO

-- =========================================
-- TABLA: VENTAS
-- =========================================
CREATE TABLE VENTAS
(
    id_venta INT IDENTITY(1,1) PRIMARY KEY,

    fecha DATETIME NOT NULL DEFAULT GETDATE(),

    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,

    total DECIMAL(10,2) NOT NULL,

    estado BIT NOT NULL DEFAULT 1,

    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_edicion DATETIME NULL,
    fecha_eliminacion DATETIME NULL,
    fecha_restauracion DATETIME NULL,

    CONSTRAINT FK_VENTA_CLIENTE
        FOREIGN KEY(id_cliente)
        REFERENCES CLIENTES(id_cliente),

    CONSTRAINT FK_VENTA_USUARIO
        FOREIGN KEY(id_usuario)
        REFERENCES USUARIOS(id_usuario)
);
GO

-- =========================================
-- TABLA: DETALLE_VENTA
-- =========================================
CREATE TABLE DETALLE_VENTA
(
    id_detalle INT IDENTITY(1,1) PRIMARY KEY,

    id_venta INT NOT NULL,
    id_producto INT NOT NULL,

    cantidad INT NOT NULL CHECK(cantidad>0),

    precio_unitario DECIMAL(10,2) NOT NULL,

    subtotal AS (cantidad * precio_unitario) PERSISTED,

    estado BIT NOT NULL DEFAULT 1,

    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_edicion DATETIME NULL,
    fecha_eliminacion DATETIME NULL,
    fecha_restauracion DATETIME NULL,

    CONSTRAINT FK_DETALLE_VENTA
        FOREIGN KEY(id_venta)
        REFERENCES VENTAS(id_venta),

    CONSTRAINT FK_DETALLE_PRODUCTO
        FOREIGN KEY(id_producto)
        REFERENCES PRODUCTOS(id_producto)
);
GO

-- =========================================
-- DATOS DE PRUEBA
-- =========================================

INSERT INTO USUARIOS(nombre,usuario,password,rol)
VALUES
('Lucia Sanchez','admin','12345','ADMIN'),
('Maria Mendez','admin2','12345','VENDEDOR');
GO

INSERT INTO CLIENTES(nombre,dni,telefono,email)
VALUES
('Juan Perez','12345678','987654321','juan@gmail.com'),
('Maria Lopez','87654321','912345678','maria@gmail.com');
GO

INSERT INTO PRODUCTOS(nombre,descripcion,categoria,precio,stock)
VALUES
('iPhone 15','128GB Color Negro','Celular',3500,10),
('Samsung A54','256GB Azul','Celular',1800,15),
('Laptop HP Pavilion','16GB RAM 512GB SSD','Laptop',2800,5),
('Audifonos JBL','Bluetooth','Accesorio',150,30);
GO

-- =========================================
-- VENTA DE PRUEBA
-- =========================================

INSERT INTO VENTAS(fecha,id_cliente,id_usuario,total)
VALUES
(GETDATE(),1,1,3650);
GO

INSERT INTO DETALLE_VENTA(id_venta,id_producto,cantidad,precio_unitario)
VALUES
(1,1,1,3500),
(1,4,1,150);
GO

UPDATE PRODUCTOS
SET stock = stock - 1
WHERE id_producto IN (1,4);
GO

SELECT * FROM USUARIOS;
SELECT * FROM CLIENTES;
SELECT * FROM PRODUCTOS;
SELECT * FROM VENTAS;
SELECT * FROM DETALLE_VENTA;