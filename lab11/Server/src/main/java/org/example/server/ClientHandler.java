package org.example.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Log
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Player player;
    private final Matchmaker matchmaker;
    private final ExecutorService executor;

    @Override
    public void run() {
        try {
            player.send(Protocol.S_WELCOME + Protocol.SEPARATOR + "Send your name with NAME|<your-name>");
            String line = player.readLineDirect();
            if (line == null) {
                log.info("Client " + player.addressLabel() + " closed before handshake");
                player.close();
                return;
            }
            String[] parts = line.split(Protocol.SPLIT_REGEX, 2);
            if (parts.length < 2 || !Protocol.C_NAME.equals(parts[0])) {
                player.send(Protocol.S_ERROR + Protocol.SEPARATOR + "Handshake failed: expected NAME|<name>");
                player.close();
                return;
            }
            String name = parts[1].trim();
            if (name.isEmpty()) {
                player.send(Protocol.S_ERROR + Protocol.SEPARATOR + "Name cannot be empty");
                player.close();
                return;
            }
            player.setName(name);
            log.info("Player '" + name + "' (" + player.addressLabel() + ") joined");

            executor.submit(new InboxFiller(player));
            matchmaker.enqueue(player);
        } catch (IOException e) {
            log.warning("Handshake error from " + player.addressLabel() + ": " + e.getMessage());
            player.close();
        }
    }

    @RequiredArgsConstructor
    private static final class InboxFiller implements Runnable {
        private final Player player;

        @Override
        public void run() {
            try {
                String line;
                while ((line = player.readLineDirect()) != null) {
                    player.getInbox().offer(line);
                }
            } catch (IOException ignored) {
            } finally {
                player.markDisconnected();
                player.getInbox().offer(Protocol.C_QUIT);
            }
        }
    }
}
