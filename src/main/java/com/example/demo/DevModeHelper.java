package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Profile("development")
@Slf4j
public class DevModeHelper implements InitializingBean {

    private static final int ALIVE_MARKER_TCP_PORT = 8999;
    private final Semaphore mutex = new Semaphore(0);
    private final Thread tcpServerThread = new Thread(this::spawnTcpServer, "tcp-server");
    @Value("${project.baseProjectDir:}")
    private String projectBaseDir;
    private final Thread devServerThread = new Thread(this::spawnDevServer, "esDevServer");

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void afterPropertiesSet() throws Exception {
        //FIXME Handle SpringBoot restart properly
        if (initialized.compareAndSet(false, true)) {
            if (!tcpServerThread.isAlive()) {
                tcpServerThread.start();
            }
            if (!devServerThread.isAlive()) {
                devServerThread.start();
            }
        }
    }

    private void spawnTcpServer() {
        try (ServerSocket serverSocket = new ServerSocket(ALIVE_MARKER_TCP_PORT)) {
            mutex.release();
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    // Just ignore
                }
            }
        } catch (IOException e) {
            log.error("Unable to start frontend server", e);
        }
    }

    private void spawnDevServer() {
        try {
            Path root = Paths.get(projectBaseDir);

            String commandLine;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                commandLine = "cmd /c npmw.cmd run start";
            } else {
                commandLine = "./npmw run start";
            }
            mutex.acquire();

            Process p = Runtime.getRuntime().exec(commandLine, null, root.toFile());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(line -> {
                    String cleanLine = line.replace("\033c","");
                    if (cleanLine.contains("error TS")) {
                        log.error(cleanLine);
                    } else {
                        log.info(cleanLine);
                    }
                });
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(log::error);
            }
        } catch (Exception e) {
            log.error("Unable to start frontend server", e);
        }
    }
}