INSERT INTO parfum_store (id, name, type, description, weight, price)
SELECT 0, 'Chanel No. 5', 'Легендарный женский аромат с нотами майской розы, жасмина, иланг-иланга и ванили. Классический цветочно-альдегидный парфюм, созданный в 1921 году.', 'Туалетная вода', 150.0, 50
WHERE NOT EXISTS (
    SELECT 1
    FROM parfum_store
    WHERE id = 0
);