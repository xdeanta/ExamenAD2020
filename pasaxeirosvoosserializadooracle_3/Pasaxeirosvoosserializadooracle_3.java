/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pasaxeirosvoosserializadooracle_3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author oracle
 */
public class Pasaxeirosvoosserializadooracle_3 {
    
    public static Connection conexion=null;

    public static Connection getConexion() throws SQLException  {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; 
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

    public static ArrayList<Reserva> lerReservas(){
        ObjectInputStream in;
        Reserva r;
        Object o;
        ArrayList<Reserva> info;
        info = new ArrayList();
        try{
           in = new ObjectInputStream(new FileInputStream("reservas"));
           while((o=in.readObject())!=null){
               r=(Reserva)o;
               info.add(r);
               //System.out.println(r);
           }
           in.close();
       }catch(IOException e2){
           System.out.println("Error al abrir el archivo");
       }catch(ClassNotFoundException e3){
           System.out.println("Clase no encontrada");
       }
        return info;
    }
    
    public static void actualizarDB(ArrayList<Reserva> in) throws SQLException{
        Statement st = conexion.createStatement();
        Statement st2 = conexion.createStatement();
        Statement st3 = conexion.createStatement();
        PreparedStatement pst = conexion.prepareStatement("update pasaxeiros set nreservas = ? where dni = ?");
        PreparedStatement pst2 = conexion.prepareStatement("insert into reservasfeitas values (?,?,?,?)");
        ResultSet rs1, rsvueloida,rsvuelovuelta;
        int nreservasum, sumprecio = 0;
        for(int i = 0; i < in.size(); i++){
            rs1=st.executeQuery("select * from pasaxeiros where dni='" + in.get(i).getDni() + "'");
            rsvueloida=st2.executeQuery("select prezo from voos where voo=" + in.get(i).getIdvooida());
            rsvuelovuelta=st3.executeQuery("select prezo from voos where voo=" + in.get(i).getIdvoovolta());
            while(rs1.next()){
                
                nreservasum=rs1.getInt("nreservas");
                nreservasum++;
                pst.setInt(1, nreservasum);
                pst.setString(2, in.get(i).getDni());
                pst.executeUpdate();
                pst2.setInt(1, in.get(i).getCodr());
                pst2.setString(2, in.get(i).getDni());
                pst2.setString(3, rs1.getString("nome"));
                while(rsvueloida.next()){
                    sumprecio=sumprecio+rsvueloida.getInt("prezo");
                }
                while(rsvuelovuelta.next()){
                    sumprecio=sumprecio+rsvuelovuelta.getInt("prezo");
                }
                //System.out.println("sumprecio:" + sumprecio);
                pst2.setInt(4, sumprecio);
                pst2.executeUpdate();
            }
            sumprecio=0;
            
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException{
        // TODO code application logic here
        ArrayList<Reserva> arr;
        conexion = getConexion();
        arr=lerReservas();
        //System.out.println("arr:" + arr);
        actualizarDB(arr);
        conexion.close();
    }
    
}
