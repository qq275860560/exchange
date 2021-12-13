# 币交所

##  下载代码

```
git clone https://github.com/qq275860560/exchange.git

```

## 修改配置
* 修改配置文件中的mysql，redis，mongodb配置，并把项目下doc目录中的所有schema.sql和data.sql导入至mysql

## 编译
```

mvn clean package -DskipTests -e -U
```


## 运行
```
java -jar exchange-api/exchange-api.jar
```


## 获取token
```


curl -i -X POST 'http://localhost:8080/api/user/login'   -d '{"username":"admin","password":"123456"}'  -H "Content-Type: application/json"

token=$(curl -i -X POST 'http://localhost:8080/api/user/login' -d '{"username":"admin","password":"123456"}' -H "Content-Type: application/json" | grep accessToken | awk -F 'accessToken' '{print $2}' | awk -F '"' '{print $3}')
echo $token

# 发送http请求，可以使用git-bash,通过curl命令发送请求，还可以使用postman。填写参数发送请求
# git-bash打开方式，右击桌面空白处，选择Git Bash Here
# 用户测试账号,平台管理员admin/123456,广告商家advertise_bussiness/123456,订单客户order_customer/123456
```


## 访问
```

curl -i -X POST 'http://localhost:8080/api/user/pageUser'   -d '{}'  -H "Content-Type: application/json"  -H "Authorization:bearer $token"
curl -i -X POST 'http://localhost:8080/api/client/pageClient'   -d '{"name":"admin"}'  -H "Content-Type: application/json"  -H "Authorization:bearer $token"


```

## A本地PI文档
浏览器打开
```

http://localhost:8080/doc.html

```
* 使用swagger进行接口调试前先在Authorize菜单中填写"bearer $token"


## 在线API文档
[在线API文档](https://qq275860560.github.io/%E5%8C%BA%E5%9D%97%E9%93%BE%E6%95%B0%E5%AD%97%E8%B4%A7%E5%B8%81%E4%BA%A4%E6%98%93%E6%89%80-API%E6%96%87%E6%A1%A3.html#pageOrderMessageUsingPOST)
## 在线设计文档
[在线设计文档](https://qq275860560.github.io/%E5%8C%BA%E5%9D%97%E9%93%BE%E6%95%B0%E5%AD%97%E8%B4%A7%E5%B8%81%E4%BA%A4%E6%98%93%E6%89%80-%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1.html#%E6%B6%88%E6%81%AF%E6%A8%A1%E5%9D%97-2)



# TODO LIST

# 单元测试
# 阅读旧代码
# 重写oauth2
# flink
# 性能测试
# 统计
# 基础框架重命名code和规范化异步
# 统计
# 对比其他项目



