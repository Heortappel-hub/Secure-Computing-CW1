### Users Table

| ID | Name         | Username | Password  |
|----|--------------|----------|-----------|
| 1  | Nick Efford  | nde      | wysiwyg0  |
| 2  | Mary Jones   | mjones   | marymary  |
| 3  | Andrew Smith | aps      | abcd1234  |


### Patients Table

| ID | Surname | Forename | Address                                 | Born       | GP ID | Condition       |
|----|---------|----------|------------------------------------------|------------|-------|------------------|
| 1  | Davison | Peter    | 27 Rowan Avenue, Hightown, NT2 1AQ       | 1942-04-12 | 4     | Lung cancer      |
| 2  | Baird   | Joan     | 52 The Willows, Lowtown, LT5 7RA         | 1927-05-08 | 17    | Osteoarthritis   |
| 3  | Stevens | Susan    | 36 Queen Street, Histon, HT3 5EM         | 1989-04-01 | 2     | Asthma           |
| 4  | Johnson | Michael  | The Barn, Yuleville, YV67 2WR            | 1951-11-27 | 10    | Liver cancer     |
| 5  | Scott   | Ian      | 4 Vale Rise, Bingham, BG3 8GD            | 1978-09-15 | 15    | Pneumonia        |


Notice: No encrypted


Fix:
<img width="1366" height="519" alt="image" src="https://github.com/user-attachments/assets/2b4a9be7-533e-465d-a227-f4919913be57" />
Enable jetty.servlet.ServletContextHandler instead of import org.eclipse.jetty.servlet.ServletHandler; The Last version does not support httpsession.

## Hash update
<img width="895" height="263" alt="image" src="https://github.com/user-attachments/assets/88c37d10-f696-4fb6-a82f-3781bef44a63" />
For the previous data stored in the DB, update the password to encrypt after login.
