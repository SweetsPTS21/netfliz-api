drop table if exists public.tokens;
drop table if exists public.users;
drop table if exists public.profiles;
drop table if exists public.movies;
drop table if exists public.movie_genres; 
drop table if exists public.nf_files;
drop table if exists public.movie_images;
drop table if exists public.movie_episodes;
drop table if exists public.movie_assets;
drop table if exists public.movie_process_logs;

-- 1) Tạo bảng
CREATE TABLE public.users (
    id          SERIAL PRIMARY KEY,
    avatar      TEXT DEFAULT '',
    email       VARCHAR(255) NOT NULL DEFAULT '',
    first_name  VARCHAR(255) DEFAULT '',
    last_name   VARCHAR(255) DEFAULT '',
    "password"  VARCHAR(255) DEFAULT '',
    phone       VARCHAR(255) DEFAULT '',
    "role"      VARCHAR(50)  NOT NULL DEFAULT 'USER',
    status      INTEGER      NOT NULL DEFAULT 0,
    "type"      VARCHAR(50)  DEFAULT '0',
    created_at  TIMESTAMPTZ  DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  DEFAULT NOW(),
    username    VARCHAR(255) DEFAULT ''
);

COMMENT ON COLUMN public.users.avatar     IS 'User avatar';
COMMENT ON COLUMN public.users.email      IS 'User email';
COMMENT ON COLUMN public.users.first_name IS 'Họ';
COMMENT ON COLUMN public.users.last_name  IS 'Tên';
COMMENT ON COLUMN public.users.password   IS 'User password';
COMMENT ON COLUMN public.users.phone      IS 'Số điện thoại';
COMMENT ON COLUMN public.users.role       IS 'Role user: ADMIN, MANAGER, USER';
COMMENT ON COLUMN public.users.status     IS 'Trạng thái hiện tại: 0-inactive, 1-active, 2-banned';
COMMENT ON COLUMN public.users."type"     IS 'User type: 0-common, 1-vip1, 2-vip2, 3-vip3, 4-super vip';
COMMENT ON COLUMN public.users.created_at IS 'Ngày tạo';
COMMENT ON COLUMN public.users.updated_at IS 'Ngày cập nhật';
COMMENT ON COLUMN public.users.username   IS 'Username';


CREATE UNIQUE INDEX idx_users_email ON public.users (email);
CREATE INDEX        idx_users_phone ON public.users (phone);
CREATE UNIQUE INDEX idx_users_username ON public.users (username);

INSERT INTO public.users (avatar,email,first_name,last_name,"password",phone,"role",status,"type",created_at,updated_at,username) VALUES
	 (NULL,'user04@gmail.com','Trần','Văn A','$2a$10$T9rmLYQBoUDVZshBYj5hL.SrLi9ZJxQ93n8hgjn0VwhZXKrDd785G','0987654321','USER',1,'0','2025-11-09 08:23:12.722877+07','2025-11-09 08:23:12.722877+07','user04'),
	 (NULL,'user05@gmail.com','Trần Nguyễn','Hoàng','$2a$10$bo/6g8dON.fNw/gzROAiCeMml3dwJ6J3KgXRR7X07HPuk6OUq5A0.','0987654321','USER',1,'0','2025-11-09 08:47:02.991098+07','2025-11-09 08:47:02.991098+07','user05'),
	 (NULL,'user02@gmail.com','Nguyễn','Văn Nam','$2a$10$acYbRK/ECimMc9FEaMaI/ub1f.ks9ZX5k2tdE8FLC/E4dRO1cIgC.','0875324712','USER',2,'0','2025-11-08 17:17:55.668425+07','2025-11-08 17:17:55.668425+07','user02'),
	 (NULL,'admin@gmail.com','Admin','01','$2a$10$LPBz6/RQ/VmWQOI.K1CGDuaK69Du3fyib.TzQ/wjiTs5VQewGhcH2','0987635492','ADMIN',1,'0','2025-11-08 20:26:10.006938+07','2025-11-08 20:26:10.006938+07','admin');


-- 2) Token
CREATE TABLE public.tokens (
	id serial4 NOT NULL,
	expired bool NOT null default false,
	revoked bool NOT null default false,
	"token" text NOT null default '',
	token_type varchar(50) NOT null default 'BEARER',
	created_at timestamptz(6) default now(),
	updated_at timestamptz(6) DEFAULT now(),
	user_id int4 NOT NULL,
	CONSTRAINT token_pkey PRIMARY KEY (id),
	CONSTRAINT token_token_type_check CHECK (((token_type)::text = 'BEARER'::text)),
	CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_tokens_user_id ON public.tokens USING btree (user_id);
CREATE INDEX idx_tokens_value ON public.tokens USING btree (token);


-- 3) Profile
CREATE TABLE public.profiles (
	id serial4 not null,
	avatar text default '',
	description text default '',
	"name" varchar(255) null,
	"password" varchar(255) null,
	status integer default 0,
	"type" integer default 0,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	user_id int4 not null,
	CONSTRAINT profile_pkey PRIMARY KEY (id),
	CONSTRAINT profile_type_check CHECK (((type >= 0) AND (type <= 3))),
	constraint fk_profile_user foreign key (user_id) references public.users(id)
);
create index idx_profiles_user_id on public.profiles using btree(user_id);


-- 4) Movie
CREATE TABLE public.movies (
	id serial4 NOT NULL,
	title text not null default '',
	plot text NULL,
	runtime varchar(255) NULL,
	"type" varchar(255) default 'MOVIE',
	actors text NULL,
	awards text NULL,
	categories varchar(255) NULL,
	country varchar(255) NULL,
	director text NULL,
	genre text NULL,
	images text NULL,
	imdb_rating varchar(255) NULL,
	imdb_votes int8 NULL,
	languages varchar(255) NULL,
	meta_score int8 NULL,
	rated varchar(255) NULL,
	released varchar(255) NULL,
	response bool NOT NULL,
	trailer varchar(255) NULL,
	writer varchar(255) NULL,
	"year" int4 NOT null default 0,
	poster_id int8 DEFAULT 0 NULL,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	CONSTRAINT movie_pkey PRIMARY KEY (id)
);
create index idx_movies_year on public.movies using btree("year");


-- 5) Movie Genre
create table public.movie_genres (
	id serial4 not null,
	name varchar(255) not null default '',
	title varchar(255) null,
	description text null,
	slug text null,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	CONSTRAINT movie_genres_pkey PRIMARY KEY (id)
);
create index idx_movie_genres_name on public.movie_genres using btree("name");

INSERT INTO movie_genres (name, title, description, slug) VALUES
('Sci-Fi', 'Khoa học viễn tưởng', 'Phim khai thác các yếu tố khoa học viễn tưởng, công nghệ tương lai hoặc vũ trụ.', 'sci-fi'),
('Thriller', 'Giật gân / Hồi hộp', 'Phim gây hồi hộp, căng thẳng, tạo cảm giác kịch tính cho người xem.', 'thriller'),
('Action', 'Hành động / Kịch tính', 'Phim hành động với các pha chiến đấu, rượt đuổi và kỹ xảo mạnh mẽ.', 'action'),
('Adventure', 'Phiêu lưu', 'Phim phiêu lưu với hành trình khám phá và những trải nghiệm mạo hiểm.', 'adventure'),
('Superhero', 'Siêu anh hùng', 'Phim siêu anh hùng với nhân vật sở hữu năng lực đặc biệt.', 'superhero'),
('Crime', 'Tội phạm', 'Phim tội phạm xoay quanh điều tra, phá án hoặc thế giới ngầm.', 'crime'),
('Drama', 'Tâm lý / Chính kịch', 'Phim tâm lý, cảm xúc, tập trung vào nhân vật và câu chuyện đời sống.', 'drama'),
('Fantasy', 'Giả tưởng', 'Phim giả tưởng với yếu tố phép thuật, sinh vật kỳ ảo hoặc thế giới huyền bí.', 'fantasy'),
('Spy', 'Điệp viên', 'Phim tình báo với các nhiệm vụ bí mật và hành động căng thẳng.', 'spy'),
('Romance', 'Lãng mạn', 'Phim lãng mạn xoay quanh tình yêu và các mối quan hệ.', 'romance'),
('Musical', 'Nhạc kịch', 'Phim nhạc kịch, nhân vật thể hiện cảm xúc qua âm nhạc và vũ đạo.', 'musical'),
('Biography', 'Tiểu sử', 'Phim kể về cuộc đời các nhân vật có thật.', 'biography'),
('Music', 'Âm nhạc', 'Phim có chủ đề liên quan đến âm nhạc hoặc nghệ sĩ.', 'music'),
('History', 'Lịch sử', 'Phim tái hiện sự kiện, thời kỳ hoặc nhân vật trong quá khứ.', 'history'),
('Dark Comedy', 'Hài đen', 'Phim hài có yếu tố u ám, nghiêm túc hoặc châm biếm sâu.', 'dark-comedy'),
('Samurai', 'Samurai', 'Phim về võ sĩ đạo, văn hóa và lịch sử Nhật Bản.', 'samurai'),
('Historical', 'Lịch sử / Cổ trang', 'Phim mang bối cảnh lịch sử hoặc có yếu tố tái hiện thời đại.', 'historical'),
('Horror', 'Kinh dị', 'Phim tạo cảm giác sợ hãi, căng thẳng hoặc rùng rợn.', 'horror'),
('Martial Arts', 'Võ thuật', 'Phim tập trung vào các màn chiến đấu và kỹ năng võ học.', 'martial-arts'),
('Family', 'Gia đình', 'Phim phù hợp mọi lứa tuổi, mang thông điệp nhẹ nhàng và ấm áp.', 'family'),
('Comedy', 'Hài', 'Phim mang tính giải trí và gây tiếng cười.', 'comedy'),
('War', 'Chiến tranh', 'Phim tái hiện trận chiến, quân đội và hậu quả xung đột.', 'war'),
('Cyberpunk', 'Cyberpunk', 'Phim bối cảnh tương lai đen tối, công nghệ phát triển vượt bậc.', 'cyberpunk'),
('Youth', 'Tuổi trẻ / Thanh xuân', 'Phim về tuổi trẻ, trưởng thành và những trải nghiệm đầu đời.', 'youth'),
('Slice of Life', 'Đời thường', 'Phim mô tả những khoảnh khắc chân thật, giản dị trong cuộc sống.', 'slice-of-life'),
('Psychological', 'Tâm lý học', 'Phim khai thác chiều sâu cảm xúc, tinh thần và xung đột nội tâm.', 'psychological'),
('Science', 'Khoa học', 'Phim liên quan đến chủ đề khoa học và nghiên cứu.', 'science'),
('Friendship', 'Tình bạn', 'Phim khai thác mối quan hệ và sự gắn kết giữa bạn bè.', 'friendship'),
('Animation', 'Hoạt hình', 'Phim hoạt hình với hình ảnh vẽ tay, 2D, 3D hoặc CGI, phù hợp nhiều độ tuổi và có nội dung đa dạng.', 'animation');

-- 6) NF file
CREATE TABLE public.nf_files (
	id serial4 NOT NULL,
	file_category varchar(255) NULL,
	file_description varchar(255) NULL,
	file_download_uri text NULL,
	file_extension varchar(255) NULL,
	file_name text NULL,
	file_owner varchar(255) NULL,
	file_size int8 NULL,
	file_status varchar(255) NULL,
	file_tags varchar(255) NULL,
	file_type varchar(255) NULL,
	file_uploader varchar(255) NULL,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	CONSTRAINT nf_file_pkey PRIMARY KEY (id)
);

-- 7) Config
create table public.configs (
	id serial4 not null,
	config jsonb null,
	active bool not null default false,
	user_id int4 not null,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	CONSTRAINT configs_pkey PRIMARY KEY (id),
	constraint fk_configs_user foreign key (user_id) references public.users(id)
);

INSERT INTO public.configs (config,active,user_id,created_at,updated_at) VALUES
	 ('{"todayGenres": ["animation", "action", "scri-fi", "romance", "drama", "fantasy", "adventure", "comedy", "family"]}',true,4,'2025-11-13 12:42:04.92962+07','2025-11-13 12:42:04.92962+07');


-- 8) Movie images
create table public.movie_images (
	id serial4 not null,
	name varchar(255),
	format varchar(50),
	image_type int4 not null default 0,
	image_url varchar(255) null,
	file_id int4 not null default 0,
	object_id int8 not null default 0,
	object_type int4 not null default 0,
	created_at timestamptz default now(),
	updated_at timestamptz default now(),
	constraint movie_images_pkey primary key (id)
);

create index idx_movie_images_object_id on public.movie_images using btree(object_id);


-- 9) Movie Episode
create table public.movie_episodes (
	id serial8 PRIMARY KEY,
  	movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
  	episode_number INT NOT NULL, -- 1,2,3...
  	episode_order INT NOT NULL, -- same as episode_number but can be used if decimals/partials needed
  	name TEXT,
  	description TEXT,
  	runtime INT,
  	is_published BOOLEAN DEFAULT FALSE,
  	metadata JSONB DEFAULT '{}'::jsonb,
  	created_at TIMESTAMPTZ DEFAULT now(),
  	updated_at TIMESTAMPTZ DEFAULT now(),
  	UNIQUE (movie_id, episode_number)
);

-- 10) Movie Asset
CREATE TABLE public.movie_assets (
  	id serial8 PRIMARY KEY,
  	name varchar(255),
  	movie_id BIGINT REFERENCES movies(id) ON DELETE CASCADE,
  	episode_id BIGINT REFERENCES movie_episodes(id) ON DELETE CASCADE,
  	file_id BIGINT not null default 0,
  	asset_type INT, -- e.g. 'VIDEO', 'TRAILER', 'SUBTITLE'
  	format TEXT, -- 'hls', 'dash', 'mp4'
  	url TEXT NOT null default '',
  	drm JSONB, -- DRM details if any
  	rendition JSONB, -- bitrate, resolution etc
  	created_at TIMESTAMPTZ DEFAULT now()
);

create index idx_movie_assets_movie_id_episode_id on public.movie_assets using btree(movie_id, episode_id);

-- 11) Video processing
create table public.movie_process_logs (
	id serial8 primary key,
	object_id bigint not null default 0,
	object_type int not null default 1, -- 1: movie, 2 - episode
	status int not null default 1, -- 1: processing, 2 - completed, 3 - failed
	progress int not null default 0,
	configs jsonb,
	created_at TIMESTAMPTZ DEFAULT now(),
	updated_at TIMESTAMPTZ DEFAULT now()
);

create index idx_movie_process_logs_object_id on public.movie_process_logs using btree(object_id);








