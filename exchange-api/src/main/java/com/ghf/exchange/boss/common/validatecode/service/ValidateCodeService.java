package com.ghf.exchange.boss.common.validatecode.service;

import com.ghf.exchange.boss.common.validatecode.dto.*;
import com.ghf.exchange.boss.common.validatecode.entity.ValidateCode;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.service.BaseService;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jiangyuanlin@163.com
 */
public interface ValidateCodeService extends BaseService<ValidateCode, Long> {

    /**
     * 生成图片验证码
     *
     * @param generatePictureValidateCodeReqDTO
     * @return
     */

    Result<PictureValidateCodeRespDTO> generatePictureValidateCode(@RequestBody GeneratePictureValidateCodeReqDTO generatePictureValidateCodeReqDTO);

    /**
     * 校验图片验证码
     *
     * @param checkValidateCodeReqDTO
     * @return
     */

    Result<PictureValidateCodeRespDTO> checkPictureValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO);

    /**
     * 生成短信验证码
     *
     * @param generateMessageValidateCodeReqDTO
     * @return
     */

    Result<MessageValidateCodeRespDTO> generateMessageValidateCode(@RequestBody GenerateMessageValidateCodeReqDTO generateMessageValidateCodeReqDTO);

    /**
     * 校验短信验证码
     *
     * @param checkValidateCodeReqDTO
     * @return
     */

    Result<MessageValidateCodeRespDTO> checkMessageValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO);

    /**
     * 生成邮箱验证码
     *
     * @param generateEmailValidateCodeReqDTO
     * @return
     */

    Result<EmailValidateCodeRespDTO> generateEmailValidateCode(@RequestBody GenerateEmailValidateCodeReqDTO generateEmailValidateCodeReqDTO);

    /**
     * 校验邮箱验证码
     *
     * @param checkValidateCodeReqDTO
     * @return
     */

    Result<EmailValidateCodeRespDTO> checkEmailValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO);

    /**
     * 生成滑块验证码
     *
     * @param generateSliderValidateCodeReqDTO
     * @return
     */

    Result<SliderValidateCodeRespDTO> generateSliderValidateCode(@RequestBody GenerateSliderValidateCodeReqDTO generateSliderValidateCodeReqDTO);

    /**
     * 校验滑块验证码
     *
     * @param checkValidateCodeReqDTO
     * @return
     */
    Result<SliderValidateCodeRespDTO> checkSliderValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO);

}