# TravelManager
国家5A景区旅游辅助平台，一个学生javaWeb实训作业
## 技术栈
### 前端：vue + antdv pro, 前台后台一体，采用动态路由根据权限加载菜单页面。
### 后端：Spring boot, Spring mvc, Mybatis-plus, JWT
### 数据库：Mysql, Minio
## 功能：
1. 后端请求拦截 + Token校验身份权限
2. 管理员、作者支持编写md格式的景区心得文章
3. 图片上传后,后端将图片存入Minio中并返回在Minio中的地址给前端
4. 根据文章浏览量降序分页加载展示热门景点
5. 作者、管理员支持对文章进行编辑等操作
6. 管理员支持对用户管理
## 使用部署
1. 后端端口：8080
2. 前端端口：6678 npm run dev
3. Minio端口：9000 桶名travel且设置桶为公开访问
4. Mysql: 创建数据库travel 运行SQL下init.sql文件创建表和初始数据