create table schedule(
                         id int not null primary key auto_increment,
                         username varchar(100) not null,
                         title varchar(200),
                         content varchar(500),
                         created_at datetime default current_timestamp,
                         updated_at timestamp default current_timestamp on update current_timestamp
);

