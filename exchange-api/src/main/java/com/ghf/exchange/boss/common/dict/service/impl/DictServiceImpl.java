package com.ghf.exchange.boss.common.dict.service.impl;

import com.ghf.exchange.boss.authorication.user.dto.UserRespDTO;
import com.ghf.exchange.boss.authorication.user.service.UserService;
import com.ghf.exchange.boss.common.dict.dto.*;
import com.ghf.exchange.boss.common.dict.entity.Dict;
import com.ghf.exchange.boss.common.dict.entity.QDict;
import com.ghf.exchange.boss.common.dict.enums.DictStatusEnum;
import com.ghf.exchange.boss.common.dict.repository.DictRepository;
import com.ghf.exchange.boss.common.dict.service.DictService;
import com.ghf.exchange.dto.PageRespDTO;
import com.ghf.exchange.dto.Result;
import com.ghf.exchange.enums.ResultCodeEnum;
import com.ghf.exchange.service.impl.BaseServiceImpl;
import com.ghf.exchange.util.IdUtil;
import com.ghf.exchange.util.ModelMapperUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
@Service
@Lazy
@Slf4j
public class DictServiceImpl extends BaseServiceImpl<Dict, Long> implements DictService {
    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private DictService dictService;

    public DictServiceImpl(DictRepository repository) {
        super(repository);
    }

    @Cacheable(cacheNames = "Dict", key = "'pageDict:'.concat(#p0.pageNum).concat(':').concat(#p0.pageSize).concat(':').concat(#p0.sort[0].property).concat(':').concat(#p0.sort[0].direction) ", condition = "T(org.springframework.util.StringUtils).isEmpty(#p0.dicttype)  && T(org.springframework.util.StringUtils).isEmpty(#p0.dictkey)   && #p0.sort!=null && #p0.sort.size()==1   ")
    @Override
    @SneakyThrows
    public Result<PageRespDTO<DictRespDTO>> pageDict(PageDictReqDTO pageDictReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(pageDictReqDTO.getDicttype())) {
            predicate.and(QDict.dict.dicttype.contains(pageDictReqDTO.getDicttype()));
        }
        if (!ObjectUtils.isEmpty(pageDictReqDTO.getDictkey())) {
            predicate.and(QDict.dict.dictkey.contains(pageDictReqDTO.getDictkey()));
        }
        PageRespDTO<DictRespDTO> pageRespDTO = this.page(predicate, pageDictReqDTO, DictRespDTO.class);
        return new Result<>(pageRespDTO);
    }

    @Cacheable(cacheNames = "Dict", key = "'listDictByDicttypeAndDictkey:'.concat(#p0.dicttype)")
    @Override
    @SneakyThrows
    public Result<List<DictRespDTO>> listDictByDicttype(ListDictByDicttypeReqDTO listDictByDicttypeReqDTO) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (!ObjectUtils.isEmpty(listDictByDicttypeReqDTO.getDicttype())) {
            predicate.and(QDict.dict.dicttype.eq(listDictByDicttypeReqDTO.getDicttype()));
        }
        List<DictRespDTO> list = this.list(predicate, DictRespDTO.class);
        return new Result<>(list);
    }

    @Override
    @SneakyThrows
    public Result<List<BatchListDictByDicttypesRespDTO>> batchListDictByDicttypes(BatchListDictByDicttypesReqDTO batchListDictByDicttypesReqDTO) {
        //??????????????????
        List<BatchListDictByDicttypesRespDTO> list = new ArrayList<>();

        //???????????????????????????
        List<String> dicttypeList = batchListDictByDicttypesReqDTO.getDicttypeList();
        for (String dicttype : dicttypeList) {
            ListDictByDicttypeReqDTO listDictByDicttypeReqDTO = new ListDictByDicttypeReqDTO();
            listDictByDicttypeReqDTO.setDicttype(dicttype);
            //??????????????????????????????????????????
            List<DictRespDTO> dictRespDTOList = dictService.listDictByDicttype(listDictByDicttypeReqDTO).getData();
            //??????????????????
            BatchListDictByDicttypesRespDTO batchListDictByDicttypesRespDTO = new BatchListDictByDicttypesRespDTO();
            batchListDictByDicttypesRespDTO.setDicttype(dicttype);
            batchListDictByDicttypesRespDTO.setDictRespDTOList(dictRespDTOList);
            list.add(batchListDictByDicttypesRespDTO);
        }
        return new Result<>(list);
    }

    @SneakyThrows
    private Result<Dict> getDictByDicttypeAndDictkey(String dicttype, String dictkey) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QDict.dict.dicttype.eq(dicttype));
        predicate.and(QDict.dict.dictkey.eq(dictkey));
        Dict dict = this.get(predicate);
        return new Result<>(dict);
    }

    @Cacheable(cacheNames = "Dict", key = "'getDictByDicttypeAndDictkey:'.concat(#p0.dicttype).concat(':').concat(#p0.dictkey)")
    @Override
    @SneakyThrows
    public Result<DictRespDTO> getDictByDicttypeAndDictkey(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        //TODO ????????????
        String dicttype = getDictByDicttypeAndDictkeyReqDTO.getDicttype();
        String dictkey = getDictByDicttypeAndDictkeyReqDTO.getDictkey();
        Dict dict = this.getDictByDicttypeAndDictkey(dicttype, dictkey).getData();
        //??????
        DictRespDTO dictRespDTO = ModelMapperUtil.map(dict, DictRespDTO.class);
        return new Result<>(dictRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<DictRespDTO> getDictByDicttypeAndDictvalue(GetDictByDicttypeAndDictvalueReqDTO getDictByDicttypeAndDictvalueReqDTO) {
        //TODO ????????????
        String dicttype = getDictByDicttypeAndDictvalueReqDTO.getDicttype();
        String dictvalue = getDictByDicttypeAndDictvalueReqDTO.getDictvalue();

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QDict.dict.dicttype.eq(dicttype));
        predicate.and(QDict.dict.dictvalue.eq(dictvalue));
        Dict dict = this.get(predicate);
        //??????
        DictRespDTO dictRespDTO = ModelMapperUtil.map(dict, DictRespDTO.class);
        return new Result<>(dictRespDTO);
    }

    @Override
    @SneakyThrows
    public Result<Boolean> existsDictByDicttypeAndDictkey(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        //TODO ????????????
        String dicttype = getDictByDicttypeAndDictkeyReqDTO.getDicttype();
        String dictkey = getDictByDicttypeAndDictkeyReqDTO.getDictkey();
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QDict.dict.dicttype.eq(dicttype));
        predicate.and(QDict.dict.dictkey.eq(dictkey));
        boolean b = this.exists(predicate);
        return new Result<>(b);
    }

    @CacheEvict(cacheNames = "Dict", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> addDict(AddDictReqDTO addDictReqDTO) {
        Dict dict = ModelMapperUtil.map(addDictReqDTO, Dict.class);
        //??????????????????????????????

        UserRespDTO currentLoginUser = userService.getCurrentLoginUser().getData();
        dict.setCreateUserId(currentLoginUser.getId());
        dict.setCreateUserName(currentLoginUser.getUsername());

        dict.setCreateTime(new Date());
        //???????????????
        String dicttype = dict.getDicttype();
        String dictkey = dict.getDictkey();
        GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO = new GetDictByDicttypeAndDictkeyReqDTO();
        getDictByDicttypeAndDictkeyReqDTO.setDicttype(dicttype);
        getDictByDicttypeAndDictkeyReqDTO.setDictkey(dictkey);
        boolean b = this.existsDictByDicttypeAndDictkey(getDictByDicttypeAndDictkeyReqDTO).getData();
        if (b) {
            return new Result<>(ResultCodeEnum.DICT_EXISTS);
        }
        //?????????id
        dict.setId(IdUtil.generateLongId());
        dict.setStatus(DictStatusEnum.ENABLE.getCode());
        //??????????????????
        this.add(dict);
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Dict", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> updateDictByDicttypeAndDictkey(UpdateDictByDicttypeAndDictkeyReqDTO updateDictByDicttypeAndDictkeyReqDTO) {
        String dicttype = updateDictByDicttypeAndDictkeyReqDTO.getDicttype();
        String dicttypedesc = updateDictByDicttypeAndDictkeyReqDTO.getDicttypedesc();
        String dictkey = updateDictByDicttypeAndDictkeyReqDTO.getDictkey();
        String dictvalue = updateDictByDicttypeAndDictkeyReqDTO.getDictvalue();
        int orderNum = updateDictByDicttypeAndDictkeyReqDTO.getOrderNum();
        //??????
        Dict afterDict = this.getDictByDicttypeAndDictkey(dicttype, dictkey).getData();
        if (afterDict == null) {
            return new Result<>(ResultCodeEnum.DICT_NOT_EXISTS);
        }
        //?????????
        afterDict.setDicttypedesc(dicttypedesc);
        afterDict.setDictvalue(dictvalue);
        afterDict.setOrderNum(orderNum);
        //??????????????????
        this.update(afterDict);
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Dict", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> enableDict(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        String dicttype = getDictByDicttypeAndDictkeyReqDTO.getDicttype();
        String dictkey = getDictByDicttypeAndDictkeyReqDTO.getDictkey();
        //??????
        Dict afterDict = this.getDictByDicttypeAndDictkey(dicttype, dictkey).getData();
        if (afterDict == null) {
            return new Result<>(ResultCodeEnum.DICT_NOT_EXISTS);
        }
        //?????????
        afterDict.setStatus(DictStatusEnum.ENABLE.getCode());
        //??????????????????
        this.update(afterDict);
        return new Result<>(ResultCodeEnum.OK);
    }

    @CacheEvict(cacheNames = "Dict", allEntries = true)

    @Override
    @SneakyThrows
    public Result<Void> disableDict(GetDictByDicttypeAndDictkeyReqDTO getDictByDicttypeAndDictkeyReqDTO) {
        String dicttype = getDictByDicttypeAndDictkeyReqDTO.getDicttype();
        String dictkey = getDictByDicttypeAndDictkeyReqDTO.getDictkey();
        //??????
        Dict afterDict = this.getDictByDicttypeAndDictkey(dicttype, dictkey).getData();
        if (afterDict == null) {
            return new Result<>(ResultCodeEnum.DICT_NOT_EXISTS);
        }
        //?????????
        afterDict.setStatus(DictStatusEnum.DISABLE.getCode());
        //??????????????????
        this.update(afterDict);
        return new Result<>(ResultCodeEnum.OK);
    }
}