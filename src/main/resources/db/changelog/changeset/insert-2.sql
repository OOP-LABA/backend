--changeset RuslanYuneev:insert posts
--comment Insert posts migration

insert into posts (id, title, content, category_id, user_id, goal, account_details)
values
    (
     1,
     'Сбор на машину',
     'Помогите пожалуйста собрать деньги на классный автомобиль',
     2,
     1,
     100000,
     '88005553535'
    );