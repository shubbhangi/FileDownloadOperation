package com.app.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.app.file.util.FileStorageProperties;

@EnableConfigurationProperties({FileStorageProperties.class})
@SpringBootApplication
public class FileDownloadOperationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileDownloadOperationApplication.class, args);
	}

}
