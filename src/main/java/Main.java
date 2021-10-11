import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/vasilenkodb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "pass";
    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try{
            try{
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while(true){
                    System.out.println("1: add new appartment");
                    System.out.println("2: find appartments");
                    System.out.print("-> ");
                    String s = sc.nextLine();
                    switch(s) {
                        case "1":
                            add(sc);
                            break;
                        case "2":
                            System.out.println("1: find by price");
                            System.out.println("2: find by district and floor area");
                            System.out.println("3: find by rooms ");
                            System.out.print("-> ");
                            String tmp =sc.nextLine();
                            switch(tmp){
                                case "1":
                                    System.out.println("Price from: ");
                                    int from = sc.nextInt();
                                    System.out.println("Price to: ");
                                    int to = sc.nextInt();
                                    find(from, to);
                                    break;
                                case "2":
                                    System.out.println("Write a district: ");
                                    String district = sc.nextLine();
                                    System.out.println("Write a floor area: ");
                                    int floorArea = sc.nextInt();
                                    find(district, floorArea);
                                    break;
                                case "3":
                                    System.out.println("Number of rooms: ");
                                    int rooms = sc.nextInt();
                                    find(rooms);

                            }
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
    private static void initDB() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Appartments");
            st.execute("CREATE TABLE Appartments (id INT NOT NULL" + " AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(20) " + "NOT NULL, address VARCHAR (50) " + "NOT NULL, " +
                    "floorArea INT, rooms INT, price INT)");
        }
    }

    private static void add(Scanner sc) throws SQLException {
        System.out.println("Введите название района: ");
        String district = sc.nextLine();
        System.out.println("Введите адрес: ");
        String address = sc.nextLine();
        System.out.println("Введите площадь квартиры : ");
        int floorArea = sc.nextInt();
        System.out.println("Введите количество комнат: ");
        int rooms = sc.nextInt();
        System.out.println("Введите цену квартиры: ");
        int price = sc.nextInt();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO Appartments (district, address, floorArea, rooms, price)" +
                " VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, floorArea);
            ps.setInt(4, rooms);
            ps.setInt(5, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void find(int priceFrom, int priceTo) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Appartments WHERE price >= ? AND price <= ?");
        ps.setInt(1, priceFrom);
        ps.setInt(2, priceTo);
        print(ps);
    }

    private static void find(String district,int floorArea) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Appartments WHERE district = ? AND floorArea = ?");
        ps.setString(1, district);
        ps.setInt(2, floorArea);
        print(ps);
    }

    private static void find(int rooms) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Appartments WHERE rooms =  ?");
        ps.setInt(1, rooms);
        print(ps);
    }

    private static void print(PreparedStatement ps) throws SQLException {
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t");
                System.out.println();
                while (rs.next()){
                    for(int i = 1; i <= md.getColumnCount(); i++)
                        System.out.print(rs.getString(i) + "\t");
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }
}
