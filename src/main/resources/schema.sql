
DROP TABLE IF EXISTS Contact;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS OrderDetails;
DROP TABLE IF EXISTS PurchaseOrder;
DROP TABLE IF EXISTS MainCategory;
DROP TABLE IF EXISTS SubCategory;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS SupplierContact;
DROP TABLE IF EXISTS Supplier;

CREATE TABLE IF NOT EXISTS User
(
    username    VARCHAR(50) PRIMARY KEY,
    full_name   VARCHAR(100)          NOT NULL,
    password    VARCHAR(300)          NOT NULL,
    role        ENUM ('ADMIN','USER') NOT NULL,
    no_of_sales INT                   NOT NULL DEFAULT 0,
    sales_value DECIMAL(8, 2)         NOT NULL DEFAULT '0.00'
);

CREATE TABLE IF NOT EXISTS Contact
(
    contact       VARCHAR(15) NOT NULL,
    user_username VARCHAR(50) NOT NULL,
    CONSTRAINT uk_contact UNIQUE KEY (contact),
    CONSTRAINT pk_contact PRIMARY KEY (contact, user_username),
    CONSTRAINT fk_contact FOREIGN KEY (user_username) REFERENCES User (username)
);

CREATE TABLE IF NOT EXISTS Supplier
(
    cName VARCHAR(6) PRIMARY KEY,
    sId   VARCHAR(11) NOT NULL,
    sName VARCHAR(100) NOT NULL,
    sSC   VARCHAR(4)   NOT NULL

);

CREATE TABLE IF NOT EXISTS SupplierContact
(
    contact  VARCHAR(15) NOT NULL,
    sup_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_supplier_contact PRIMARY KEY (sup_name, contact),
    CONSTRAINT fk_supplier_name FOREIGN KEY (sup_name) REFERENCES Supplier (cName)
);

CREATE TABLE IF NOT EXISTS PurchaseOrder
(
    orderNumber INT AUTO_INCREMENT PRIMARY KEY,
    supplier VARCHAR(10),
    orderDate DATE NOT NULL,
    saleMan VARCHAR(10) NOT NULL,
    CONSTRAINT order_supplier FOREIGN KEY (supplier) REFERENCES Supplier (cName)
);


CREATE TABLE IF NOT EXISTS Item
(
    itemCode     VARCHAR(20) PRIMARY KEY,
    cName        VARCHAR(20) NOT NULL,
    cost         DECIMAL(8, 2) DEFAULT 0.00,
    sellingPrice DECIMAL(8, 2) DEFAULT 0.00,
    stock        INT           DEFAULT 0,
    CONSTRAINT sup_name FOREIGN KEY (cName) REFERENCES Supplier (cName),
);


CREATE TABLE IF NOT EXISTS OrderDetails
(
    od_id          int AUTO_INCREMENT PRIMARY KEY,
    oNumber        INT         NOT NULL,
    item_code           VARCHAR(20) NOT NULL,
    item           VARCHAR(20) NOT NULL,
    qty            INT         NOT NULL,
    price_per_item DECIMAL     NOT NULL,
    total          DECIMAL     NOT NULL,
    description    VARCHAR(50),
    CONSTRAINT oder_detail_id FOREIGN KEY (oNumber) REFERENCES PurchaseOrder (orderNumber),
    CONSTRAINT oder_detail_code FOREIGN KEY (item_code) REFERENCES Item (itemCode)

);

CREATE TABLE IF NOT EXISTS SubCategory
(
    sid              VARCHAR(6) PRIMARY KEY,
    itemCode        VARCHAR(20) NOT NULL,
    scName          VARCHAR(30)   NOT NULL,
    description     VARCHAR(200),
    CONSTRAINT sub_item_code FOREIGN KEY (itemCode) REFERENCES Item(itemCode)
);

CREATE TABLE IF NOT EXISTS MainCategory
(
    mid      VARCHAR(6) PRIMARY KEY,
    sid     VARCHAR(6)  NOT NULL,
    mc_name VARCHAR(30) NOT NULL,
    CONSTRAINT sub_id FOREIGN KEY (sid) REFERENCES SubCategory(sid)
);

# select * from Department order by id desc limit 1;


