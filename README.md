# Mail-System
架构：

    1) 采用SSM框架   
    2) 使用Mysql数据库
    3) MVC三层架构
    4) 前后端分离
 
功能实现：

    1) eml文件的读取与解析：
        ① 使用JavaMail读取并解析eml文件，根据邮件类型(text/plain, text/html, multipart/*)进行解析
        ② 对于text/html文本，使用jsoup对相应html文本进行解析，提取文本内容
        ③ 对于有多个附件并且带压缩包的附件，使用ZipUtil进行解压，ZipUtil的实现基于javaSE自带包
    
    2) 业务功能实现：
        ① 数据库存储相关：
           · 邮件正文以及相关信息存储于数据库中
           · 邮件相关附件，只存储相关磁盘文件名在attachment表中，需要搜索时会读取磁盘文件
           · 附件attachment表通过字段email_id作为外键，与邮件表email关联
        ② 搜索功能实现：
           · 搜索策略：使用数据库模糊匹配对标题和正文的匹配进行搜索并率先返回一部分数据，
                      对于关键词在附件中的部分，会另开线程进行搜索
           · 搜索实现(主要是附件搜索)：使用Apache Tika工具包，对磁盘文件读取并解析，提取其文本内容，
                                    并使用KMP算法进行关键词搜索
    
    3) 前端部分实现：
        使用阿里巴巴的SUI MOBIE作为基础前端模板
        
    4) 程序运行：
        ① 通过访问/initialize进行数据库更新，数据库相关信息可在resources下的db.properties中进行配置
        ② 通过访问/route/toSearch访问搜索界面，页面中有查询内容和查询附件两个选项，可选择搜索范围，标题默认进行搜索
        ③ 点击搜索页面相应列表卡片，可以查询邮件相关信息