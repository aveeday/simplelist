# --- !Ups
create table `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `text` TEXT NOT NULL
)

# --- !Downs
drop table `message`
