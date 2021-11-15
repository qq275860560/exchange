package com.ghf.exchange.boss.common.validatecode.controller;

import com.ghf.exchange.boss.common.validatecode.dto.*;
import com.ghf.exchange.boss.common.validatecode.service.ValidateCodeService;
import com.ghf.exchange.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiangyuanlin@163.com
 */
@Api(value = "验证码接口", tags = {"验证码接口"})

@RestController
@Lazy
@Slf4j
public class ValidateCodeController {
    @Lazy
    @Resource
    private ValidateCodeService validateCodeService;

    @ApiOperation(value = "生成图片验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/generatePictureValidateCode")
    @SneakyThrows
    public Result<PictureValidateCodeRespDTO> generatePictureValidateCode(@RequestBody GeneratePictureValidateCodeReqDTO generatePictureValidateCodeReqDTO) {
        return validateCodeService.generatePictureValidateCode(generatePictureValidateCodeReqDTO);
    }

    @ApiOperation(value = "校验图片验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/checkPictureValidateCode")
    @SneakyThrows
    public Result<PictureValidateCodeRespDTO> checkPictureValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        return validateCodeService.checkPictureValidateCode(checkValidateCodeReqDTO);
    }

    @ApiOperation(value = "生成短信验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/generateMessageValidateCode")
    @SneakyThrows
    public Result<MessageValidateCodeRespDTO> generateMessageValidateCode(@RequestBody GenerateMessageValidateCodeReqDTO generateMessageValidateCodeReqDTO) {
        return validateCodeService.generateMessageValidateCode(generateMessageValidateCodeReqDTO);
    }

    @ApiOperation(value = "校验短信验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/checkMessageValidateCode")
    @SneakyThrows
    public Result<MessageValidateCodeRespDTO> checkMessageValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        return validateCodeService.checkMessageValidateCode(checkValidateCodeReqDTO);
    }

    @ApiOperation(value = "生成邮箱验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/generateEmailValidateCode")
    @SneakyThrows
    public Result<EmailValidateCodeRespDTO> generateEmailValidateCode(@RequestBody GenerateEmailValidateCodeReqDTO generateEmailValidateCodeReqDTO) {
        return validateCodeService.generateEmailValidateCode(generateEmailValidateCodeReqDTO);
    }

    @ApiOperation(value = "校验邮箱验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/checkEmailValidateCode")
    @SneakyThrows
    public Result<EmailValidateCodeRespDTO> checkEmailValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        return validateCodeService.checkEmailValidateCode(checkValidateCodeReqDTO);
    }

    @ApiOperation(value = "生成滑块验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/generateSliderValidateCode")
    @SneakyThrows
    public Result<SliderValidateCodeRespDTO> generateSliderValidateCode(@RequestBody GenerateSliderValidateCodeReqDTO generateSliderValidateCodeReqDTO) {
        return validateCodeService.generateSliderValidateCode(generateSliderValidateCodeReqDTO);
    }

    @ApiOperation(value = "校验滑块验证码", notes = "<p></p>", httpMethod = "POST")
    @PostMapping(value = "/api/validateCode/checkSliderValidateCode")
    @SneakyThrows
    public Result<SliderValidateCodeRespDTO> checkSliderValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        return validateCodeService.checkSliderValidateCode(checkValidateCodeReqDTO);
    }

}
