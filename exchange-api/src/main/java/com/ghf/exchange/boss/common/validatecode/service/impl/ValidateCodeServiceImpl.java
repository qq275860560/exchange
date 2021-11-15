package com.ghf.exchange.boss.common.validatecode.service.impl;

import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.resourcefile.entity.QResourceFile;
import com.ghf.exchange.boss.common.resourcefile.entity.ResourceFile;
import com.ghf.exchange.boss.common.resourcefile.service.ResourceFileService;
import com.ghf.exchange.boss.common.validatecode.dto.*;
import com.ghf.exchange.boss.common.validatecode.entity.QValidateCode;
import com.ghf.exchange.boss.common.validatecode.entity.ValidateCode;
import com.ghf.exchange.boss.common.validatecode.enums.ValidateCodeCauseEnum;
import com.ghf.exchange.boss.common.validatecode.enums.ValidateCodeStatusEnum;
import com.ghf.exchange.boss.common.validatecode.enums.ValidateCodeTypeEnum;
import com.ghf.exchange.boss.common.validatecode.repository.ValidateCodeRepository;
import com.ghf.exchange.boss.common.validatecode.service.ValidateCodeService;
import com.ghf.exchange.boss.common.validatecode.util.VerifyImageUtil;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.AutoMapUtils;
import com.ghf.exchange.util.IdUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class ValidateCodeServiceImpl extends BaseServiceImpl<ValidateCode, Long> implements ValidateCodeService {
    @Lazy
    @Resource
    private UserService userService;

    public ValidateCodeServiceImpl(ValidateCodeRepository repository) {
        super(repository);
    }

    @Lazy
    @Resource
    private DefaultKaptcha defaultKaptcha;
    @Lazy
    @Resource
    private RedisTemplate redisTemplate;
    @Lazy
    @Resource
    private ResourceFileService resourceFileService;

    @SneakyThrows
    @Override
    public Result<PictureValidateCodeRespDTO> generatePictureValidateCode(@RequestBody GeneratePictureValidateCodeReqDTO generatePictureValidateCodeReqDTO) {
        //TODO 根据用途可选择对应模板，比如英文模板，数字模板，混合模板,模板可以配置在数据字典模块，也可以定义在全局常量类，枚举类
        int validateCodeUsage = generatePictureValidateCodeReqDTO.getValidateCodeUsage();

        // 生成验证码开始
        String createText = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(createText);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        byte[] bytes = os.toByteArray();
        String imageBase64String = Base64.encodeBase64String(bytes);
        //生成验证码结束

        //写入数据库或者缓存开始
        ValidateCode validateCode = new ValidateCode();
        // 初始化
        long id = IdUtil.generateLongId();
        validateCode.setId(id);
        validateCode.setValidateCodeType(ValidateCodeTypeEnum.PICTURE.getCode());
        validateCode.setValidateCodeUsage(validateCodeUsage);
        validateCode.setValidateCodeKey("pictureValidateCode:" + id);
        validateCode.setValidateCodeValue(createText);
        validateCode.setSeconds(60);
        validateCode.setStatus(ValidateCodeStatusEnum.UN.getCode());
        validateCode.setCreateTime(new Date());
        //TODO 写库,未来写入缓存
        this.add(validateCode);
        //写入数据库或者缓存结束

        //返回
        PictureValidateCodeRespDTO validateCodeRespDTO = AutoMapUtils.map(validateCode, PictureValidateCodeRespDTO.class);
        // 初始化RespDTO剩余字段
        validateCodeRespDTO.setImageBase64String(imageBase64String);

        return new Result<>(validateCodeRespDTO);

    }

    @SneakyThrows
    @Override
    public Result<PictureValidateCodeRespDTO> checkPictureValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        String validateCodeKey = checkValidateCodeReqDTO.getValidateCodeKey();
        String validateCodeValue = checkValidateCodeReqDTO.getValidateCodeValue();

        PictureValidateCodeRespDTO validateCodeRespDTO = new PictureValidateCodeRespDTO();
        validateCodeRespDTO.setValidateCodeKey(validateCodeKey);

        Predicate predicate = QValidateCode.validateCode.validateCodeKey.eq(validateCodeKey);
        ValidateCode validateCode = this.get(predicate);

        //验证码存在时
        if (validateCode != null) {
            //验证通过时
            if (validateCode.getValidateCodeValue().equalsIgnoreCase(validateCodeValue)) {
                //TODO 物理删除,未来从缓存删除
                this.delete(predicate);
                int number1000 = 1000;
                //TODO 使用数据库时，数据库不会自动删除过期验证码，每天晚上还需要定时扫描删除过期验证码
                if (System.currentTimeMillis() > validateCode.getCreateTime().getTime() + validateCode.getSeconds() * number1000) {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                    validateCodeRespDTO.setCause(ValidateCodeCauseEnum.OVER_TIME.getCode());
                } else {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.SUCCESS.getCode());
                }
            } else {
                validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                validateCodeRespDTO.setCause(ValidateCodeCauseEnum.ERROR.getCode());
            }
        } else {
            validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
            validateCodeRespDTO.setCause(ValidateCodeCauseEnum.NOT_EXISTS.getCode());
        }
        return new Result<>(validateCodeRespDTO);
    }

    @SneakyThrows
    @Override
    public Result<MessageValidateCodeRespDTO> generateMessageValidateCode(@RequestBody GenerateMessageValidateCodeReqDTO generateMessageValidateCodeReqDTO) {

        String mobile = generateMessageValidateCodeReqDTO.getMobile();
        if (ObjectUtils.isEmpty(mobile)) {
            return new Result(ResultCodeEnum.MOBILE_CODE_NOT_NULL);
        }

        //TODO 根据用途可选择对应模板，比如英文模板，数字模板，混合模板,模板可以配置在数据字典模块，也可以定义在全局常量类，枚举类
        int validateCodeUsage = generateMessageValidateCodeReqDTO.getValidateCodeUsage();
        final String messageTemplate = "XXX，你正在进行{}业务，你的短信验证码为{code}，请保管好不要随意给其他人，有效时间为3分钟";
        // 生成验证码开始
        String createText = RandomStringUtils.randomNumeric(6);
        // 生成验证码结束

        //写入数据库或者缓存开始
        ValidateCode validateCode = new ValidateCode();
        // 初始化
        long id = IdUtil.generateLongId();
        validateCode.setId(id);
        validateCode.setValidateCodeType(ValidateCodeTypeEnum.MESSAGE.getCode());
        validateCode.setValidateCodeUsage(validateCodeUsage);
        validateCode.setValidateCodeKey("messageValidateCode:" + id);
        validateCode.setValidateCodeValue(createText);
        validateCode.setSeconds(60);
        validateCode.setStatus(ValidateCodeStatusEnum.UN.getCode());
        validateCode.setCreateTime(new Date());
        //TODO 写库,未来写入缓存
        this.add(validateCode);
        //写入数据库或者缓存结束

        //返回
        EmailValidateCodeRespDTO validateCodeRespDTO = AutoMapUtils.map(validateCode, EmailValidateCodeRespDTO.class);
        //TODO 发送短信

        return null;
    }

    @SneakyThrows
    @Override
    public Result<MessageValidateCodeRespDTO> checkMessageValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        String validateCodeKey = checkValidateCodeReqDTO.getValidateCodeKey();
        String validateCodeValue = checkValidateCodeReqDTO.getValidateCodeValue();

        MessageValidateCodeRespDTO validateCodeRespDTO = new MessageValidateCodeRespDTO();
        validateCodeRespDTO.setValidateCodeKey(validateCodeKey);

        Predicate predicate = QValidateCode.validateCode.validateCodeKey.eq(validateCodeKey);
        ValidateCode validateCode = this.get(predicate);

        //验证码存在时
        if (validateCode != null) {
            //验证通过时
            if (validateCode.getValidateCodeValue().equalsIgnoreCase(validateCodeValue)) {
                //TODO 物理删除,未来从缓存删除
                this.delete(predicate);
                int number1000 = 1000;
                //TODO 使用数据库时，数据库不会自动删除过期验证码，每天晚上还需要定时扫描删除过期验证码
                if (System.currentTimeMillis() > validateCode.getCreateTime().getTime() + validateCode.getSeconds() * number1000) {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                    validateCodeRespDTO.setCause(ValidateCodeCauseEnum.OVER_TIME.getCode());
                } else {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.SUCCESS.getCode());
                }
            } else {
                validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                validateCodeRespDTO.setCause(ValidateCodeCauseEnum.ERROR.getCode());
            }
        } else {
            validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
            validateCodeRespDTO.setCause(ValidateCodeCauseEnum.NOT_EXISTS.getCode());
        }
        return new Result<>(validateCodeRespDTO);
    }

    @SneakyThrows
    @Override
    public Result<EmailValidateCodeRespDTO> generateEmailValidateCode(@RequestBody GenerateEmailValidateCodeReqDTO generateEmailValidateCodeReqDTO) {
        String email = generateEmailValidateCodeReqDTO.getEmail();
        if (ObjectUtils.isEmpty(email)) {
            return new Result(ResultCodeEnum.EMAIL_CODE_NOT_NULL);
        }

        //TODO 根据用途可选择对应模板，比如英文模板，数字模板，混合模板,模板可以配置在数据字典模块，也可以定义在全局常量类，枚举类
        int validateCodeUsage = generateEmailValidateCodeReqDTO.getValidateCodeUsage();
        final String codeSubject = "XXX";
        final String codeContent = "【YYY】本次%s操作验证码为【%s】，请保管好不要随意给其他人，有效时间为3分钟";
        // 生成验证码开始
        String createText = RandomStringUtils.randomNumeric(6);
        // 生成验证码结束

        //写入数据库或者缓存开始
        ValidateCode validateCode = new ValidateCode();
        // 初始化
        long id = IdUtil.generateLongId();
        validateCode.setId(id);
        validateCode.setValidateCodeType(ValidateCodeTypeEnum.EMAIL.getCode());
        validateCode.setValidateCodeUsage(validateCodeUsage);
        validateCode.setValidateCodeKey("emailValidateCode:" + id);
        validateCode.setValidateCodeValue(createText);
        validateCode.setSeconds(60);
        validateCode.setStatus(ValidateCodeStatusEnum.UN.getCode());
        validateCode.setCreateTime(new Date());
        //TODO 写库,未来写入缓存
        this.add(validateCode);
        //写入数据库或者缓存结束

        //返回
        EmailValidateCodeRespDTO validateCodeRespDTO = AutoMapUtils.map(validateCode, EmailValidateCodeRespDTO.class);
        //发送邮件
        return new Result<>(validateCodeRespDTO);
    }

    @SneakyThrows
    @Override
    public Result<EmailValidateCodeRespDTO> checkEmailValidateCode(@RequestBody CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        String validateCodeKey = checkValidateCodeReqDTO.getValidateCodeKey();
        String validateCodeValue = checkValidateCodeReqDTO.getValidateCodeValue();

        EmailValidateCodeRespDTO validateCodeRespDTO = new EmailValidateCodeRespDTO();
        validateCodeRespDTO.setValidateCodeKey(validateCodeKey);

        Predicate predicate = QValidateCode.validateCode.validateCodeKey.eq(validateCodeKey);
        ValidateCode validateCode = this.get(predicate);

        //验证码存在时
        if (validateCode != null) {
            //验证通过时
            if (validateCode.getValidateCodeValue().equalsIgnoreCase(validateCodeValue)) {
                //TODO 物理删除,未来从缓存删除
                this.delete(predicate);
                int number1000 = 1000;
                //TODO 使用数据库时，数据库不会自动删除过期验证码，每天晚上还需要定时扫描删除过期验证码
                if (System.currentTimeMillis() > validateCode.getCreateTime().getTime() + validateCode.getSeconds() * number1000) {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                    validateCodeRespDTO.setCause(ValidateCodeCauseEnum.OVER_TIME.getCode());
                } else {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.SUCCESS.getCode());
                }
            } else {
                validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                validateCodeRespDTO.setCause(ValidateCodeCauseEnum.ERROR.getCode());
            }
        } else {
            validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
            validateCodeRespDTO.setCause(ValidateCodeCauseEnum.NOT_EXISTS.getCode());
        }
        return new Result<>(validateCodeRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<SliderValidateCodeRespDTO> generateSliderValidateCode(GenerateSliderValidateCodeReqDTO generateSliderValidateCodeReqDTO) {

        int validateCodeUsage = generateSliderValidateCodeReqDTO.getValidateCodeUsage();

        // 生成验证码开始
        Predicate predicate = QResourceFile.resourceFile.resourceUsage.eq(1);
        List<ResourceFile> files = resourceFileService.list(predicate);
        int n = new Random().nextInt(files.size());
        String path = files.get(n).getPath();

        // 生成验证码开始
        URL url = new URL(path);
        BufferedImage bufferedImage = ImageIO.read(url.openStream());
        Random rand = new Random();
        int widthRandom = rand.nextInt(bufferedImage.getWidth() - 2 * VerifyImageUtil.TARGET_WIDTH) + VerifyImageUtil.TARGET_WIDTH;
        int heightRandom = rand.nextInt(bufferedImage.getHeight() - VerifyImageUtil.TARGET_HEIGHT);
        log.debug("原图大小{} x {},随机生成的坐标 X,Y 为（{}，{}）", bufferedImage.getWidth(), bufferedImage.getHeight(), widthRandom, heightRandom);

        BufferedImage smallImage = new BufferedImage(VerifyImageUtil.TARGET_WIDTH, VerifyImageUtil.TARGET_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        VerifyImageUtil.cutByTemplate(bufferedImage, smallImage, VerifyImageUtil.getBlockData(), widthRandom, heightRandom);
        //大图
        FastByteArrayOutputStream bigImageFastByteArrayOutputStream = new FastByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bigImageFastByteArrayOutputStream);
        byte[] bigImageBytes = bigImageFastByteArrayOutputStream.toByteArray();
        String bigImageString = Base64.encodeBase64String(bigImageBytes);
        //小图
        FastByteArrayOutputStream simallImageFastByteArrayOutputStream = new FastByteArrayOutputStream();
        ImageIO.write(smallImage, "png", simallImageFastByteArrayOutputStream);
        byte[] smallImageBytes = simallImageFastByteArrayOutputStream.toByteArray();
        String smallImageString = Base64.encodeBase64String(smallImageBytes);
        //生成验证码结束

        //写入数据库或者缓存开始
        ValidateCode validateCode = new ValidateCode();
        // 初始化
        long id = IdUtil.generateLongId();
        validateCode.setId(id);
        validateCode.setValidateCodeType(ValidateCodeTypeEnum.SLIDER.getCode());
        validateCode.setValidateCodeUsage(validateCodeUsage);
        validateCode.setValidateCodeKey("sliderValidateCode:" + id);
        validateCode.setValidateCodeValue(String.valueOf(widthRandom));
        validateCode.setSeconds(60);
        validateCode.setStatus(ValidateCodeStatusEnum.UN.getCode());
        validateCode.setCreateTime(new Date());
        //TODO 写库,未来写入缓存
        this.add(validateCode);
        //写入数据库或者缓存结束

        //返回
        SliderValidateCodeRespDTO sliderValidateCodeRespDTO = AutoMapUtils.map(validateCode, SliderValidateCodeRespDTO.class);
        // 初始化RespDTO剩余字段
        //大图
        sliderValidateCodeRespDTO.setBigImage(bigImageString);
        //小图
        sliderValidateCodeRespDTO.setSmallImage(smallImageString);
        sliderValidateCodeRespDTO.setXWidth(widthRandom);
        sliderValidateCodeRespDTO.setYHeight(heightRandom);
        sliderValidateCodeRespDTO.setBigImageWidth(bufferedImage.getWidth());
        sliderValidateCodeRespDTO.setBigImageHeight(bufferedImage.getHeight());
        return new Result<>(sliderValidateCodeRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<SliderValidateCodeRespDTO> checkSliderValidateCode(CheckValidateCodeReqDTO checkValidateCodeReqDTO) {
        String validateCodeKey = checkValidateCodeReqDTO.getValidateCodeKey();
        String validateCodeValue = checkValidateCodeReqDTO.getValidateCodeValue();

        SliderValidateCodeRespDTO validateCodeRespDTO = new SliderValidateCodeRespDTO();
        validateCodeRespDTO.setValidateCodeKey(validateCodeKey);

        Predicate predicate = QValidateCode.validateCode.validateCodeKey.eq(validateCodeKey);
        ValidateCode validateCode = this.get(predicate);

        //验证码存在时
        if (validateCode != null) {
            int number10 = 10;
            if (Math.abs(Double.valueOf(validateCodeValue) - Double.valueOf(validateCode.getValidateCodeValue())) <= number10) {
                //TODO 物理删除,未来从缓存删除
                this.delete(predicate);
                int number1000 = 1000;
                //TODO 使用数据库时，数据库不会自动删除过期验证码，每天晚上还需要定时扫描删除过期验证码
                if (System.currentTimeMillis() > validateCode.getCreateTime().getTime() + validateCode.getSeconds() * number1000) {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                    validateCodeRespDTO.setCause(ValidateCodeCauseEnum.OVER_TIME.getCode());
                } else {
                    validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.SUCCESS.getCode());
                }
            } else {
                validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
                validateCodeRespDTO.setCause(ValidateCodeCauseEnum.ERROR.getCode());
            }
        } else {
            validateCodeRespDTO.setStatus(ValidateCodeStatusEnum.FAIL.getCode());
            validateCodeRespDTO.setCause(ValidateCodeCauseEnum.NOT_EXISTS.getCode());
        }
        return new Result<>(validateCodeRespDTO);
    }

}