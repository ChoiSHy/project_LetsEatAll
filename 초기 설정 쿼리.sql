INSERT INTO category VALUES(1, '족발');
INSERT INTO category VALUES(2, '보쌈');
INSERT INTO category VALUES(3, '찜');
INSERT INTO category VALUES(4, '탕');
INSERT INTO category VALUES(5, '찌개');
INSERT INTO category VALUES(6, '돈까스');
INSERT INTO category VALUES(7, '회');
INSERT INTO category VALUES(8, '일식');
INSERT INTO category VALUES(9, '피자');
INSERT INTO category VALUES(10, '구이');
INSERT INTO category VALUES(11, '양식');
INSERT INTO category VALUES(12, '치킨');
INSERT INTO category VALUES(13, '중식');
INSERT INTO category VALUES(14, '아시안');
INSERT INTO category VALUES(15, '백반');
INSERT INTO category VALUES(16, '국수');
INSERT INTO category VALUES(17, '분식');
INSERT INTO category VALUES(18, '카페/디저트');
INSERT INTO category VALUES(19, '햄버거');

INSERT INTO franchise VALUES (1,'VIPS',11);
# INSERT INTO user VALUES(1, STR_TO_DATE('19990228','%Y%m%d'), 'ChoiSHy', 50);
# INSERT INTO login VALUES('tjdgus4697',1234,1);

INSERT INTO menu VALUES(1,'빕스 1997 스테이크 2인세트',52000,0,11,1,NULL);
INSERT INTO menu VALUES(2, '스테이크 싱글플래터 1인 세트',23000,0,11,1,NULL);
INSERT INTO menu VALUES(3, '얌새우 알리오올리오 파스타', 17900,0,11,1,NULL);
INSERT INTO menu VALUES(4, '쉬림프 로제 파스타',18900,0,11,1,NULL);
INSERT INTO menu VALUES(5, '시그니처 훈제 연어 샐러드',15500,0,11,1,NULL);

INSERT INTO restaurant VALUES(1, '도남중앙로','도남김밥',0,17,NULL);
INSERT INTO menu VALUES(6, '김밥', 3000, 0,17,NULL, 1);
INSERT INTO menu VALUES(7,'라면',5000,40,17,NULL, 1);