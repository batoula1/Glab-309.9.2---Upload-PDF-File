package com.perscholas.Demo.UploadPDF;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class UploadService {

    private final static String FILE_EXTENSION = ".pdf";

    public void encryptPDFFile(String path, String fileName,
                               MultipartFile multipartFile, String clientID ) throws IOException{

        boolean isFilePDF = isFilePDF(fileName);

        if (isFilePDF){
            String fullPath = path + "/" + clientID;
            createDirectory(fullPath);

            InputStream inputStream = multipartFile.getInputStream();
            PDDocument pdDocument = PDDocument.load(inputStream);

            AccessPermission ap =new AccessPermission();

            StandardProtectionPolicy stpp = new StandardProtectionPolicy(
                    "test", "test", ap);

            stpp.setEncryptionKeyLength(128);

            stpp.setPermissions(ap);

            pdDocument.protect(stpp);

            pdDocument.save(fullPath + "/" + fileName);
            pdDocument.close();
        }else{
            throw new IOException("The file should have PDF format");
        }
    }

    private static void createDirectory(String dirPath){
        Path p = Paths.get(dirPath);
        if(!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)){
            try{
                Files.createDirectory(p);
            }catch (IOException e){
                log.error("Creating directory failed: {}", e.getMessage());
            }
        }
    }

    private static boolean isFilePDF(String fileName){
        return fileName != null && fileName.endsWith(FILE_EXTENSION);
    }
}