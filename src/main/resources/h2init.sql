CREATE SCHEMA IF NOT EXISTS sh;
SET SCHEMA sh;
SET SCHEMA_SEARCH_PATH sh;

CREATE TABLE IF NOT EXISTS education_type(id INT NOT NULL PRIMARY KEY,
                                          name VARCHAR(40));

CREATE TABLE IF NOT EXISTS applicant (id INT PRIMARY KEY AUTO_INCREMENT,
                                      name VARCHAR(255),
    birthday DATE,
    mail VARCHAR(40),
    phone VARCHAR(40),
    gender CHARACTER,
    education_type INT,
    institution VARCHAR(255),
    FOREIGN KEY(education_type) REFERENCES education_type(id));

CREATE TABLE IF NOT EXISTS workplace (id INT NOT NULL,
                                      begindate DATE NOT NULL,
                                      enddate DATE,
                                      applicant_id INT,
                                      jobdescription VARCHAR(1024),
    PRIMARY KEY(id, begindate),
    FOREIGN KEY(applicant_id) REFERENCES applicant(id)
    ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS interview(id INT NOT NULL AUTO_INCREMENT,
                                     eventdate DATE NOT NULL,
                                     applicant_id INT,
                                     result VARCHAR(40),
    PRIMARY KEY(id, eventdate),
    FOREIGN KEY(applicant_id) REFERENCES applicant(id)
    ON DELETE CASCADE);


MERGE INTO education_type AS t USING
    (SELECT 1 as id, 'Высшее' as name UNION
     SELECT 2, 'н/Высшее' UNION
     SELECT 3, 'Среднее') AS s ON s.id = t.id
    WHEN NOT MATCHED THEN
        INSERT(id, name) VALUES (s.id, s.name);

COMMIT;

INSERT INTO applicant(name, birthday, mail, phone, gender, education_type, institution)
SELECT *
FROM (SELECT 'Ахметов Артур Ахматнурович', '1979-08-24', 'artur-a@yandex.ru', '922-555-55-55', 'М', 1, 'ОмГТУ') t
WHERE NOT EXISTS(SELECT * FROM applicant WHERE id = 1);

SET @pk = NULL;
SELECT SET(@pk, max(id)) RT FROM applicant;

-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 2, '2018-12-01', '2020-03-01', @pk, 'Инженер. Отдел разработки информационных систем.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES(3, '2018-07-01', '2019-01-01', @pk, 'Должность: инженер-программист III категории. Отдел бизнес-приложений.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 4, '2008-05-01','2018-02-01', @pk, 'Должность: ведущий инженер по автоматизированным системам управления производством. Отдел системно-технической инфраструктуры.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 5, '2006-10-01','2008-04-01', @pk, 'Должность: ведущий администратор систем. Отдел информационных технологий.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 6, '2004-09-01', '2006-10-01', @pk, 'Должность: ведущий инженер. Отдел ведения научно-справочной информации финансово-хозяйственной деятельности предприятия.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 7, '2001-07-01', '2004-09-01', @pk, 'Должность: инженер-программист III, II, I категории. Отдел АСУ производственными процессами.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 8, '2000-11-01', '2001-02-01', @pk, 'Должность: программист-оператор. Служба автоматизации и связи.');
-- INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription)
-- VALUES( 9, '1998-02-01', '1998-08-01', @pk, 'Должность: программист.');
--
-- COMMIT;
--
-- MERGE INTO workplace AS w USING
-- (SELECT 1 as id, '2019-02-01' as begindate, NULL as enddate, @pk as applicant_id, 'Ведущий инженер-программист. Отдел разработки программных средств.' as jobdescription union
-- SELECT 2, '2018-12-01', '2020-03-01', @pk, 'Инженер. Отдел разработки информационных систем.' union
-- SELECT 3, '2018-07-01', '2019-01-01', @pk, 'Должность: инженер-программист III категории. Отдел бизнес-приложений.' union
-- SELECT 4, '2008-05-01','2018-02-01', @pk, 'Должность: ведущий инженер по автоматизированным системам управления производством. Отдел системно-технической инфраструктуры.' union
-- SELECT 5, '2006-10-01','2008-04-01', @pk, 'Должность: ведущий администратор систем. Отдел информационных технологий.' union
-- SELECT 6, '2004-09-01', '2006-10-01', @pk, 'Должность: ведущий инженер. Отдел ведения научно-справочной информации финансово-хозяйственной деятельности предприятия.' union
-- SELECT 7, '2001-07-01', '2004-09-01', @pk, 'Должность: инженер-программист III, II, I категории. Отдел АСУ производственными процессами.' union
-- SELECT 8, '2000-11-01', '2001-02-01', @pk, 'Должность: программист-оператор. Служба автоматизации и связи.' union
-- SELECT 9, '1998-02-01', '1998-08-01', @pk, 'Должность: программист.'
-- ) as d
-- ON d.id = w.id
-- WHEN NOT MATCHED THEN
-- INSERT(id, begindate, enddate, applicant_id, jobdescription) VALUES (d.id, d.begindate, d.enddate, d.applicant_id, d.jobdescription);
--
-- COMMIT;
