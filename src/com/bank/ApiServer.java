package com.bank;

import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.service.BankService;
import com.bank.util.DBConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class ApiServer {
    public static void main(String[] args) throws Exception {
        HttpServer server=HttpServer.create(new InetSocketAddress(8080),0);
        server.createContext("/api/customer",ApiServer::createCustomer);
        server.createContext("/api/customers",ApiServer::getCustomers);
        server.createContext("/api/login",ApiServer::login);
        server.createContext("/api/profile",ApiServer::profile);
        server.createContext("/api/account",ApiServer::createAccount);
        server.createContext("/api/balance",ApiServer::balance);
        server.createContext("/api/deposit",ApiServer::deposit);
        server.createContext("/api/withdraw",ApiServer::withdraw);
        server.createContext("/api/transfer",ApiServer::transfer);
        server.createContext("/api/history",ApiServer::history);
        server.start();
        System.out.println("Java API Server started on port 8080");
    }

    private static boolean options(HttpExchange e)throws IOException{
        if(e.getRequestMethod().equalsIgnoreCase("OPTIONS")){send(e,204,"");return true;}
        return false;
    }

    private static void createCustomer(HttpExchange e)throws IOException{
        if(options(e))return;
        try{
            Map<String,String>d=form(e); String name=d.get("name"),phone=d.get("phone"),email=d.get("email"),password=d.get("password");
            if(name==null||phone==null||email==null||password==null){send(e,400,"Please fill all fields!");return;}
            new CustomerDAO().addCustomer(new Customer(0,name,phone,email),password);
            send(e,200,"Customer created successfully!");
        }catch(Exception x){x.printStackTrace();send(e,500,"Customer creation failed!");}
    }

    private static void getCustomers(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection();
            PreparedStatement s=c.prepareStatement("SELECT customer_id,name,phone,email FROM customer ORDER BY customer_id");
            ResultSet r=s.executeQuery()){
            StringBuilder o=new StringBuilder();
            while(r.next())o.append(r.getInt(1)).append("|").append(r.getString(2)).append("|").append(r.getString(3)).append("|").append(r.getString(4)).append("\n");
            send(e,200,o.length()==0?"NO_CUSTOMERS":o.toString());
        }catch(Exception x){x.printStackTrace();send(e,500,"Unable to load customers!");}
    }

    private static void login(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection()){
            Map<String,String>d=form(e);
            PreparedStatement s=c.prepareStatement("SELECT customer_id,name FROM customer WHERE email=? AND password=?");
            s.setString(1,d.get("email"));s.setString(2,d.get("password"));ResultSet r=s.executeQuery();
            if(r.next())send(e,200,"SUCCESS|"+r.getInt(1)+"|"+r.getString(2));else send(e,401,"Invalid email or password!");
        }catch(Exception x){x.printStackTrace();send(e,500,"Login failed!");}
    }

    private static void profile(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection()){
            String q=e.getRequestURI().getQuery();Map<String,String>d=new HashMap<>();
            if(q!=null)for(String p:q.split("&")){String[]a=p.split("=",2);if(a.length==2)d.put(URLDecoder.decode(a[0],StandardCharsets.UTF_8),URLDecoder.decode(a[1],StandardCharsets.UTF_8));}
            int id=Integer.parseInt(d.get("customerId"));
            PreparedStatement s=c.prepareStatement("SELECT customer_id,name,phone,email FROM customer WHERE customer_id=?");s.setInt(1,id);ResultSet r=s.executeQuery();
            if(!r.next()){send(e,404,"Customer not found!");return;}
            StringBuilder j=new StringBuilder("{\"customerId\":").append(r.getInt(1)).append(",\"name\":\"").append(json(r.getString(2))).append("\",\"phone\":\"").append(json(r.getString(3))).append("\",\"email\":\"").append(json(r.getString(4))).append("\",\"accounts\":[");
            PreparedStatement a=c.prepareStatement("SELECT account_number,account_type,balance FROM account WHERE customer_id=? ORDER BY account_number");a.setInt(1,id);ResultSet ar=a.executeQuery();boolean first=true;
            while(ar.next()){if(!first)j.append(",");first=false;j.append("{\"accountNumber\":").append(ar.getLong(1)).append(",\"accountType\":\"").append(json(ar.getString(2))).append("\",\"balance\":").append(ar.getDouble(3)).append("}");}
            j.append("]}");send(e,200,j.toString());
        }catch(Exception x){x.printStackTrace();send(e,500,"Unable to load profile!");}
    }

    private static String json(String s){return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"");}

    private static void createAccount(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection()){
            Map<String,String>d=form(e);int id=Integer.parseInt(d.get("customerId"));double dep=Double.parseDouble(d.get("initialDeposit"));String type=d.get("accountType");
            if(dep<0){send(e,400,"Initial deposit cannot be negative!");return;}
            PreparedStatement cs=c.prepareStatement("SELECT customer_id FROM customer WHERE customer_id=?");cs.setInt(1,id);ResultSet cr=cs.executeQuery();
            if(!cr.next()){send(e,404,"Customer not found!");return;}
            Random rnd=new Random();long no;
            do{no=10000000L+rnd.nextInt(90000000);PreparedStatement ck=c.prepareStatement("SELECT account_number FROM account WHERE account_number=?");ck.setLong(1,no);ResultSet rr=ck.executeQuery();if(!rr.next()){rr.close();ck.close();break;}rr.close();ck.close();}while(true);
            PreparedStatement a=c.prepareStatement("INSERT INTO account(account_number,customer_id,account_type,balance) VALUES(?,?,?,?)");
            a.setLong(1,no);a.setInt(2,id);a.setString(3,type);a.setDouble(4,dep);a.executeUpdate();
            send(e,200,"Account created successfully! Account Number: "+no);
        }catch(Exception x){x.printStackTrace();send(e,500,"Account creation failed!");}
    }

    private static void balance(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection()){Map<String,String>d=form(e);PreparedStatement s=c.prepareStatement("SELECT balance FROM account WHERE account_number=?");s.setLong(1,Long.parseLong(d.get("accountNumber")));ResultSet r=s.executeQuery();if(r.next())send(e,200,String.valueOf(r.getDouble(1)));else send(e,404,"Account not found!");}catch(Exception x){x.printStackTrace();send(e,500,"Unable to check balance!");}
    }

    private static void deposit(HttpExchange e)throws IOException{
        if(options(e))return;
        try{Map<String,String>d=form(e);new BankService().deposit(Long.parseLong(d.get("accountNumber")),Double.parseDouble(d.get("amount")));send(e,200,"Deposit successful!");}catch(Exception x){x.printStackTrace();send(e,500,"Deposit failed!");}
    }

    private static void withdraw(HttpExchange e)throws IOException{
        if(options(e))return;
        try{Map<String,String>d=form(e);new BankService().withdraw(Long.parseLong(d.get("accountNumber")),Double.parseDouble(d.get("amount")));send(e,200,"Withdrawal successful!");}catch(Exception x){x.printStackTrace();send(e,500,"Withdrawal failed!");}
    }

    private static void transfer(HttpExchange e)throws IOException{
        if(options(e))return;
        try{Map<String,String>d=form(e);new BankService().transfer(Long.parseLong(d.get("senderAccount")),Long.parseLong(d.get("receiverAccount")),Double.parseDouble(d.get("amount")));send(e,200,"Transfer successful!");}catch(Exception x){x.printStackTrace();send(e,500,"Transfer failed!");}
    }

    private static void history(HttpExchange e)throws IOException{
        if(options(e))return;
        try(Connection c=DBConnection.getConnection()){
            Map<String,String>d=form(e);PreparedStatement s=c.prepareStatement("SELECT transaction_id,transaction_type,amount,transaction_date FROM transaction_history WHERE account_number=? ORDER BY transaction_date DESC");s.setLong(1,Long.parseLong(d.get("accountNumber")));ResultSet r=s.executeQuery();StringBuilder o=new StringBuilder();
            while(r.next())o.append(r.getInt(1)).append("|").append(r.getString(2)).append("|").append(r.getDouble(3)).append("|").append(r.getTimestamp(4)).append("\n");
            send(e,200,o.length()==0?"NO_TRANSACTIONS":o.toString());
        }catch(Exception x){x.printStackTrace();send(e,500,"Unable to load transaction history!");}
    }

    private static Map<String,String> form(HttpExchange e)throws IOException{
        String b=new String(e.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);Map<String,String>d=new HashMap<>();
        for(String p:b.split("&")){String[]a=p.split("=",2);if(a.length==2)d.put(URLDecoder.decode(a[0],StandardCharsets.UTF_8),URLDecoder.decode(a[1],StandardCharsets.UTF_8));}return d;
    }

    private static void send(HttpExchange e,int code,String s)throws IOException{
        e.getResponseHeaders().set("Access-Control-Allow-Origin","*");
        e.getResponseHeaders().set("Access-Control-Allow-Methods","GET,POST,OPTIONS");
        e.getResponseHeaders().set("Access-Control-Allow-Headers","Content-Type");
        e.getResponseHeaders().set("Content-Type","text/plain; charset=UTF-8");
        byte[]b=s.getBytes(StandardCharsets.UTF_8);e.sendResponseHeaders(code,b.length);try(OutputStream o=e.getResponseBody()){o.write(b);}
    }
}
