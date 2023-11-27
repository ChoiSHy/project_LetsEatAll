package com.letseatall.letseatall.common;

import org.apache.tomcat.jni.Proc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

@Component
public class SeekerLoader implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(SeekerLoader.class);
    private Process process;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOGGER.info("[SeekerLoader] unicorn main:app --reload");

        String command = "python";
        String file = "./main.py";
        String model_path = "./HaterSeeker_model.pth";

        ProcessBuilder processBuilder=null;
        LOGGER.info("[SeekerLoader] 실행 시작.   --->   command : {}, file : {}",
                command, file,model_path);
        try {
            processBuilder = new ProcessBuilder(command, file,model_path);

            LOGGER.info("[SeekerLoader] 프로세스 시작 : {}", file);
            process = processBuilder.start();

        } catch (Exception e) {
            if(process!= null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while((line = br.readLine())!= null)
                    LOGGER.error("[Seeker] {}", line);
            }
            throw new RemoteException("Fast API 실행 실패");
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        LOGGER.info("[SeekerLoader] shutting down FastAPI server...");
        if(process.isAlive()){
            process.destroy();
        }
        LOGGER.info("[SeekerLoader] shutting down finished!");
    }
}
