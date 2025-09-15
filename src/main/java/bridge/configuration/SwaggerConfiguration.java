//package bridge.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////import springfox.documentation.builders.ApiInfoBuilder;
////import springfox.documentation.builders.PathSelectors;
////import springfox.documentation.builders.RequestHandlerSelectors;
////import springfox.documentation.service.ApiInfo;
////import springfox.documentation.spi.DocumentationType;
////import springfox.documentation.spring.web.plugins.Docket;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//
//@Configuration
//public class SwaggerConfiguration {
//
//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.apiInfo(apiInfo())
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("bridge"))
//				.paths(PathSelectors.any())
//				.build();
//	}
//	
//	private ApiInfo apiInfo() {
//		return new ApiInfoBuilder()
//				.title("Spring Boot Open API with Swagger")
//				.description("Bridge REST API")
//				.version("1.0.0")
//				.build();
//	}
//}


/* 
 Spring Boot 3.x 으로 변경하면서 Swagger 2(Springfox)호환 문제 발생, 
 springdoc-openapi 기반의 OpenAPI 3.0 방식으로 변경. 
 기존의 @ApiOperation(value=...) 어노테이션은 @Operation(summary=...)로 일괄 수정
 */