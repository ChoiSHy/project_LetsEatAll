package com.letseatall.letseatall.common;

import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

@Component
public class Seeker {
    private final static Logger LOGGER = LoggerFactory.getLogger(Seeker.class);
    @Value("${python}")

    private String command;
    @Value("${python.file}")
    private String file;
    @Value("${python.model}")
    private String model_pos;

    public boolean run(Review review) throws IOException {
        ProcessBuilder processBuilder=null;
        Process process=null;
        LOGGER.info("[Seeker] 실행 시작.   --->   command : {}, file : {}", command, file);
        try {
            processBuilder = new ProcessBuilder(command, file,model_pos, review.getContent());

            LOGGER.info("[Seeker] 프로세스 시작 : {}", file);
            process = processBuilder.start();

            int exitval = process.waitFor();

            LOGGER.info("[Seeker] 프로세스 종료");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

            String line = br.readLine();
            LOGGER.info("[Seeker] {}", line);
            if(line == null)
                throw new Exception();
            return Boolean.parseBoolean(line.toLowerCase());
        } catch (Exception e) {
            if(process!= null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while((line = br.readLine())!= null)
                    LOGGER.error("[Seeker] {}", line);
            }
            throw new RemoteException("파이선 파일 실행 중 오류 발생");
        }
    }
}
