create table schedule(
                         id int not null primary key auto_increment,
                         username varchar(100) not null,
                         title varchar(200),
                         content varchar(500),
                         created_at datetime default current_timestamp,
                         updated_at timestamp default current_timestamp on update current_timestamp
);

create table comment(
                        id int not null primary key auto_increment,
                        content varchar(300) not null,
                        username varchar(100) not null,
                        schedule_id int not null,
                        foreign key (schedule_id) references schedule(id)
);

create table user(
                     id int not null primary key auto_increment,
                     username varchar(100) not null,
                     email varchar(200) not null
);

create table manager(
                        id int not null primary key auto_increment,
                        schedule_id int not null,
                        user_id int not null,
                        foreign key (schedule_id) references schedule(id),
                        foreign key (user_id) references user(id)
);

alter table schedule drop column username;

alter table schedule add user_id int references user(id);

alter table user add password varchar(150);

alter table user add role varchar(100);