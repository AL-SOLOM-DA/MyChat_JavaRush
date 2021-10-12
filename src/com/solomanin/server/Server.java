package com.solomanin.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                System.out.println("Сообщение не отправлено. Повторите попытку.");
            }
        }
    }

    public static void main(String[] args)  {
       ConsoleHelper.writeMessage("Введите номер порта:");
       int port = ConsoleHelper.readInt();
       try(ServerSocket serverSocket = new ServerSocket(port)){
           System.out.println("Сервер запущен..");
           while (true){
               Socket socket = serverSocket.accept();
               new Handler(socket).start();
           }
       } catch (IOException e){
           System.out.println("Network error!");
       }


    }

    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            while (true){
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                String name = message.getData();
                if(message.getType()==MessageType.USER_NAME
                        && name!=""
                        && !connectionMap.containsKey(name)){
                    connectionMap.put(name, connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED));
                    return name;
                } else {
                    continue;
                }
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (String name : connectionMap.keySet()){
                if(name!=userName){
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    StringBuilder str = new StringBuilder();
                    str.append(userName).append(":").append(" ").append(message.getData());
                    sendBroadcastMessage(new Message(MessageType.TEXT, str.toString()));
                } else {
                    ConsoleHelper.writeMessage("error");
                }
            }
        }

        public void run(){
            ConsoleHelper.writeMessage("Установлено соединение с " + socket.getRemoteSocketAddress());
            String userName = null;
            try  ( Connection connection = new Connection(socket)){
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (ClassNotFoundException | IOException e) {
                ConsoleHelper.writeMessage("Произошла ошибка");
            }
            if(userName!=null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }
            ConsoleHelper.writeMessage("Соединение с "+ socket.getRemoteSocketAddress() +" закрыто");
        }

    }

}
