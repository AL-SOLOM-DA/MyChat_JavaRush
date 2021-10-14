package com.solomanin.client;

import com.solomanin.server.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread{
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Принимаю комманды: дата, день, месяц, год, время, час, минуты,секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String userName = message.substring(0, message.indexOf(":"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.YYYY H:mm:ss");

        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected String getUserName() {
        return "get_bot_" + (int)(Math.random()*100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    public static void main(String[] args) {
        Client botClient = new BotClient();
        botClient.run();
    }
}
