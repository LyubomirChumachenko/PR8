CREATE TABLE IF NOT EXISTS parfum_store (
                                            id SERIAL,
                                            name VARCHAR(255) NOT NULL,
                                            type VARCHAR(255),
                                            description TEXT,
                                            weight float(53) NOT NULL,
                                            price float(53) NOT NULL
                                         
);