package ru.avalon.java.j30.labs;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class Main {

    public static HashMap <String, String> sqlMap;      
    public static void main(String[] args) throws SQLException {
        
       Connection connection;
        if (loadDriver())  {                                                    // работаем с базой
        
         connection = getConnection();
            sqlMap = getQueries("sql/queries.sql");
//   printAllQueries (sqlMap);
            System.out.println("работаем с базой");
            
            Product product = new Product(50, "Mixer", 2005.00);
            
            product.save(connection);
            printAllCodes(connection);

            product.setPrice(1500.00);
            product.save(connection);
            printAllCodes(connection);
        }
    }
    /**
     * Выводит в кодсоль все коды товаров
     * 
     * @param connection действительное соединение с базой данных
     * @throws SQLException 
     */    
    private static void printAllCodes(Connection connection) throws SQLException {
        Collection<Product> codes = Product.all(connection);
        for (Product code : codes) {
            System.out.println(code);
        }
    }
    /**
     * Возвращает URL, описывающий месторасположение базы данных
     * @return URL в виде объекта класса {@link String}
     */
    private static String getUrl() {
          throw new UnsupportedOperationException("Not implemented yet!");
    }
    /**
     * Возвращает параметры соединения
     * @return Объект класса {@link Properties}, содержащий параметры user и 
     * password
     */
    private static Properties getProperties() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    /**
     * Возвращает соединение с базой данных Sample
     * @return объект типа {@link Connection}
     * @throws SQLException 
     */
    private static Connection getConnection() {
         
        String url = "jdbc:hsqldb:hsql://localhost:9002/example";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, "example", "");
            System.out.println("Соединение получено: " + conn);
//            PreparedStatement st = conn.prepareStatement("SELECT * FROM PRODUCT");
//            ResultSet rs = st.executeQuery();
//            while(rs.next()) {
//                System.out.println("NAME = " + rs.getString("NAME"));
//                             }
//                  System.out.println("количество строк = " + rs.next());
            return conn;
        } catch (SQLException ex) {
            System.out.println("Ошибка соединения " + ex.getMessage());
            return null;
        }
    }

    private static boolean loadDriver() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            System.out.println("Драйвер загружен");
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println("не найден драйвер "
            + ex.getMessage());
            return false;
        }
    }

    private static HashMap<String, String> getQueries(String path) {
        LinkedList<String> lines = new LinkedList<>();
        
        try (InputStream is = ClassLoader.getSystemResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is))) 
        {
            String s;
            while ((s=br.readLine()) != null) {
                lines.add(s);
               // System.out.println("строка:" + s);
            }
        }
        catch (IOException ex){
                      System.out.println("ошибка чтения " + path);  
        }
        
        HashMap<String, String> hss = new HashMap<>();
        boolean entryStarted = false;
        String key = null;
        StringBuilder sb = new StringBuilder();
        for(String line: lines){
            if(line.startsWith("--")) {
                entryStarted = true;
                key = line.substring(2).trim();                    // возвращает строку с 2го символа, отбрасывает пробелы
                
            } else {
                if(entryStarted) sb.append(line);                   // добавляем строку в StringBuilder
                if(line.trim().endsWith(";")) {                     // sql запрос закончился
                    entryStarted = false;
                    hss.put(key, sb.toString());                    //
                    sb.delete(0, sb.length());                      // очищаем StringBuilder
                }
            }
        }
                return hss;
    }

    private static void printAllQueries(HashMap<String, String> sqlMap) {           // печать SQL запросов
        for(Map.Entry<String, String> entry : sqlMap.entrySet()) {
            System.out.println("имя: " + entry.getKey() + "\nзапрос " + entry.getValue());
        }
    }
}
