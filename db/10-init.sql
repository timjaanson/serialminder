CREATE TABLE series (
	id VARCHAR(255) primary key,
	name VARCHAR(255) NOT NULL,
	available_seasons INT,
	last_checked TIMESTAMP,
	allowed_for_check BOOLEAN DEFAULT TRUE
);