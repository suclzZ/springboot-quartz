1、注意，表是由jpa自动生成，由于系统使用了quartz内部表，在建表时会由于表关联出现问题，
可以先生成系统表，再人工生成quartz表，同时修改application.properties，禁止jpa自动生成表

2、由于是与web应用整合，提供了controller供前端调用