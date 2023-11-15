package com.letseatall.letseatall.service.awsS3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class S3UploadService {
    @Autowired
    private AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 로컬 경로에 저장
     */
    public String uploadFileToS3(MultipartFile multipartFile, String filePath) {
        // MultipartFile -> File 로 변환
        File uploadFile = null;
        try {
            uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("[error]: MultipartFile -> 파일 변환 실패"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // S3에 저장된 파일 이름
        String fileName = filePath + "/" + UUID.randomUUID();

        // s3로 업로드 후 로컬 파일 삭제
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }


    /**
     * S3로 업로드
     * @param uploadFile : 업로드할 파일
     * @param fileName : 업로드할 파일 이름
     * @return 업로드 경로
     */
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에 있는 파일 삭제
     * 영어 파일만 삭제 가능 -> 한글 이름 파일은 안됨
     */
    public void deleteS3(String filePath) throws Exception {
        try{
            String key = filePath.substring(56); // 폴더/파일.확장자

            try {
                amazonS3Client.deleteObject(bucket, key);
            } catch (AmazonServiceException e) {
                log.info(e.getErrorMessage());
            }

        } catch (Exception exception) {
            log.info(exception.getMessage());
        }
        log.info("[S3Uploader] : S3에 있는 파일 삭제");
    }

    /**
     * 로컬에 저장된 파일 지우기
     * @param targetFile : 저장된 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("[파일 업로드] : 파일 삭제 성공");
            return;
        }
        log.info("[파일 업로드] : 파일 삭제 실패");
    }

    /**
     * 로컬에 파일 업로드 및 변환
     * @param file : 업로드할 파일
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        // 로컬에서 저장할 파일 경로 : user.dir => 현재 디렉토리 기준
        String dirPath = System.getProperty("user.dir") + "/" + file.getOriginalFilename();
        File convertFile = new File(dirPath);

        if (convertFile.createNewFile()) {
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
    public ResponseEntity<byte[]> getObject(String storedFileName) throws IOException {
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, storedFileName));
        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(storedFileName, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);

    }
}
