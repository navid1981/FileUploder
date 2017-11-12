package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
/**
 * The class accept a single file and it's Metadata (creation time) from Ajax and save into local file systems in server side.
 * @author Navid Vaziri
 *
 */
@RestController
@SpringBootApplication
@EnableAutoConfiguration
public class FileUploaderApplication {
	/**
	 * Make SpringBoot Executable by main method
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(FileUploaderApplication.class, args);
	}
	
	/**
	 * Receive two params (File and Creation time) by Ajax in Restfull Post method and make suitable response object for return.
	 * @param file
	 * @param time
	 * @return
	 */
	@PostMapping("/upload")
	public Response fileUploader(@RequestParam("file") MultipartFile file, @RequestParam("creationTime") String time) {

		if (file.isEmpty()) {
			return Response.status(400).entity("You didn't select any file").build();
		}

		Date date = new Date();
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
		} catch (ParseException e1) {
			return Response.status(400).entity("You didn't select File Creation Time").build();
		}
		FileTime ft = FileTime.fromMillis(date.getTime());

		try {
			saveFile(file, ft);
		} catch (IOException e) {
			return Response.status(500).entity("File cannot be saved").build();
		}

		return Response.status(201).entity(file.getOriginalFilename() + " was uploaded").build();
	}
	
	/**
	 * Save File into User Home directory of system and add its Metadata by using Files.setAttributes.
	 * We can also add Metadata of files in another file by creating new Path object and putting information in it.
	 * @param file
	 * @param fileTime
	 * @throws IOException
	 */
	private void saveFile(MultipartFile file, FileTime fileTime) throws IOException {
		Path target = Paths.get(System.getProperty("user.home") + "/" + file.getOriginalFilename());
		if (!Files.exists(target))
			Files.createFile(target);
		Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
		Files.setAttribute(target, "basic:creationTime", fileTime);
	}

}
