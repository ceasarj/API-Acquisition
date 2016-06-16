drop table if exists video_data;

create table video_data(
    video_id         varchar(50),
    title            varchar(100),
    published_date   DATE,
    view_count       INTEGER,
    like_count       INTEGER,
    dislike_count    INTEGER,
    comment_count    INTEGER
);

