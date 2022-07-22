import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class RemovingSpacesServer {
    public static void main(String[] args) {
        //  Занимаем порт, определяя серверный сокет
        try {
            final ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("127.0.0.1", 23334));
            while (true) {
                //  Ждем подключения клиента и получаем потоки для дальнейшей работы
                try (SocketChannel socketChannel = serverChannel.accept()) {
                    // Определяем буфер для получения данных
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                    while (socketChannel.isConnected()) {
                        //  читаем данные из канала в буфер
                        int bytesCount = socketChannel.read(inputBuffer);
                        //  если из потока читать нельзя, перестаем работать с этим клиентом
                        if (bytesCount == -1) break;
                        //  получаем переданную от клиента строку в нужной кодировке и
                        //  очищаем буфер
                        final String msg = new String(inputBuffer.array(), 0, bytesCount,
                                StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        System.out.println("Получено сообщение от клиента: " + msg);
                        String msgResult = msg.replaceAll(" ", "");
                        // отправляем сообщение клиента назад с пометкой ЭХО
                        socketChannel.write(ByteBuffer.wrap(("Результат: " + msgResult).getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException err) {
                    System.out.println(err.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

