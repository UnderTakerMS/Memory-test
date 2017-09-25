import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbOperation {
    public DbOperation(){
        JOptionPane.showMessageDialog(null,"欢迎使用DbOpeartion(JDBC连贯操作库)，本类不提供任何可调用的方法或接口，具体操作请调用以下类\nDbConnection：连接数据库\nC：向数据库中添加数据\nU：更新数据库中的数据\nD：从数据库中删除数据\nR：从数据库中读取数据","DbOperation",JOptionPane.INFORMATION_MESSAGE);
    }
}

class DbConnection{
    private String userName, password, url, driverType, port,driverName,db,server,charset;
    private boolean singal = true;

    public DbConnection(){
        port = "3306";
        server = "localhost";
        userName = "root";
        password = "root";
    }

    public DbConnection(String driverName,String driverType,String server,String port,String db,String userName,String password){
        setDriverName(driverName);
        setDriverType(driverType);
        setServer(server);
        setPort(port);
        setDb(db);
        setUserName(userName);
        setPassword(password);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public void setDriverName(String driverName){
        this.driverName = driverName;
    }

    public void setDb(String db){
        this.db = db;
    }

    public void setServer(String server){
        this.server = server;
    }

    public void setCharset(boolean onOff,String charset){
        if(onOff){
            this.charset = "?useUnicode=true&amp;" + "characterEncoding=" + charset;
        }else{
            charset = null;
        }
    }

    public boolean setPort(String port) {
        DataCheck isNum = new DataCheck(port,"-?[0-9]+\\.?[0-9]*");
        singal = isNum.check();
        if(singal){
            this.port = port;
        }
        return singal;
    }

    public void setPort(int port){
        this.port = "" + port;
    }

    private boolean makeUrl(){
        if(driverType != null && server != null && port != null && db != null){
            this.url = driverType + "://" + server + ":" + port + "/" + db + charset;
            return true;
        }
        return false;
    }

    public Connection connectToDb(){
        if(this.singal){
            boolean checkMark = makeUrl();
            if(!checkMark){
                JOptionPane.showMessageDialog(null,"制作连接失败","内部错误",JOptionPane.ERROR_MESSAGE);
            }
            try{
                Class.forName(driverName);
            }catch (ClassNotFoundException e){
                JOptionPane.showMessageDialog(null,"加载数据库驱动程序失败\n" + "错误的驱动名","内部错误",JOptionPane.ERROR_MESSAGE);
                return null;
            }
            try {
                if (userName == null){
                    userName = "root";
                }
                if (password == null){
                    password = "root";
                }
                Connection connection = DriverManager.getConnection(url,userName,password);
                if(connection.isClosed()){
                    JOptionPane.showMessageDialog(null,"无法与数据库取得联系，数据库连接已经被关闭" ,"外部错误",JOptionPane.ERROR_MESSAGE);
                    System.out.print(3);
                    return null;
                }
                //JOptionPane.showMessageDialog(null,"连接数据库成功！","连接成功",JOptionPane.INFORMATION_MESSAGE);
                return connection;
            }catch (SQLException e){
                JOptionPane.showMessageDialog(null,"无法与数据库建立连接,可能是用户名密码或数据库名错误","外部错误",JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    void showConfig(){
        String msg = "您的数据库连接配置信息如下\n" + "数据库驱动：" + driverName + "\n连接类型：" + driverType + "\n数据库地址：" + server + "\n数据库端口：" + port + "\n用户名：" + userName + "\n密码：" + password + "\n所选数据库：" + db + "\n创建的URL为：" + url;
        JOptionPane.showMessageDialog(null,msg,"数据库配置信息",JOptionPane.INFORMATION_MESSAGE);
    }
}

class R{
    String sql="SELECT * ";
    String field;
    ResultSet resultSet;
    Connection handle;

    public R(Connection handle){
        this.handle = handle;
    }

    R where(String where){
        sql = sql + "WHERE " + where + " ";
        return this;
    }

    R limit(String limit){
        sql = sql + "LIMIT " + limit + " ";
        return this;
    }

    R table(String table){
        sql = sql + "FROM " + table + " ";
        return this;
    }

    R order(String field,String order){
        sql = sql + "ORDER BY " + field + " " + order + " ";
        return this;
    }

    R field(String field){
        this.field = field;
        return this;
    }

    ResultSet read(){
        try{
            if(!handle.isClosed()){
                Statement statement = handle.createStatement();
                resultSet = statement.executeQuery(sql);
                return resultSet;
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"执行SQL语句失败","内部错误",JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    List<String> read(String field){
        try{
            if(!handle.isClosed()){
                Statement statement = handle.createStatement();
                resultSet = statement.executeQuery(sql);
                List<String> list = new ArrayList<String>();
                while(resultSet.next()){
                    list.add(resultSet.getString(field));
                }
                return list;
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"执行SQL语句失败","内部错误",JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    String showSql(){
        return sql;
    }
}

class C{
    String sql = "INSERT ";
    ResultSet resultSet;
    Connection handle;

    public C(Connection handle){
        this.handle = handle;
    }

    C table(String table){
        sql = sql + "INTO " + table + " ";
        return this;
    }

    C field(String field){
        sql = sql + "(" + field + ")" + " ";
        return this;
    }

    C value(String value){
        sql = sql + "VALUES (" + value + ") ";
        return this;
    }

    int create(){
        try{
            if(!handle.isClosed()){
                PreparedStatement preparedStatement = handle.prepareStatement(sql);
                return preparedStatement.executeUpdate();
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"执行SQL语句失败","内部错误",JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    String showSql(){
        return sql;
    }
}

class U{
    String sql = "UPDATE ";
    ResultSet resultSet;
    Connection handle;

    public U(Connection handle){
        this.handle = handle;
    }

    U table(String table){
        sql = sql + table + " ";
        return this;
    }

    U set(String field,String value){
        sql = sql + "SET " + field + " = " + value + " ";
        return this;
    }

    U where(String where){
        sql = sql + "WHERE " + where + " ";
        return this;
    }

    int update(){
        try{
            if(!handle.isClosed()){
                PreparedStatement preparedStatement = handle.prepareStatement(sql);
                return preparedStatement.executeUpdate();
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"执行SQL语句失败","内部错误",JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    String showSql(){
        return sql;
    }
}

class D{
    String sql = "DELETE ";
    ResultSet resultSet;
    Connection handle;

    public D(Connection handle){
        this.handle = handle;
    }

    D table(String table){
        sql = sql + "FROM " + table + " ";
        return this;
    }

    D where(String where){
        sql = sql + "WHERE " + where + " ";
        return this;
    }

    int delete(){
        try{
            if(!handle.isClosed()){
                Statement statement = handle.createStatement();
                return statement.executeUpdate(sql);
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"执行SQL语句失败","内部错误",JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    String showSql(){
        return sql;
    }
}
