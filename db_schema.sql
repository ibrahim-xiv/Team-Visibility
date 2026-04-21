-- ENUM TYPES
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE friendship_status AS ENUM ('PENDING', 'ACCEPTED');
CREATE TYPE report_target_type AS ENUM ('USER', 'EVENT');
CREATE TYPE report_status AS ENUM ('OPEN', 'CLOSED');

-- USERS
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role user_role NOT NULL DEFAULT 'USER',
    is_verified BOOLEAN DEFAULT FALSE,
    high_contrast BOOLEAN DEFAULT FALSE,
    notif_radius INTEGER
);

-- CATEGORIES
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- EVENTS
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    creator_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    lat FLOAT,
    lng FLOAT,
    date_time TIMESTAMP NOT NULL,
    is_non_commercial BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- FRIENDSHIPS
CREATE TABLE friendships (
    id SERIAL PRIMARY KEY,
    user_one_id INTEGER NOT NULL,
    user_two_id INTEGER NOT NULL,
    status friendship_status NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (user_one_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_two_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_friendship UNIQUE (user_one_id, user_two_id)
);

-- RIDE REQUESTS
CREATE TABLE ride_requests (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    provider_id INTEGER,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE SET NULL
);

-- REPORTS
CREATE TABLE reports (
    id SERIAL PRIMARY KEY,
    reporter_id INTEGER NOT NULL,
    target_type report_target_type NOT NULL,
    target_id INTEGER NOT NULL,
    reason TEXT,
    status report_status NOT NULL DEFAULT 'OPEN',
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE
);

