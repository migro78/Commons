package top.doublewin.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.doublewin.core.filter.DefaultCorsFilter;
import top.doublewin.core.util.DataUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2019/3/28 23:03
 */
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class WebConfig {
    protected static Logger logger = LogManager.getLogger();

    @NacosValue(value = "${swagger.title:}", autoRefreshed = true)
    private String swaggerTitle;
    @NacosValue(value = "${swagger.description:}", autoRefreshed = true)
    private String swaggerDescription;


    /**
     * Json格式定义初始化
     *
     * @param
     * @return
     */
    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.valueOf("application/json;charset=UTF-8"));
        mediaTypes.add(MediaType.valueOf("text/html"));
        converter.setSupportedMediaTypes(mediaTypes);

        // converter.setFeatures(SerializerFeature.QuoteFieldNames, SerializerFeature.WriteDateUseDateFormat);

        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect
        );
        converter.setFastJsonConfig(config);
        return converter;
    }

    /**
     * Swagger2初始化
     *
     * @param
     * @return
     */
    @Bean
    public Docket platformApi() {
        logger.info("=======================     完成Swagger2初始化    =========================");
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        //header中的Authorization参数非必填，传空也可以
        ticketPar.name("Authorization").description("user token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        //根据每个方法名也知道当前方法在设置什么参数
        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        if(DataUtil.isEmpty(swaggerTitle)){
            swaggerTitle = "WEB-API";
        }
        if(DataUtil.isEmpty(swaggerDescription)){
            swaggerDescription = "©2018 Copyright.";
        }
        logger.debug("swaggerTitle is {},swaggerDescription is {}",swaggerTitle,swaggerDescription);
        return new ApiInfoBuilder()
                .title(swaggerTitle)
                .description(swaggerDescription)
                .termsOfServiceUrl("")
                .contact(new Contact("migro", "", ""))
                .version("2.0")
                .build();
    }


    @Bean
    public FilterRegistrationBean<DefaultCorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<DefaultCorsFilter> registration = new FilterRegistrationBean<DefaultCorsFilter>(new DefaultCorsFilter());
        registration.setName("corsFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(5);
        return registration;
    }


}
