-- Write your Task 1 answers in this file
--Create database called bedandbreakfast.

DROP SCHEMA IF EXISTS bedandbreakfast;
CREATE SCHEMA IF NOT EXISTS bedandbreakfast;
USE bedandbreakfast;

create table if not exists users (
    email varchar(128) not null,
    name varchar(128) not null,

    primary key (email)
);

create table if not exists bookings (
    booking_id char(8) not null,
    listing_id varchar(20) not null,
    email varchar(128) not null,
    duration int not null,
    primary key (booking_id),
    constraint fk_e foreign key (email) references users (email)
);

create table reviews (

   id int auto_increment,
   date timestamp default current_timestamp,
   listing_id varchar(20) not null,
   reviewer_name varchar(64) not null,
   comments text, 

   
   primary key(id)
);

GRANT ALL PRIVILEGES ON bedandbreakfast.* TO 'fred'@'%';
FLUSH PRIVILEGES;

--Batch insert user.csv -> users table 
INSERT INTO users(email,name) VALUES
 ('fred@gmail.com','Fred Flintstone')
,('barney@gmail.com','Barney Rubble')
,('fry@planetexpress.com','Philip J Fry')
,('hlmer@gmail.com','Homer Simpson');


