package com.insights;

//import com.insights.frota.importacao.sascar.ImportacaoSascarService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	public CommandLineRunner executarAoIniciar(ImportacaoSascarService importacaoSascarService) {
//		return args -> {
//			ClassPathResource resource = new ClassPathResource("detalhes_nota12112026204102104851.xlsx");
//
//			// Se o seu LocalMultipartFile exigir um Path, copie para um arquivo temporário no Linux do container:
//			Path tempFile = Files.createTempFile("teste-sascar", ".xlsx");
//			Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//			MultipartFile file = new LocalMultipartFile(
//					tempFile,
//					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//			);
//
//			System.out.println("Rodando via @Bean!");
//			importacaoSascarService.processar(file);
//		};
//	}

}
