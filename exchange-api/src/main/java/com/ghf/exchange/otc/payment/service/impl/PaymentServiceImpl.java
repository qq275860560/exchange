package com.ghf.exchange.otc.payment.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.otc.payment.dto.*;
import com.ghf.exchange.otc.payment.entity.Payment;
import com.ghf.exchange.otc.payment.entity.QPayment;
import com.ghf.exchange.otc.payment.enums.PaymentStatusEnum;
import com.ghf.exchange.otc.payment.enums.PaymentTypeEnum;
import com.ghf.exchange.otc.payment.repository.PaymentRepository;
import com.ghf.exchange.otc.payment.service.PaymentService;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class PaymentServiceImpl extends BaseServiceImpl<Payment, Long> implements PaymentService {

    @Lazy
    @Resource
    private UserService userService;
    @Lazy
    @Resource
    private PaymentService paymentService;

    @Lazy
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public PaymentServiceImpl(PaymentRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Payment", key = "'pagePayment:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.paymentType)", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")

    @Override
    @SneakyThrows
    public Result<PageRespDTO<PaymentRespDTO>> pagePayment(PagePaymentReqDTO pagePaymentReqDTO) {
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QPayment.payment.username.eq(currentLoginUser.getUsername()));

        if (pagePaymentReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || pagePaymentReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || pagePaymentReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()
                || pagePaymentReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()
        ) {
            predicate.and(QPayment.payment.paymentType.eq(pagePaymentReqDTO.getPaymentType()));
        }

        PageRespDTO<PaymentRespDTO> pageRespDTO = paymentService.page(predicate, pagePaymentReqDTO, PaymentRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Payment", key = "'pagePaymentForAdmin:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction).concat(':').concat(#p0.username).concat(':').concat(#p0.paymentType) ", condition = "      #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<PaymentRespDTO>> pagePaymentForAdmin(PagePaymentForAdminReqDTO pagePaymentForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pagePaymentForAdminReqDTO.getUsername())) {
            predicate.and(QPayment.payment.username.eq(pagePaymentForAdminReqDTO.getUsername()));
        }

        if (pagePaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || pagePaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || pagePaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()
                || pagePaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()
        ) {
            predicate.and(QPayment.payment.paymentType.eq(pagePaymentForAdminReqDTO.getPaymentType()));
        }

        PageRespDTO<PaymentRespDTO> pageRespDTO = paymentService.page(predicate, pagePaymentForAdminReqDTO, PaymentRespDTO.class);

        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Payment", key = "'listPayment:'.concat(#p0.paymentType) ")
    @Override
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPayment(ListPaymentReqDTO listPaymentReqDTO) {

        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        BooleanBuilder predicate = new BooleanBuilder();
        if (listPaymentReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || listPaymentReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || listPaymentReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()
                || listPaymentReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()
        ) {
            predicate.and(QPayment.payment.paymentType.eq(listPaymentReqDTO.getPaymentType()));
        }
        predicate.and(QPayment.payment.username.eq(currentLoginUser.getUsername()));
        predicate.and(QPayment.payment.status.eq(PaymentStatusEnum.ENABLE.getCode()));

        List<PaymentRespDTO> list = paymentService.list(predicate, PaymentRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Payment", key = "'listPaymentForAdmin:'.concat(#p0.username).concat(':').concat(#p0.paymentType)")
    @Override
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPaymentForAdmin(ListPaymentForAdminReqDTO listPaymentForAdminReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (listPaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || listPaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || listPaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()
                || listPaymentForAdminReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()
        ) {
            predicate.and(QPayment.payment.paymentType.eq(listPaymentForAdminReqDTO.getPaymentType()));
        }
        if (!ObjectUtils.isEmpty(listPaymentForAdminReqDTO.getUsername())) {
            predicate.and(QPayment.payment.username.eq(listPaymentForAdminReqDTO.getUsername()));
        }
        predicate.and(QPayment.payment.status.eq(PaymentStatusEnum.ENABLE.getCode()));

        List<PaymentRespDTO> list = paymentService.list(predicate, PaymentRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Payment", key = "'listPaymentForClient:'.concat(#p0.username).concat(':').concat(#p0.paymentType)")
    @Override
    @SneakyThrows
    public Result<List<PaymentRespDTO>> listPaymentForClient(ListPaymentForClientReqDTO listPaymentForClientReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (listPaymentForClientReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()
                || listPaymentForClientReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()
                || listPaymentForClientReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()
                || listPaymentForClientReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()
        ) {
            predicate.and(QPayment.payment.paymentType.eq(listPaymentForClientReqDTO.getPaymentType()));
        }
        if (!ObjectUtils.isEmpty(listPaymentForClientReqDTO.getUsername())) {
            predicate.and(QPayment.payment.username.eq(listPaymentForClientReqDTO.getUsername()));
        }
        predicate.and(QPayment.payment.status.eq(PaymentStatusEnum.ENABLE.getCode()));

        List<PaymentRespDTO> list = paymentService.list(predicate, PaymentRespDTO.class);

        return new Result<>(list);
    }

    @Cacheable(cacheNames = "Payment", key = "'getPaymentByPaymentCode:'.concat(':').concat(#p0.paymentCode)")
    @Override
    @SneakyThrows
    public Result<PaymentRespDTO> getPaymentByPaymentCode(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {

        String paymentCode = getPaymentByPaymentCodeReqDTO.getPaymentCode();
        Predicate predicate = QPayment.payment.paymentCode.eq(paymentCode);
        Payment payment = paymentService.get(predicate);

        //返回
        PaymentRespDTO paymentRespDTO = ModelMapperUtil.map(payment, PaymentRespDTO.class);

        return new Result<>(paymentRespDTO);
    }

    @Cacheable(cacheNames = "Payment", key = "'getPaymentByPaymentTypeForClient:'.concat(':').concat(#p0.username).concat(':').concat(#p0.paymentType)")
    @Override
    @SneakyThrows
    public Result<PaymentRespDTO> getPaymentByPaymentTypeForClient(GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO) {
        String username = getPaymentByPaymentTypeForClientReqDTO.getUsername();
        int paymentType = getPaymentByPaymentTypeForClientReqDTO.getPaymentType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QPayment.payment.username.eq(username));
        booleanBuilder.and(QPayment.payment.paymentType.eq(paymentType));
        booleanBuilder.and(QPayment.payment.status.eq(PaymentStatusEnum.ENABLE.getCode()));
        Payment payment = paymentService.get(booleanBuilder);
        //返回
        PaymentRespDTO paymentRespDTO = ModelMapperUtil.map(payment, PaymentRespDTO.class);
        return new Result<>(paymentRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsPaymentByPaymentCode(GetPaymentByPaymentCodeReqDTO paymentByPaymentCodeReqDTO) {

        String paymentCode = paymentByPaymentCodeReqDTO.getPaymentCode();
        Predicate predicate = QPayment.payment.paymentCode.eq(paymentCode);
        boolean b = paymentService.exists(predicate);
        return new Result<>(b);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsPaymentByPaymentTypeForClient(GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO) {

        String username = getPaymentByPaymentTypeForClientReqDTO.getUsername();
        int paymentType = getPaymentByPaymentTypeForClientReqDTO.getPaymentType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QPayment.payment.username.eq(username));
        booleanBuilder.and(QPayment.payment.paymentType.eq(paymentType));
        booleanBuilder.and(QPayment.payment.status.eq(PaymentStatusEnum.ENABLE.getCode()));
        boolean b = paymentService.exists(booleanBuilder);

        return new Result<>(b);
    }

    //TODO 根据国家获取银行名称列表

    @CacheEvict(cacheNames = "Payment", allEntries = true)
    @Override
    @SneakyThrows
    public Result<?> addPayment(AddPaymentReqDTO addPaymentReqDTO) {
        Payment payment = new Payment();

        //初始化id
        payment.setId(IdUtil.generateLongId());
        //判断编号
        if (!ObjectUtils.isEmpty(addPaymentReqDTO.getPaymentCode())) {
            //判断唯一性
            String paymentCode = addPaymentReqDTO.getPaymentCode();
            GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO = new GetPaymentByPaymentCodeReqDTO();
            getPaymentByPaymentCodeReqDTO.setPaymentCode(paymentCode);
            boolean b = paymentService.existsPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO).getData();
            if (b) {
                return new Result<>(ResultCodeEnum.PAYMENT_EXISTS);
            }
            payment.setPaymentCode(addPaymentReqDTO.getPaymentCode());
        } else {
            //自动生成编号
            payment.setPaymentCode(payment.getId() + "");
        }

        //TODO 用户启用的每种付款方式类型最多只能一种

        String username = userService.getCurrentLoginUser().getData().getUsername();
        int paymentType = addPaymentReqDTO.getPaymentType();
        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO.setUsername(username);
        getPaymentByPaymentTypeForClientReqDTO.setPaymentType(paymentType);
        boolean b = paymentService.existsPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_EXISTS);
        }

        if (addPaymentReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()) {
            payment.setPaymentType(addPaymentReqDTO.getPaymentType());
            payment.setPaymentTypeAlipayAccount(addPaymentReqDTO.getPaymentTypeAlipayAccount());
            payment.setPaymentTypeAlipayQrcode(addPaymentReqDTO.getPaymentTypeAlipayQrcode());
        } else if (addPaymentReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()) {
            payment.setPaymentType(addPaymentReqDTO.getPaymentType());

            payment.setPaymentTypeWechatAccount(addPaymentReqDTO.getPaymentTypeWechatAccount());
            payment.setPaymentTypeWechatQrcode(addPaymentReqDTO.getPaymentTypeWechatAccount());

        } else if (addPaymentReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()) {
            payment.setPaymentType(addPaymentReqDTO.getPaymentType());
            payment.setPaymentTypeBankName(addPaymentReqDTO.getPaymentTypeBankName());
            payment.setPaymentTypeBankBranchName(addPaymentReqDTO.getPaymentTypeBankBranchName());
            payment.setPaymentTypeBankAccount(addPaymentReqDTO.getPaymentTypeBankAccount());
            payment.setPaymentTypeBankRealname(addPaymentReqDTO.getPaymentTypeBankRealname());

        } else if (addPaymentReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()) {

        } else {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }

        //获取当前登陆用户详情

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        payment.setUsername(currentLoginUser.getUsername());

        payment.setCreateTime(new Date());
        //判断唯一性

        payment.setStatus(PaymentStatusEnum.ENABLE.getCode());
        payment.setRemark(addPaymentReqDTO.getRemark());
        //新增到数据库
        paymentService.add(payment);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Payment", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> updatePaymentByPaymentCode(UpdatePaymentByPaymentCodeReqDTO updatePaymentByPaymentCodeReqDTO) {
        String paymentCode = updatePaymentByPaymentCodeReqDTO.getPaymentCode();
        //加载
        GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO = new GetPaymentByPaymentCodeReqDTO();
        getPaymentByPaymentCodeReqDTO.setPaymentCode(paymentCode);
        PaymentRespDTO afterPaymentRespDTO = paymentService.getPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO).getData();
        Payment afterPayment = ModelMapperUtil.map(afterPaymentRespDTO, Payment.class);

        //
        if (afterPayment == null) {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        if (currentLoginUser.getUsername().equals(afterPayment.getUsername())) {
            //创建者才有权限
            flag = true;
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //
        if (updatePaymentByPaymentCodeReqDTO.getPaymentType() == PaymentTypeEnum.ALIPAY.getCode()) {
            afterPayment.setPaymentType(updatePaymentByPaymentCodeReqDTO.getPaymentType());
            afterPayment.setPaymentTypeAlipayAccount(updatePaymentByPaymentCodeReqDTO.getPaymentTypeAlipayAccount());
            afterPayment.setPaymentTypeAlipayQrcode(updatePaymentByPaymentCodeReqDTO.getPaymentTypeAlipayQrcode());

        } else if (updatePaymentByPaymentCodeReqDTO.getPaymentType() == PaymentTypeEnum.WECHAT.getCode()) {
            afterPayment.setPaymentType(updatePaymentByPaymentCodeReqDTO.getPaymentType());
            afterPayment.setPaymentTypeWechatAccount(updatePaymentByPaymentCodeReqDTO.getPaymentTypeWechatAccount());
            afterPayment.setPaymentTypeWechatQrcode(updatePaymentByPaymentCodeReqDTO.getPaymentTypeWechatAccount());

        } else if (updatePaymentByPaymentCodeReqDTO.getPaymentType() == PaymentTypeEnum.BANK.getCode()) {
            afterPayment.setPaymentType(updatePaymentByPaymentCodeReqDTO.getPaymentType());
            afterPayment.setPaymentTypeBankName(updatePaymentByPaymentCodeReqDTO.getPaymentTypeBankName());
            afterPayment.setPaymentTypeBankBranchName(updatePaymentByPaymentCodeReqDTO.getPaymentTypeBankBranchName());
            afterPayment.setPaymentTypeBankAccount(updatePaymentByPaymentCodeReqDTO.getPaymentTypeBankAccount());
            afterPayment.setPaymentTypeBankRealname(updatePaymentByPaymentCodeReqDTO.getPaymentTypeBankRealname());

        } else if (updatePaymentByPaymentCodeReqDTO.getPaymentType() == PaymentTypeEnum.CASH.getCode()) {

        } else {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }
        afterPayment.setRemark(updatePaymentByPaymentCodeReqDTO.getRemark());
        //更新到数据库
        paymentService.update(afterPayment);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Payment", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> enablePayment(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        String paymentCode = getPaymentByPaymentCodeReqDTO.getPaymentCode();
        //加载
        PaymentRespDTO afterPaymentRespDTO = paymentService.getPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO).getData();
        if (afterPaymentRespDTO == null) {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }
        Payment afterPayment = ModelMapperUtil.map(afterPaymentRespDTO, Payment.class);

        //已启用状态无需启用
        if (afterPayment.getStatus() == PaymentStatusEnum.ENABLE.getCode()) {
            return new Result<>(ResultCodeEnum.PAYMENT_STATUS_ENABLE);
        }

        //TODO 用户启用的每种付款方式类型最多只能一种

        String username = afterPaymentRespDTO.getUsername();
        int paymentType = afterPaymentRespDTO.getPaymentType();
        GetPaymentByPaymentTypeForClientReqDTO getPaymentByPaymentTypeForClientReqDTO = new GetPaymentByPaymentTypeForClientReqDTO();
        getPaymentByPaymentTypeForClientReqDTO.setUsername(username);
        getPaymentByPaymentTypeForClientReqDTO.setPaymentType(paymentType);
        boolean b = paymentService.existsPaymentByPaymentTypeForClient(getPaymentByPaymentTypeForClientReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.PAYMENT_TYPE_EXISTS);
        }

        //初始化
        afterPayment.setStatus(PaymentStatusEnum.ENABLE.getCode());

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        if (currentLoginUser.getUsername().equals(afterPayment.getUsername())) {
            //创建者才有权限
            flag = true;
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //更新到数据库
        paymentService.update(afterPayment);

        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Payment", allEntries = true)
    @Override
    @SneakyThrows
    public Result<Void> disablePayment(GetPaymentByPaymentCodeReqDTO getPaymentByPaymentCodeReqDTO) {
        String paymentCode = getPaymentByPaymentCodeReqDTO.getPaymentCode();
        //加载
        PaymentRespDTO afterPaymentRespDTO = paymentService.getPaymentByPaymentCode(getPaymentByPaymentCodeReqDTO).getData();
        if (afterPaymentRespDTO == null) {
            return new Result<>(ResultCodeEnum.PAYMENT_NOT_EXISTS);
        }
        Payment afterPayment = ModelMapperUtil.map(afterPaymentRespDTO, Payment.class);

        //初始化
        afterPayment.setStatus(PaymentStatusEnum.DISABLE.getCode());

        //默认无权限
        boolean flag = false;
        //获取当前登陆用户详情
        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();

        if (currentLoginUser.getUsername().equals(afterPayment.getUsername())) {
            //创建者才有权限
            flag = true;
        }
        if (!flag) {
            //无权限，直接返回403
            return new Result<>(ResultCodeEnum.FORBIDDEN);
        }

        //更新到数据库
        paymentService.update(afterPayment);

        return new Result<>(ResultCodeEnum.OK);
    }

}