package com.app.file;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.file.service.impl.FileStorageService;
import com.app.file.util.Empty;
import com.app.file.util.ResponseObject;


@RestController
public class FileController {
	@Autowired
	private Empty empty;
	
	
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
	private ResponseObject response;
   
    @SuppressWarnings("unused")
	@PostMapping("/uploadFile")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam(value="file", required=false) MultipartFile file) {
     
    	if(file == null) {
			response.setError("1");
			response.setMessage("'file' is empty or null please check");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
			
			
		}
		else {
			
    	String fileName =  fileStorageService.storeFile(file);
     
        if (fileName != null) {
			logger.info("your File is uploaded successfully");
				response.setMessage("your File is uploaded successfully");

				response.setData(fileName);
				response.setError("0");
				response.setStatus("SUCCESS");

				return ResponseEntity.ok(response);
			} else {
				logger.error("your File is not uploaded");
				response.setMessage("your File is not uploaded");

				response.setData(empty);
				response.setError("1");
				response.setStatus("FAIL");

				return ResponseEntity.ok(response);
			} 
		}
    }
   
    
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
       
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/deleteFile/{fileName:.+}")
    public ResponseEntity<ResponseObject> deleteFile(@PathVariable String fileName, HttpServletRequest request) {
       
    	try {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        	Files.delete(Paths.get(resource.getFile().getAbsolutePath()));
        	logger.info("FILE DELETED SUCCESSFULLY");
        	response.setError("1");
        	
        	response.setMessage(fileName+" FILE DELETED SUCCESSFULLY");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
            
        } catch (IOException ex) {
            logger.error("file does not exist");
            
            response.setError("1");   
			response.setMessage("file does not exist");
			response.setData(empty);
			response.setStatus("FAIL");
			
			return ResponseEntity.ok(response);
        }

    }


    
}