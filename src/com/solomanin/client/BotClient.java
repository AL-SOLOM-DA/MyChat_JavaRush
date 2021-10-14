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
            BotClient.this.sendTextMessage("Привет чатику. Я бот. Принимаю комманды: дата, день, месяц, год, время, час, минуты,секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);

            String[] split = message.split(": ");
            if (split.length!=2) return;
            Calendar calendar = new GregorianCalendar();
            String dateFormat = null;
            switch (split[1]){
                case "дата": dateFormat = "d.MM.YYYY";
                        break;
                case "день": dateFormat = "d";
                    break;
                case "месяц": dateFormat = "MMMM";
                        break;
                case "год": dateFormat = "YYYY";
                    break;
                case "время": dateFormat = "H:mm:ss";
                    break;
                case "час": dateFormat = "H";
                    break;
                case "минуты": dateFormat = "m";
                    break;
                case "секунды":dateFormat = "s";
                    break;
            }
            if(dateFormat!=null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                BotClient.this.sendTextMessage("Ответ для " + split[0] + ": " + simpleDateFormat.format(Calendar.getInstance().getTime()));
            }

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
